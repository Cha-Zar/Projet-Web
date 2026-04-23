package tn.mondelys.backend.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tn.mondelys.backend.dto.EngagementDtos;
import tn.mondelys.backend.service.EngagementService;

import java.util.List;

@RestController
public class EngagementController {

    private final EngagementService engagementService;

    public EngagementController(EngagementService engagementService) {
        this.engagementService = engagementService;
    }

    @PostMapping("/api/reviews")
    public ResponseEntity<EngagementDtos.ReviewView> createReview(@Valid @RequestBody EngagementDtos.CreateReviewRequest request) {
        return ResponseEntity.ok(engagementService.createReview(request));
    }

    @GetMapping("/api/reviews/public")
    public ResponseEntity<List<EngagementDtos.ReviewView>> publicReviews() {
        return ResponseEntity.ok(engagementService.getPublicReviews());
    }

    @GetMapping("/api/admin/reviews")
    public ResponseEntity<List<EngagementDtos.ReviewView>> adminReviews(
            @RequestParam(required = false) Integer rating,
            @RequestParam(required = false) Boolean responded
    ) {
        return ResponseEntity.ok(engagementService.getAdminReviews(rating, responded));
    }

    @PatchMapping("/api/admin/reviews/{id}/respond")
    public ResponseEntity<EngagementDtos.ReviewView> respondReview(
            @PathVariable Long id,
            @Valid @RequestBody EngagementDtos.ReviewResponseRequest request,
            Authentication authentication
    ) {
        String adminEmail = authentication == null ? "system" : authentication.getName();
        return ResponseEntity.ok(engagementService.respondReview(id, adminEmail, request));
    }

    @PostMapping("/api/contact-messages")
    public ResponseEntity<EngagementDtos.ContactMessageView> createContactMessage(
            @Valid @RequestBody EngagementDtos.ContactMessageCreateRequest request
    ) {
        return ResponseEntity.ok(engagementService.createContactMessage(request));
    }

    @GetMapping("/api/admin/contact-messages")
    public ResponseEntity<List<EngagementDtos.ContactMessageView>> adminContactMessages() {
        return ResponseEntity.ok(engagementService.getAdminMessages());
    }

    @PatchMapping("/api/admin/contact-messages/{id}/read")
    public ResponseEntity<EngagementDtos.ContactMessageView> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(engagementService.markMessageRead(id));
    }

    @PostMapping("/api/admin/contact-messages/{id}/respond")
    public ResponseEntity<EngagementDtos.ContactMessageView> respondContactMessage(
            @PathVariable Long id,
            @Valid @RequestBody EngagementDtos.ContactResponseRequest request,
            Authentication authentication
    ) {
        String adminEmail = authentication == null ? "system" : authentication.getName();
        return ResponseEntity.ok(engagementService.respondToMessage(id, adminEmail, request));
    }

    @GetMapping("/api/admin/notifications")
    public ResponseEntity<EngagementDtos.NotificationsResponse> notifications() {
        return ResponseEntity.ok(engagementService.getNotificationsSummary());
    }
}
