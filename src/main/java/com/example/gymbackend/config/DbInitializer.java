package com.example.gymbackend.config;

import com.example.gymbackend.model.*;
import com.example.gymbackend.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DbInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;
    private final MembershipPlanRepository planRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        // Seed default admin users
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPasswordHash(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN_TI);
            admin.setFirstName("Super");
            admin.setLastName("Admin");
            admin.setIsActive(true);
            userRepository.save(admin);

            User gymAdmin = new User();
            gymAdmin.setUsername("gerente");
            gymAdmin.setPasswordHash(passwordEncoder.encode("gerente123"));
            gymAdmin.setRole(Role.ADMIN_GYM);
            gymAdmin.setFirstName("Gym");
            gymAdmin.setLastName("Manager");
            gymAdmin.setIsActive(true);
            userRepository.save(gymAdmin);

            User employee = new User();
            employee.setUsername("empleado");
            employee.setPasswordHash(passwordEncoder.encode("empleado123"));
            employee.setRole(Role.EMPLOYEE);
            employee.setFirstName("Recepcionista");
            employee.setLastName("Turno 1");
            employee.setIsActive(true);
            userRepository.save(employee);

            System.out.println("✅ DbInitializer: Default system users created.");
        }

        // Seed default branch
        if (branchRepository.count() == 0) {
            Branch hq = new Branch();
            hq.setName("Sede Principal Centro");
            hq.setTimezone("America/Bogota");
            branchRepository.save(hq);
            System.out.println("✅ DbInitializer: Default branch created.");
        }

        // Seed membership plans
        if (planRepository.count() == 0) {
            MembershipPlan p1 = new MembershipPlan();
            p1.setName("Día Suelto");
            p1.setDescription("Pase diario para visitantes.");
            p1.setDurationDays(1);
            p1.setPriceAmount(5.0);
            p1.setIsPromotion(false);

            MembershipPlan p2 = new MembershipPlan();
            p2.setName("Mensualidad Básica");
            p2.setDescription("Pase estándar de 30 días.");
            p2.setDurationDays(30);
            p2.setPriceAmount(35.0);
            p2.setIsPromotion(false);

            MembershipPlan p3 = new MembershipPlan();
            p3.setName("Trimestral VIP");
            p3.setDescription("Promo de 90 días con descuento.");
            p3.setDurationDays(90);
            p3.setPriceAmount(80.0);
            p3.setIsPromotion(true);

            planRepository.saveAll(Arrays.asList(p1, p2, p3));
            System.out.println("✅ DbInitializer: Default membership plans created.");
        }
    }
}
