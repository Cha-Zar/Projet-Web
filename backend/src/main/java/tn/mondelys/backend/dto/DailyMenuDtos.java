package tn.mondelys.backend.dto;

import jakarta.validation.constraints.Size;
import tn.mondelys.backend.model.DailyMenu;

public class DailyMenuDtos {

    public static class UpsertDailyMenuRequest {
        @Size(max = 2000)
        private String chefNote;

        @Size(max = 180)
        private String dish1Name;

        @Size(max = 1200)
        private String dish1Description;

        @Size(max = 40)
        private String dish1Price;

        @Size(max = 120)
        private String dish1Origin;

        @Size(max = 180)
        private String dish2Name;

        @Size(max = 1200)
        private String dish2Description;

        @Size(max = 40)
        private String dish2Price;

        @Size(max = 120)
        private String dish2Origin;

        @Size(max = 180)
        private String dish3Name;

        @Size(max = 1200)
        private String dish3Description;

        @Size(max = 40)
        private String dish3Price;

        @Size(max = 120)
        private String dish3Origin;

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

    public static class DailyMenuResponse {
        private Long id;
        private String menuDate;
        private String chefNote;
        private String dish1Name;
        private String dish1Description;
        private String dish1Price;
        private String dish1Origin;
        private String dish2Name;
        private String dish2Description;
        private String dish2Price;
        private String dish2Origin;
        private String dish3Name;
        private String dish3Description;
        private String dish3Price;
        private String dish3Origin;

        public static DailyMenuResponse from(DailyMenu menu) {
            DailyMenuResponse response = new DailyMenuResponse();
            response.id = menu.getId();
            response.menuDate = menu.getMenuDate() != null ? menu.getMenuDate().toString() : null;
            response.chefNote = menu.getChefNote();
            response.dish1Name = menu.getDish1Name();
            response.dish1Description = menu.getDish1Description();
            response.dish1Price = menu.getDish1Price();
            response.dish1Origin = menu.getDish1Origin();
            response.dish2Name = menu.getDish2Name();
            response.dish2Description = menu.getDish2Description();
            response.dish2Price = menu.getDish2Price();
            response.dish2Origin = menu.getDish2Origin();
            response.dish3Name = menu.getDish3Name();
            response.dish3Description = menu.getDish3Description();
            response.dish3Price = menu.getDish3Price();
            response.dish3Origin = menu.getDish3Origin();
            return response;
        }

        public Long getId() {
            return id;
        }

        public String getMenuDate() {
            return menuDate;
        }

        public String getChefNote() {
            return chefNote;
        }

        public String getDish1Name() {
            return dish1Name;
        }

        public String getDish1Description() {
            return dish1Description;
        }

        public String getDish1Price() {
            return dish1Price;
        }

        public String getDish1Origin() {
            return dish1Origin;
        }

        public String getDish2Name() {
            return dish2Name;
        }

        public String getDish2Description() {
            return dish2Description;
        }

        public String getDish2Price() {
            return dish2Price;
        }

        public String getDish2Origin() {
            return dish2Origin;
        }

        public String getDish3Name() {
            return dish3Name;
        }

        public String getDish3Description() {
            return dish3Description;
        }

        public String getDish3Price() {
            return dish3Price;
        }

        public String getDish3Origin() {
            return dish3Origin;
        }
    }
}
