package tn.mondelys.backend.dto;

import tn.mondelys.backend.model.Reservation;
import tn.mondelys.backend.model.ReservationStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ReservationDtos {

    public static class CreateReservationRequest {
        @NotBlank
        @Size(max = 120)
        private String firstName;

        @NotBlank
        @Size(max = 120)
        private String lastName;

        @NotBlank
        @Email
        @Size(max = 160)
        private String email;

        @NotBlank
        @Size(max = 40)
        private String phone;

        @NotNull
        @FutureOrPresent
        private LocalDate reservationDate;

        @NotNull
        private LocalTime reservationTime;

        @NotNull
        @Min(1)
        @Max(20)
        private Integer guestsCount;

        @Size(max = 180)
        private String occasion;

        @Size(max = 220)
        private String preorder;

        @Size(max = 1500)
        private String specialRequests;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public LocalDate getReservationDate() {
            return reservationDate;
        }

        public void setReservationDate(LocalDate reservationDate) {
            this.reservationDate = reservationDate;
        }

        public LocalTime getReservationTime() {
            return reservationTime;
        }

        public void setReservationTime(LocalTime reservationTime) {
            this.reservationTime = reservationTime;
        }

        public Integer getGuestsCount() {
            return guestsCount;
        }

        public void setGuestsCount(Integer guestsCount) {
            this.guestsCount = guestsCount;
        }

        public String getOccasion() {
            return occasion;
        }

        public void setOccasion(String occasion) {
            this.occasion = occasion;
        }

        public String getPreorder() {
            return preorder;
        }

        public void setPreorder(String preorder) {
            this.preorder = preorder;
        }

        public String getSpecialRequests() {
            return specialRequests;
        }

        public void setSpecialRequests(String specialRequests) {
            this.specialRequests = specialRequests;
        }
    }

    public static class ReservationResponse {
        private Long id;
        private String referenceCode;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private LocalDate reservationDate;
        private LocalTime reservationTime;
        private Integer guestsCount;
        private String occasion;
        private String preorder;
        private String specialRequests;
        private ReservationStatus status;
        private String adminNote;
        private String verifiedBy;
        private LocalDateTime verifiedAt;
        private LocalDateTime createdAt;

        public static ReservationResponse fromEntity(Reservation reservation) {
            ReservationResponse response = new ReservationResponse();
            response.id = reservation.getId();
            response.referenceCode = reservation.getReferenceCode();
            response.firstName = reservation.getFirstName();
            response.lastName = reservation.getLastName();
            response.email = reservation.getEmail();
            response.phone = reservation.getPhone();
            response.reservationDate = reservation.getReservationDate();
            response.reservationTime = reservation.getReservationTime();
            response.guestsCount = reservation.getGuestsCount();
            response.occasion = reservation.getOccasion();
            response.preorder = reservation.getPreorder();
            response.specialRequests = reservation.getSpecialRequests();
            response.status = reservation.getStatus();
            response.adminNote = reservation.getAdminNote();
            response.verifiedBy = reservation.getVerifiedBy();
            response.verifiedAt = reservation.getVerifiedAt();
            response.createdAt = reservation.getCreatedAt();
            return response;
        }

        public Long getId() {
            return id;
        }

        public String getReferenceCode() {
            return referenceCode;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public LocalDate getReservationDate() {
            return reservationDate;
        }

        public LocalTime getReservationTime() {
            return reservationTime;
        }

        public Integer getGuestsCount() {
            return guestsCount;
        }

        public String getOccasion() {
            return occasion;
        }

        public String getPreorder() {
            return preorder;
        }

        public String getSpecialRequests() {
            return specialRequests;
        }

        public ReservationStatus getStatus() {
            return status;
        }

        public String getAdminNote() {
            return adminNote;
        }

        public String getVerifiedBy() {
            return verifiedBy;
        }

        public LocalDateTime getVerifiedAt() {
            return verifiedAt;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }

    public static class UpdateStatusRequest {
        @NotNull
        private ReservationStatus status;

        @Size(max = 1200)
        private String adminNote;

        public ReservationStatus getStatus() {
            return status;
        }

        public void setStatus(ReservationStatus status) {
            this.status = status;
        }

        public String getAdminNote() {
            return adminNote;
        }

        public void setAdminNote(String adminNote) {
            this.adminNote = adminNote;
        }
    }

    public static class DashboardResponse {
        private long reservationsToday;
        private long pendingReservations;
        private long confirmedReservations;
        private long cancelledReservations;
        private long totalReservations;

        public DashboardResponse(long reservationsToday, long pendingReservations, long confirmedReservations,
                                 long cancelledReservations, long totalReservations) {
            this.reservationsToday = reservationsToday;
            this.pendingReservations = pendingReservations;
            this.confirmedReservations = confirmedReservations;
            this.cancelledReservations = cancelledReservations;
            this.totalReservations = totalReservations;
        }

        public long getReservationsToday() {
            return reservationsToday;
        }

        public long getPendingReservations() {
            return pendingReservations;
        }

        public long getConfirmedReservations() {
            return confirmedReservations;
        }

        public long getCancelledReservations() {
            return cancelledReservations;
        }

        public long getTotalReservations() {
            return totalReservations;
        }
    }
}
