package tn.mondelys.backend.service;

import tn.mondelys.backend.dto.AuthDtos;
import tn.mondelys.backend.model.Admin;
import tn.mondelys.backend.repository.AdminRepository;
import tn.mondelys.backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AdminAuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AdminAuthService(AdminRepository adminRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthDtos.LoginResponse login(AuthDtos.LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase(Locale.ROOT);

        Admin admin = adminRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Email ou mot de passe invalide"));

        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new IllegalArgumentException("Email ou mot de passe invalide");
        }

        String token = jwtService.generateToken(admin.getId(), admin.getEmail(), admin.getRole());
        return new AuthDtos.LoginResponse(token, admin.getFullName(), admin.getRole());
    }

    public AuthDtos.SessionResponse getSession(String email) {
        Admin admin = adminRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Session administrateur introuvable"));

        return new AuthDtos.SessionResponse(admin.getEmail(), admin.getFullName(), admin.getRole());
    }
}
