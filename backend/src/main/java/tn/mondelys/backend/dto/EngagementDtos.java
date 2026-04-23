package tn.mondelys.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import tn.mondelys.backend.model.ClientReview;
import tn.mondelys.backend.model.ContactMessage;

import java.time.LocalDateTime;
import java.util.List;

public class EngagementDtos {

    public static class CreateReviewRequest {
        @NotBlank
        @Size(max = 140)
        private String fullName;

        @NotBlank
        @Email
        @Size(max = 180)
        private String email;

        @Min(1)
        @Max(5)
        private int rating;

        @Size(max = 180)
        private String title;

        @NotBlank
        @Size(max = 1800)
        private String message;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public int getRating() {
            return rating;
        }

        public void setRating(int rating) {
            this.rating = rating;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ReviewResponseRequest {
        @NotBlank
        @Size(max = 1800)
        private String response;

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }

    public static class ContactMessageCreateRequest {
        @NotBlank
        @Size(max = 120)
        private String firstName;

        @NotBlank
        @Size(max = 120)
        private String lastName;

        @NotBlank
        @Email
        @Size(max = 180)
        private String email;

        @Size(max = 40)
        private String phone;

        @NotBlank
        @Size(max = 180)
        private String subject;

        @NotBlank
        @Size(max = 3000)
        private String message;

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

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class ContactResponseRequest {
        @NotBlank
        @Size(max = 3000)
        private String response;

        public String getResponse() {
            return response;
        }

        public void setResponse(String response) {
            this.response = response;
        }
    }

    public static class ReviewView {
        private Long id;
        private String fullName;
        private String email;
        private int rating;
        private String title;
        private String message;
        private boolean responded;
        private String responseMessage;
        private LocalDateTime createdAt;

        public static ReviewView from(ClientReview review) {
            ReviewView view = new ReviewView();
            view.id = review.getId();
            view.fullName = review.getFullName();
            view.email = review.getEmail();
            view.rating = review.getRating();
            view.title = review.getTitle();
            view.message = review.getMessage();
            view.responded = review.isResponded();
            view.responseMessage = review.getResponseMessage();
            view.createdAt = review.getCreatedAt();
            return view;
        }

        public Long getId() {
            return id;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public int getRating() {
            return rating;
        }

        public String getTitle() {
            return title;
        }

        public String getMessage() {
            return message;
        }

        public boolean isResponded() {
            return responded;
        }

        public String getResponseMessage() {
            return responseMessage;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }

    public static class ContactMessageView {
        private Long id;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String subject;
        private String message;
        private boolean readByAdmin;
        private boolean responded;
        private String adminResponse;
        private LocalDateTime respondedAt;
        private LocalDateTime createdAt;

        public static ContactMessageView from(ContactMessage message) {
            ContactMessageView view = new ContactMessageView();
            view.id = message.getId();
            view.firstName = message.getFirstName();
            view.lastName = message.getLastName();
            view.email = message.getEmail();
            view.phone = message.getPhone();
            view.subject = message.getSubject();
            view.message = message.getMessage();
            view.readByAdmin = message.isReadByAdmin();
            view.responded = message.isResponded();
            view.adminResponse = message.getAdminResponse();
            view.respondedAt = message.getRespondedAt();
            view.createdAt = message.getCreatedAt();
            return view;
        }

        public Long getId() {
            return id;
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

        public String getSubject() {
            return subject;
        }

        public String getMessage() {
            return message;
        }

        public boolean isReadByAdmin() {
            return readByAdmin;
        }

        public boolean isResponded() {
            return responded;
        }

        public String getAdminResponse() {
            return adminResponse;
        }

        public LocalDateTime getRespondedAt() {
            return respondedAt;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }

    public static class NotificationsResponse {
        private int totalNew;
        private long newMessages;
        private long pendingReviews;
        private long pendingReservations;
        private List<String> items;

        public NotificationsResponse(int totalNew, long newMessages, long pendingReviews, long pendingReservations, List<String> items) {
            this.totalNew = totalNew;
            this.newMessages = newMessages;
            this.pendingReviews = pendingReviews;
            this.pendingReservations = pendingReservations;
            this.items = items;
        }

        public int getTotalNew() {
            return totalNew;
        }

        public long getNewMessages() {
            return newMessages;
        }

        public long getPendingReviews() {
            return pendingReviews;
        }

        public long getPendingReservations() {
            return pendingReservations;
        }

        public List<String> getItems() {
            return items;
        }
    }
}
