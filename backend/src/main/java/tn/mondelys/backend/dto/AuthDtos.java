package tn.mondelys.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class AuthDtos {

    public static class LoginRequest {
        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class LoginResponse {
        private String token;
        private String fullName;
        private String role;

        public LoginResponse(String token, String fullName, String role) {
            this.token = token;
            this.fullName = fullName;
            this.role = role;
        }

        public String getToken() {
            return token;
        }

        public String getFullName() {
            return fullName;
        }

        public String getRole() {
            return role;
        }
    }

    public static class SessionResponse {
        private String email;
        private String fullName;
        private String role;

        public SessionResponse(String email, String fullName, String role) {
            this.email = email;
            this.fullName = fullName;
            this.role = role;
        }

        public String getEmail() {
            return email;
        }

        public String getFullName() {
            return fullName;
        }

        public String getRole() {
            return role;
        }
    }
}
