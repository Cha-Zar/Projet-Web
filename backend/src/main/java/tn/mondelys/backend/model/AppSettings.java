package tn.mondelys.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "app_settings")
public class AppSettings {

    @Id
    private Long id = 1L;

    @Column(nullable = false, length = 140)
    private String restaurantName = "Mondélys";

    @Column(nullable = false, length = 40)
    private String publicPhone = "+216 71 234 567";

    @Column(nullable = false, length = 180)
    private String publicEmail = "contact@mondelys.tn";

    @Column(nullable = false, length = 220)
    private String address = "15 Avenue Habib Bourguiba, Tunis";

    @Column(nullable = false, length = 1200)
    private String description = "Restaurant tunisien de gastronomie authentique. Saveurs du terroir, héritage berbère et méditerranéen.";

    @Column(nullable = false, length = 80)
    private String lunchWeek = "12:00 – 15:00";

    @Column(nullable = false, length = 80)
    private String dinnerWeek = "19:00 – 23:00";

    @Column(nullable = false, length = 80)
    private String lunchSunday = "12:00 – 15:30";

    @Column(nullable = false, length = 80)
    private String dinnerSunday = "Fermé";

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public AppSettings() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
