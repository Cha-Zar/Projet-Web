package tn.mondelys.backend.config;

import tn.mondelys.backend.security.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_SITE_ROUTES = {
            "/",
            "/index.html",
            "/menu.html",
            "/reservation.html",
            "/contact.html",
            "/style.css",
            "/styleAdmin.css",
            "/main.js",
            "/menuPage.js",
            "/mondelys-utils.js",
            "/publicSettings.js",
            "/admin",
            "/adminLogin.html",
            "/adminLogin.js",
            "/images/**",
            "/favicon.ico",
            "/api/reservations",
            "/api/settings/public",
            "/api/menu",
            "/api/daily-menu",
            "/api/reviews",
            "/api/reviews/public",
            "/api/contact-messages",
            "/api/admin/auth/login",
            "/api/admin/auth/logout"
    };

    private static final String[] PROTECTED_ADMIN_PAGES = {
            "/DashboradAdmin.html",
            "/resAdmin.html",
            "/reviwAdmin.html",
            "/contactAdmin.html",
            "/menuAdmin.html",
            "/settingsAdmin.html",
            "/adminApi.js",
            "/navAdmin.js",
            "/menuAdmin.js"
    };

    private static final String[] DENIED_FILE_PATTERNS = {
            "/backend/**",
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> handleUnauthorized(request, response))
                        .accessDeniedHandler((request, response, accessDeniedException) -> handleForbidden(request, response))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(DENIED_FILE_PATTERNS).denyAll()
                        .requestMatchers(PUBLIC_SITE_ROUTES).permitAll()
                        .requestMatchers(PROTECTED_ADMIN_PAGES).hasRole("ADMIN")
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PATCH", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private void handleUnauthorized(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        SecurityContextHolder.clearContext();

        if (isApiRequest(request)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        if (isProtectedAdminPageRequest(request)) {
            response.sendRedirect("/admin");
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private void handleForbidden(HttpServletRequest request, HttpServletResponse response) throws java.io.IOException {
        if (isApiRequest(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (isProtectedAdminPageRequest(request)) {
            response.sendRedirect("/admin");
            return;
        }

        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }

    private boolean isProtectedAdminPageRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.equals("/DashboradAdmin.html")
                || path.equals("/resAdmin.html")
                || path.equals("/reviwAdmin.html")
                || path.equals("/contactAdmin.html")
                || path.equals("/menuAdmin.html")
                || path.equals("/settingsAdmin.html")
                || path.equals("/adminApi.js")
                || path.equals("/navAdmin.js")
                || path.equals("/menuAdmin.js");
    }
}
