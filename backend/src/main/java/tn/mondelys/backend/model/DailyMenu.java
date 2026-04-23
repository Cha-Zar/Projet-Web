package tn.mondelys.backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "daily_menus")
public class DailyMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_date", nullable = false, unique = true)
    private LocalDate menuDate;

    @Column(name = "chef_note", length = 2000)
    private String chefNote;

    @Column(name = "dish1_name", length = 180)
    private String dish1Name;

    @Column(name = "dish1_description", length = 1200)
    private String dish1Description;

    @Column(name = "dish1_price", length = 40)
    private String dish1Price;

    @Column(name = "dish1_origin", length = 120)
    private String dish1Origin;

    @Column(name = "dish2_name", length = 180)
    private String dish2Name;

    @Column(name = "dish2_description", length = 1200)
    private String dish2Description;

    @Column(name = "dish2_price", length = 40)
    private String dish2Price;

    @Column(name = "dish2_origin", length = 120)
    private String dish2Origin;

    @Column(name = "dish3_name", length = 180)
    private String dish3Name;

    @Column(name = "dish3_description", length = 1200)
    private String dish3Description;

    @Column(name = "dish3_price", length = 40)
    private String dish3Price;

    @Column(name = "dish3_origin", length = 120)
    private String dish3Origin;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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

    public LocalDate getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(LocalDate menuDate) {
        this.menuDate = menuDate;
    }

    public String getChefNote() {
        return chefNote;
    }

    public void setChefNote(String chefNote) {
        this.chefNote = chefNote;
    }

    public String getDish1Name() {
        return dish1Name;
    }

    public void setDish1Name(String dish1Name) {
        this.dish1Name = dish1Name;
    }

    public String getDish1Description() {
        return dish1Description;
    }

    public void setDish1Description(String dish1Description) {
        this.dish1Description = dish1Description;
    }

    public String getDish1Price() {
        return dish1Price;
    }

    public void setDish1Price(String dish1Price) {
        this.dish1Price = dish1Price;
    }

    public String getDish1Origin() {
        return dish1Origin;
    }

    public void setDish1Origin(String dish1Origin) {
        this.dish1Origin = dish1Origin;
    }

    public String getDish2Name() {
        return dish2Name;
    }

    public void setDish2Name(String dish2Name) {
        this.dish2Name = dish2Name;
    }

    public String getDish2Description() {
        return dish2Description;
    }

    public void setDish2Description(String dish2Description) {
        this.dish2Description = dish2Description;
    }

    public String getDish2Price() {
        return dish2Price;
    }

    public void setDish2Price(String dish2Price) {
        this.dish2Price = dish2Price;
    }

    public String getDish2Origin() {
        return dish2Origin;
    }

    public void setDish2Origin(String dish2Origin) {
        this.dish2Origin = dish2Origin;
    }

    public String getDish3Name() {
        return dish3Name;
    }

    public void setDish3Name(String dish3Name) {
        this.dish3Name = dish3Name;
    }

    public String getDish3Description() {
        return dish3Description;
    }

    public void setDish3Description(String dish3Description) {
        this.dish3Description = dish3Description;
    }

    public String getDish3Price() {
        return dish3Price;
    }

    public void setDish3Price(String dish3Price) {
        this.dish3Price = dish3Price;
    }

    public String getDish3Origin() {
        return dish3Origin;
    }

    public void setDish3Origin(String dish3Origin) {
        this.dish3Origin = dish3Origin;
    }
}
