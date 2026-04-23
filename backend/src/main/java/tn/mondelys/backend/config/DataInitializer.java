package tn.mondelys.backend.config;

import tn.mondelys.backend.model.Admin;
import tn.mondelys.backend.model.AppSettings;
import tn.mondelys.backend.repository.AdminRepository;
import tn.mondelys.backend.repository.AppSettingsRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final AppSettingsRepository appSettingsRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(AdminRepository adminRepository, AppSettingsRepository appSettingsRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.appSettingsRepository = appSettingsRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        adminRepository.findByEmailIgnoreCase("admin@mondelys.tn").orElseGet(() -> {
            Admin admin = new Admin();
            admin.setEmail("admin@mondelys.tn");
            admin.setPasswordHash(passwordEncoder.encode("Admin2026!"));
            admin.setFullName("Mondelys Admin");
            admin.setRole("ADMIN");
            return adminRepository.save(admin);
        });

        appSettingsRepository.findById(1L).orElseGet(() -> {
            AppSettings settings = new AppSettings();
            settings.setId(1L);
            return appSettingsRepository.save(settings);
        });
    }
}
