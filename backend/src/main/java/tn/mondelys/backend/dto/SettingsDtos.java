package tn.mondelys.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tn.mondelys.backend.model.AppSettings;

public class SettingsDtos {

    public static class UpdateSettingsRequest {
        @NotBlank
        @Size(max = 140)
        private String restaurantName;

        @NotBlank
        @Size(max = 40)
        private String publicPhone;

        @NotBlank
        @Email
        @Size(max = 180)
        private String publicEmail;

        @NotBlank
        @Size(max = 220)
        private String address;

        @NotBlank
        @Size(max = 1200)
        private String description;

        @NotBlank
        @Size(max = 80)
        private String lunchWeek;

        @NotBlank
        @Size(max = 80)
        private String dinnerWeek;

        @NotBlank
        @Size(max = 80)
        private String lunchSunday;

        @NotBlank
        @Size(max = 80)
        private String dinnerSunday;

        public String getRestaurantName() {
            return restaurantName;
        }

        public void setRestaurantName(String restaurantName) {
            this.restaurantName = restaurantName;
        }

        public String getPublicPhone() {
            return publicPhone;
        }

        public void setPublicPhone(String publicPhone) {
            this.publicPhone = publicPhone;
        }

        public String getPublicEmail() {
            return publicEmail;
        }

        public void setPublicEmail(String publicEmail) {
            this.publicEmail = publicEmail;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getLunchWeek() {
            return lunchWeek;
        }

        public void setLunchWeek(String lunchWeek) {
            this.lunchWeek = lunchWeek;
        }

        public String getDinnerWeek() {
            return dinnerWeek;
        }

        public void setDinnerWeek(String dinnerWeek) {
            this.dinnerWeek = dinnerWeek;
        }

        public String getLunchSunday() {
            return lunchSunday;
        }

        public void setLunchSunday(String lunchSunday) {
            this.lunchSunday = lunchSunday;
        }

        public String getDinnerSunday() {
            return dinnerSunday;
        }

        public void setDinnerSunday(String dinnerSunday) {
            this.dinnerSunday = dinnerSunday;
        }
    }

    public static class SettingsResponse {
        private String restaurantName;
        private String publicPhone;
        private String publicEmail;
        private String address;
        private String description;
        private String lunchWeek;
        private String dinnerWeek;
        private String lunchSunday;
        private String dinnerSunday;

        public static SettingsResponse from(AppSettings settings) {
            SettingsResponse response = new SettingsResponse();
            response.restaurantName = settings.getRestaurantName();
            response.publicPhone = settings.getPublicPhone();
            response.publicEmail = settings.getPublicEmail();
            response.address = settings.getAddress();
            response.description = settings.getDescription();
            response.lunchWeek = settings.getLunchWeek();
            response.dinnerWeek = settings.getDinnerWeek();
            response.lunchSunday = settings.getLunchSunday();
            response.dinnerSunday = settings.getDinnerSunday();
            return response;
        }

        public String getRestaurantName() {
            return restaurantName;
        }

        public String getPublicPhone() {
            return publicPhone;
        }

        public String getPublicEmail() {
            return publicEmail;
        }

        public String getAddress() {
            return address;
        }

        public String getDescription() {
            return description;
        }

        public String getLunchWeek() {
            return lunchWeek;
        }

        public String getDinnerWeek() {
            return dinnerWeek;
        }

        public String getLunchSunday() {
            return lunchSunday;
        }

        public String getDinnerSunday() {
            return dinnerSunday;
        }
    }
}
