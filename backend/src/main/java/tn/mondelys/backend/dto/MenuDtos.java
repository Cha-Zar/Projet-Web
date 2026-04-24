package tn.mondelys.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tn.mondelys.backend.model.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MenuDtos {

    private static final String BADGE_SEPARATOR = "|";

    public static class UpsertMenuItemRequest {
        @NotBlank
        @Size(max = 180)
        private String name;

        @NotBlank
        @Size(max = 40)
        private String cuisine;

        @Size(max = 160)
        private String location;

        @NotBlank
        @Size(max = 160)
        private String price;

        @Size(max = 80)
        private String pieces;

        @NotBlank
        @Size(max = 2000)
        private String description;

        @NotBlank
        @Size(max = 600)
        private String imageUrl;

        private List<@Size(max = 60) String> badges = new ArrayList<>();

        @Min(0)
        private Integer displayOrder;

        private Boolean active = true;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCuisine() {
            return cuisine;
        }

        public void setCuisine(String cuisine) {
            this.cuisine = cuisine;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getPrice() {
            return price;
        }

        public void setPrice(String price) {
            this.price = price;
        }

        public String getPieces() {
            return pieces;
        }

        public void setPieces(String pieces) {
            this.pieces = pieces;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public List<String> getBadges() {
            return badges;
        }

        public void setBadges(List<String> badges) {
            this.badges = badges;
        }

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public void setDisplayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
        }

        public Boolean getActive() {
            return active;
        }

        public void setActive(Boolean active) {
            this.active = active;
        }
    }

    public static class MenuItemResponse {
        private Long id;
        private String name;
        private String cuisine;
        private String cuisineLabel;
        private String location;
        private String price;
        private String pieces;
        private String description;
        private List<String> badges;
        private String imageUrl;
        private Integer displayOrder;
        private boolean active;
        private String createdAt;
        private String updatedAt;

        public static MenuItemResponse from(MenuItem item) {
            MenuItemResponse response = new MenuItemResponse();
            response.id = item.getId();
            response.name = item.getName();
            response.cuisine = item.getCuisine();
            response.cuisineLabel = cuisineLabel(item.getCuisine());
            response.location = item.getLocation();
            response.price = item.getPrice();
            response.pieces = item.getPieces();
            response.description = item.getDescription();
            response.badges = decodeBadges(item.getBadgeLabels());
            response.imageUrl = item.getImageUrl();
            response.displayOrder = item.getDisplayOrder();
            response.active = item.isActive();
            response.createdAt = item.getCreatedAt() != null ? item.getCreatedAt().toString() : null;
            response.updatedAt = item.getUpdatedAt() != null ? item.getUpdatedAt().toString() : null;
            return response;
        }

        public Long getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getCuisine() {
            return cuisine;
        }

        public String getCuisineLabel() {
            return cuisineLabel;
        }

        public String getLocation() {
            return location;
        }

        public String getPrice() {
            return price;
        }

        public String getPieces() {
            return pieces;
        }

        public String getDescription() {
            return description;
        }

        public List<String> getBadges() {
            return badges;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public Integer getDisplayOrder() {
            return displayOrder;
        }

        public boolean isActive() {
            return active;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }
    }

    public static List<String> decodeBadges(String rawBadges) {
        if (rawBadges == null || rawBadges.isBlank()) {
            return List.of();
        }

        return Arrays.stream(rawBadges.split("\\Q" + BADGE_SEPARATOR + "\\E"))
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toList();
    }

    public static String encodeBadges(List<String> badges) {
        if (badges == null || badges.isEmpty()) {
            return null;
        }

        return badges.stream()
                .map(value -> value == null ? "" : value.trim())
                .filter(value -> !value.isEmpty())
                .distinct()
                .reduce((left, right) -> left + BADGE_SEPARATOR + right)
                .orElse(null);
    }

    public static String cuisineLabel(String cuisine) {
        return switch (cuisine) {
            case "JAPON" -> "Japon";
            case "MEXIQUE" -> "Mexique";
            case "ITALIE" -> "Italie";
            case "ORIENTALE" -> "Orientale";
            case "DESSERTS" -> "Desserts";
            default -> cuisine;
        };
    }
}
