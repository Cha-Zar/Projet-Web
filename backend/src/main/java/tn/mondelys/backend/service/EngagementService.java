package tn.mondelys.backend.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import tn.mondelys.backend.dto.EngagementDtos;
import tn.mondelys.backend.model.ClientReview;
import tn.mondelys.backend.model.ContactMessage;
import tn.mondelys.backend.model.ReservationStatus;
import tn.mondelys.backend.repository.ClientReviewRepository;
import tn.mondelys.backend.repository.ContactMessageRepository;
import tn.mondelys.backend.repository.ReservationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class EngagementService {

    private final ClientReviewRepository clientReviewRepository;
    private final ContactMessageRepository contactMessageRepository;
    private final ReservationRepository reservationRepository;

    public EngagementService(ClientReviewRepository clientReviewRepository,
                             ContactMessageRepository contactMessageRepository,
                             ReservationRepository reservationRepository) {
        this.clientReviewRepository = clientReviewRepository;
        this.contactMessageRepository = contactMessageRepository;
        this.reservationRepository = reservationRepository;
    }

    public EngagementDtos.ReviewView createReview(EngagementDtos.CreateReviewRequest request) {
        ClientReview review = new ClientReview();
        review.setFullName(request.getFullName().trim());
        review.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        review.setRating(request.getRating());
        review.setTitle(blankToNull(request.getTitle()));
        review.setMessage(request.getMessage().trim());

        return EngagementDtos.ReviewView.from(clientReviewRepository.save(review));
    }

    public List<EngagementDtos.ReviewView> getPublicReviews() {
        return clientReviewRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(EngagementDtos.ReviewView::from)
                .toList();
    }

    public List<EngagementDtos.ReviewView> getAdminReviews(Integer rating, Boolean responded) {
        List<ClientReview> reviews = rating == null
                ? clientReviewRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                : clientReviewRepository.findAllByRating(rating, Sort.by(Sort.Direction.DESC, "createdAt"));

        return reviews.stream()
                .filter(review -> responded == null || review.isResponded() == responded)
                .map(EngagementDtos.ReviewView::from)
                .toList();
    }

    public EngagementDtos.ReviewView respondReview(Long reviewId, String adminEmail, EngagementDtos.ReviewResponseRequest request) {
        ClientReview review = clientReviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Avis introuvable"));

        review.setResponded(true);
        review.setResponseMessage(request.getResponse().trim());
        review.setRespondedBy(adminEmail);
        review.setRespondedAt(LocalDateTime.now());

        return EngagementDtos.ReviewView.from(clientReviewRepository.save(review));
    }

    public EngagementDtos.ContactMessageView createContactMessage(EngagementDtos.ContactMessageCreateRequest request) {
        ContactMessage message = new ContactMessage();
        message.setFirstName(request.getFirstName().trim());
        message.setLastName(request.getLastName().trim());
        message.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        message.setPhone(blankToNull(request.getPhone()));
        message.setSubject(request.getSubject().trim());
        message.setMessage(request.getMessage().trim());

        return EngagementDtos.ContactMessageView.from(contactMessageRepository.save(message));
    }

    public List<EngagementDtos.ContactMessageView> getAdminMessages() {
        return contactMessageRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
                .stream()
                .map(EngagementDtos.ContactMessageView::from)
                .toList();
    }

    public EngagementDtos.ContactMessageView markMessageRead(Long messageId) {
        ContactMessage message = contactMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message introuvable"));

        message.setReadByAdmin(true);
        return EngagementDtos.ContactMessageView.from(contactMessageRepository.save(message));
    }

    public EngagementDtos.ContactMessageView respondToMessage(Long messageId, String adminEmail, EngagementDtos.ContactResponseRequest request) {
        ContactMessage message = contactMessageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("Message introuvable"));
        String responseText = request.getResponse().trim();

        message.setReadByAdmin(true);
        message.setResponded(true);
        message.setAdminResponse(responseText);
        message.setRespondedBy(adminEmail);
        message.setRespondedAt(LocalDateTime.now());

        return EngagementDtos.ContactMessageView.from(contactMessageRepository.save(message));
    }

    public EngagementDtos.NotificationsResponse getNotificationsSummary() {
        long newMessages = contactMessageRepository.countByReadByAdminFalse();
        long pendingReviews = clientReviewRepository.countByRespondedFalse();
        long pendingReservations = reservationRepository.countByStatus(ReservationStatus.PENDING);
        int total = Math.toIntExact(newMessages + pendingReviews + pendingReservations);

        List<String> items = List.of(
                newMessages + " nouveau(x) message(s) client",
                pendingReviews + " avis client(s) en attente de réponse",
                pendingReservations + " réservation(s) en attente"
        );

        return new EngagementDtos.NotificationsResponse(total, newMessages, pendingReviews, pendingReservations, items);
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
