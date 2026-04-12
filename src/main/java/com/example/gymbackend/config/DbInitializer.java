package com.example.gymbackend.config;

import com.example.gymbackend.model.*;
import com.example.gymbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DbInitializer implements CommandLineRunner {

    private final SystemAccountRepository systemAccountRepository;
    private final BranchRepository branchRepository;
    private final MembershipPlanRepository planRepository;
    private final UserRepository userRepository;
    private final MembershipTransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (systemAccountRepository.count() == 0) {
            SystemAccount admin = new SystemAccount();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN_TI);
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            systemAccountRepository.save(admin);

            SystemAccount gymAdmin = new SystemAccount();
            gymAdmin.setUsername("gerente");
            gymAdmin.setPasswordHash(passwordEncoder.encode("gerente123"));
            gymAdmin.setRole(Role.ADMIN_GYM);
            gymAdmin.setFirstName("Gym");
            gymAdmin.setLastName("Manager");
            systemAccountRepository.save(gymAdmin);

            SystemAccount employee = new SystemAccount();
            employee.setUsername("empleado");
            employee.setPasswordHash(passwordEncoder.encode("empleado123"));
            employee.setRole(Role.EMPLOYEE);
            employee.setFirstName("Recepcionista");
            employee.setLastName("Turno 1");
            systemAccountRepository.save(employee);
        }

        Branch branchHQ = null;
        if (branchRepository.count() == 0) {
            branchHQ = new Branch();
            branchHQ.setName("Sede Principal Centro");
            branchHQ.setTimezone("America/Bogota");
            branchHQ = branchRepository.save(branchHQ);
        } else {
            branchHQ = branchRepository.findAll().get(0);
        }

        List<MembershipPlan> createdPlans = null;
        if (planRepository.count() == 0) {
            MembershipPlan p1 = new MembershipPlan();
            p1.setName("Día Suelto");
            p1.setDescription("Pase diario para visitantes.");
            p1.setDurationDays(1);
            p1.setPriceAmount(5.0);
            p1.setIsPromotion(false);

            MembershipPlan p2 = new MembershipPlan();
            p2.setName("Mensualidad Básica");
            p2.setDescription("Pase estándar de 30 días, gimnasio general.");
            p2.setDurationDays(30);
            p2.setPriceAmount(35.0);
            p2.setIsPromotion(false);

            MembershipPlan p3 = new MembershipPlan();
            p3.setName("Trimestral VIP");
            p3.setDescription("Promo de 90 días con descuento.");
            p3.setDurationDays(90);
            p3.setPriceAmount(80.0);
            p3.setIsPromotion(true);

            createdPlans = planRepository.saveAll(Arrays.asList(p1, p2, p3));
        } else {
            createdPlans = planRepository.findAll();
        }

        // Mock System Data Generation for Demo Graphic Aesthetics
        if (userRepository.count() == 0 && branchHQ != null && createdPlans.size() > 0) {
            Random random = new Random();
            for (int i = 1; i <= 20; i++) {
                User u = new User();
                u.setFullName("Cliente Mock " + i);
                u.setDocumentId("1000" + i);
                u.setEmail("cliente" + i + "@demo.com");
                u.setPinZkteco(2000 + i);
                u.setHomeBranch(branchHQ);
                // Assign black silhouette placeholder
                u.setProfileImageUrl("https://res.cloudinary.com/demo/image/upload/v1312461204/sample.jpg");
                User savedUser = userRepository.save(u);

                // Create random transactions backwards for all of 2026 and 2025
                int monthTx = random.nextInt(12) + 1;
                MembershipPlan randomPlan = createdPlans.get(random.nextInt(createdPlans.size()));
                
                MembershipTransaction txYear2026 = new MembershipTransaction();
                txYear2026.setUser(savedUser);
                txYear2026.setBranch(branchHQ);
                txYear2026.setPlan(randomPlan);
                txYear2026.setAmountPaid(randomPlan.getPriceAmount());
                txYear2026.setTransactionDate(LocalDateTime.of(2026, monthTx, 1, 10, 0));
                
                int monthTx2 = random.nextInt(12) + 1;
                MembershipPlan randomPlan2 = createdPlans.get(random.nextInt(createdPlans.size()));
                MembershipTransaction txYear2025 = new MembershipTransaction();
                txYear2025.setUser(savedUser);
                txYear2025.setBranch(branchHQ);
                txYear2025.setPlan(randomPlan2);
                txYear2025.setAmountPaid(randomPlan2.getPriceAmount());
                txYear2025.setTransactionDate(LocalDateTime.of(2025, monthTx2, 1, 10, 0));

                transactionRepository.saveAll(Arrays.asList(txYear2026, txYear2025));
            }
            System.out.println("✅ DataInitializer: Mock Users and Realistic Dashboard Transactions inserted.");
        }
    }
}
