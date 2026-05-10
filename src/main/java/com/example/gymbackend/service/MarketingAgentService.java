package com.example.gymbackend.service;

import com.example.gymbackend.model.MarketingProposal;
import com.example.gymbackend.model.Membership;
import com.example.gymbackend.repository.CustomerRepository;
import com.example.gymbackend.repository.MarketingProposalRepository;
import com.example.gymbackend.repository.MembershipPlanRepository;
import com.example.gymbackend.repository.MembershipRepository;
import com.example.gymbackend.repository.MembershipTransactionRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarketingAgentService {

    private final CustomerRepository customerRepository;
    private final MembershipRepository membershipRepository;
    private final MembershipTransactionRepository transactionRepository;
    private final MembershipPlanRepository planRepository;
    private final MarketingProposalRepository proposalRepository;

    @Value("${gemini.api-key:}")
    private String geminiApiKey;

    private static final String GEMINI_URL =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=";

    // ─── Scheduler: runs every day at 6:00 AM Ecuador time ──────────────────
    @Scheduled(cron = "0 0 6 * * *", zone = "America/Guayaquil")
    public void scheduledDailyAnalysis() {
        log.info("🤖 Iniciando análisis diario de marketing con Gemini...");
        try {
            runDailyAnalysis();
        } catch (Exception e) {
            log.error("Error en análisis diario de marketing: {}", e.getMessage());
        }
    }

    // ─── Manual trigger (also called from controller) ────────────────────────
    public String runDailyAnalysis() {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            return "❌ GEMINI_API_KEY no configurada. Agrega la variable de entorno en el servidor.";
        }

        // 1. Recopilar datos reales del ERP
        LocalDate today = LocalDate.now();
        LocalDate in10Days = today.plusDays(10);
        LocalDate ago30Days = today.minusDays(30);

        long totalActiveCustomers = customerRepository.findAll().stream()
                .filter(c -> "ACTIVE".equals(c.getStatus()))
                .count();

        List<Membership> allMemberships = membershipRepository.findAll();

        long activeMemberships = allMemberships.stream()
                .filter(m -> "ACTIVE".equals(m.getStatus()) && m.getEndDate() != null && !m.getEndDate().isBefore(today))
                .count();

        List<Membership> expiringNext10 = membershipRepository.findMembershipsExpiringBetween(today, in10Days);

        long recentlyExpired = allMemberships.stream()
                .filter(m -> m.getEndDate() != null
                        && m.getEndDate().isBefore(today)
                        && m.getEndDate().isAfter(ago30Days))
                .count();

        // Ingresos de los últimos 90 días
        int currentYear = today.getYear();
        List<Object[]> financialStats = transactionRepository.findFinancialStatsByYear(currentYear);
        double totalRevenueYear = financialStats.stream()
                .mapToDouble(row -> ((Number) row[1]).doubleValue())
                .sum();

        // Plan más vendido
        String topPlanName = planRepository.findAll().stream().findFirst()
                .map(p -> p.getName()).orElse("Sin datos");

        long totalTransactions = transactionRepository.count();

        // Nombres de clientes en riesgo (por vencer)
        String atRiskNames = expiringNext10.stream()
                .limit(5)
                .map(m -> m.getCustomer() != null ? m.getCustomer().getFullName() : "Cliente")
                .collect(Collectors.joining(", "));

        // 2. Construir el prompt para Gemini
        String prompt = buildPrompt(
                today.toString(),
                totalActiveCustomers,
                activeMemberships,
                expiringNext10.size(),
                recentlyExpired,
                totalRevenueYear,
                totalTransactions,
                topPlanName,
                atRiskNames
        );

        // 3. Llamar a la API de Gemini
        String geminiResponse = callGemini(prompt);
        if (geminiResponse == null) {
            return lastGeminiError != null ? lastGeminiError : "Error al contactar la API de Gemini.";
        }

        // 4. Parsear y guardar propuestas
        int saved = parseAndSaveProposals(geminiResponse);
        String result = String.format("✅ Análisis completado. %d propuestas generadas y guardadas.", saved);
        log.info(result);
        return result;
    }

    // ─── Construir prompt con datos reales ───────────────────────────────────
    private String buildPrompt(String fecha, long clientes, long activas, int porVencer,
                                long vencidas, double ingresos, long transacciones,
                                String topPlan, String enRiesgo) {
        return String.format("""
                Eres el agente de marketing de Friends Fitness, un gimnasio en Ecuador.
                Analiza estos datos reales del negocio y genera exactamente 4 propuestas
                de marketing o retención. Sé específico y práctico para un negocio local pequeño.

                DATOS REALES (Fecha: %s):
                - Clientes activos: %d
                - Membresías vigentes: %d
                - Membresías por vencer en los próximos 10 días: %d (clientes: %s)
                - Membresías vencidas sin renovar (último mes): %d
                - Ingresos acumulados del año: $%.2f
                - Total de transacciones históricas: %d
                - Plan más popular: %s

                Responde ÚNICAMENTE con un array JSON válido, sin texto adicional, sin markdown:
                [
                  {
                    "titulo": "Título corto de la propuesta",
                    "tipo": "promocion|campaña_marketing|consejo_retencion|estrategia",
                    "descripcion": "Descripción detallada de qué hacer y por qué (2-3 oraciones)",
                    "acciones": "Pasos concretos a ejecutar separados por puntos y coma"
                  }
                ]
                """,
                fecha, clientes, activas, porVencer, enRiesgo.isBlank() ? "ninguno" : enRiesgo,
                vencidas, ingresos, transacciones, topPlan);
    }

    // ─── Llamada HTTP a Gemini 2.0 Flash ─────────────────────────────────────
    private String lastGeminiError = null;

    private String callGemini(String prompt) {
        lastGeminiError = null;
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String body = String.format("""
                    {
                      "contents": [{"parts": [{"text": %s}]}],
                      "generationConfig": {"responseMimeType": "application/json"}
                    }
                    """, new ObjectMapper().writeValueAsString(prompt));

            HttpEntity<String> request = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(
                    GEMINI_URL + geminiApiKey, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response.getBody());
            return root.path("candidates").get(0)
                       .path("content").path("parts").get(0)
                       .path("text").asText();

        } catch (HttpClientErrorException e) {
            // Google nos dice exactamente qué está mal (ej: clave inválida)
            String detail = e.getResponseBodyAsString();
            log.error("Error HTTP desde Gemini [{}]: {}", e.getStatusCode(), detail);
            lastGeminiError = "Error Gemini [" + e.getStatusCode() + "]: " + detail;
            return null;
        } catch (Exception e) {
            log.error("Error llamando a Gemini: {}", e.getMessage());
            lastGeminiError = "Error de red: " + e.getMessage();
            return null;
        }
    }

    // ─── Parsear JSON de Gemini y guardar en BD ───────────────────────────────
    private int parseAndSaveProposals(String jsonText) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<Map<String, String>> proposals = mapper.readValue(
                    jsonText, new TypeReference<List<Map<String, String>>>() {});

            LocalDateTime now = LocalDateTime.now();
            for (Map<String, String> p : proposals) {
                MarketingProposal proposal = new MarketingProposal();
                proposal.setTitulo(p.getOrDefault("titulo", "Sin título"));
                proposal.setTipo(p.getOrDefault("tipo", "estrategia"));
                proposal.setDescripcion(p.getOrDefault("descripcion", ""));
                proposal.setAcciones(p.getOrDefault("acciones", ""));
                proposal.setFechaGeneracion(now);
                proposal.setEstado("pendiente");
                proposal.setCreatedAt(now);
                proposalRepository.save(proposal);
            }
            return proposals.size();
        } catch (Exception e) {
            log.error("Error parseando respuesta de Gemini: {} | Respuesta: {}", e.getMessage(), jsonText);
            return 0;
        }
    }

    public List<MarketingProposal> getAllProposals() {
        return proposalRepository.findAllByOrderByFechaGeneracionDesc();
    }
}
