package tn.mondelys.backend.controller;

import tn.mondelys.backend.dto.AuthDtos;
import tn.mondelys.backend.service.AdminAuthService;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/admin/auth")
public class AuthController {

    private static final String AUTH_COOKIE_NAME = "adminToken";
    private static final Duration AUTH_COOKIE_TTL = Duration.ofDays(1);

    private final AdminAuthService adminAuthService;

    public AuthController(AdminAuthService adminAuthService) {
        this.adminAuthService = adminAuthService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthDtos.LoginResponse> login(
            @Valid @RequestBody AuthDtos.LoginRequest request,
            HttpServletRequest httpServletRequest,
            HttpServletResponse response
    ) {
        AuthDtos.LoginResponse loginResponse = adminAuthService.login(request);
        response.addHeader(HttpHeaders.SET_COOKIE, buildAuthCookie(loginResponse.getToken(), AUTH_COOKIE_TTL, httpServletRequest.isSecure()).toString());

        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        response.addHeader(HttpHeaders.SET_COOKIE, buildAuthCookie("", Duration.ZERO, request.isSecure()).toString());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/session")
    public ResponseEntity<AuthDtos.SessionResponse> session(Authentication authentication) {
        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(adminAuthService.getSession(authentication.getName()));
    }

    private ResponseCookie buildAuthCookie(String token, Duration maxAge, boolean secure) {
        return ResponseCookie.from(AUTH_COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secure)
                .sameSite("Strict")
                .path("/")
                .maxAge(maxAge)
                .build();
    }
}
