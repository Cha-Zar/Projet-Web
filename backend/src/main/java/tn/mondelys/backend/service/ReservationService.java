package tn.mondelys.backend.service;

import tn.mondelys.backend.dto.ReservationDtos;
import tn.mondelys.backend.model.Reservation;
import tn.mondelys.backend.model.ReservationStatus;
import tn.mondelys.backend.repository.ReservationRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ReservationDtos.ReservationResponse createReservation(ReservationDtos.CreateReservationRequest request) {
        Reservation reservation = new Reservation();
        reservation.setReferenceCode(generateReference());
        reservation.setFirstName(request.getFirstName().trim());
        reservation.setLastName(request.getLastName().trim());
        reservation.setEmail(request.getEmail().trim().toLowerCase(Locale.ROOT));
        reservation.setPhone(request.getPhone().trim());
        reservation.setReservationDate(request.getReservationDate());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setGuestsCount(request.getGuestsCount());
        reservation.setOccasion(blankToNull(request.getOccasion()));
        reservation.setPreorder(blankToNull(request.getPreorder()));
        reservation.setSpecialRequests(blankToNull(request.getSpecialRequests()));
        reservation.setStatus(ReservationStatus.PENDING);

        Reservation saved = reservationRepository.save(reservation);
        return ReservationDtos.ReservationResponse.fromEntity(saved);
    }

    public List<ReservationDtos.ReservationResponse> findAllForAdmin(String search, ReservationStatus status) {
        List<Reservation> reservations = reservationRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));

        return reservations.stream()
                .filter(reservation -> status == null || reservation.getStatus() == status)
                .filter(reservation -> {
                    if (search == null || search.isBlank()) {
                        return true;
                    }
                    String normalized = search.toLowerCase(Locale.ROOT);
                    return reservation.getReferenceCode().toLowerCase(Locale.ROOT).contains(normalized)
                            || reservation.getFirstName().toLowerCase(Locale.ROOT).contains(normalized)
                            || reservation.getLastName().toLowerCase(Locale.ROOT).contains(normalized)
                            || reservation.getEmail().toLowerCase(Locale.ROOT).contains(normalized);
                })
                .map(ReservationDtos.ReservationResponse::fromEntity)
                .toList();
    }

    public ReservationDtos.ReservationResponse updateStatus(Long reservationId, ReservationDtos.UpdateStatusRequest request, String adminEmail) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Réservation introuvable"));

        reservation.setStatus(request.getStatus());
        reservation.setAdminNote(blankToNull(request.getAdminNote()));
        reservation.setVerifiedBy(adminEmail);
        reservation.setVerifiedAt(LocalDateTime.now());

        Reservation saved = reservationRepository.save(reservation);
        return ReservationDtos.ReservationResponse.fromEntity(saved);
    }

    public ReservationDtos.DashboardResponse getDashboard() {
        long today = reservationRepository.countByReservationDate(LocalDate.now());
        long pending = reservationRepository.countByStatus(ReservationStatus.PENDING);
        long confirmed = reservationRepository.countByStatus(ReservationStatus.CONFIRMED);
        long cancelled = reservationRepository.countByStatus(ReservationStatus.CANCELLED);
        long total = reservationRepository.count();

        return new ReservationDtos.DashboardResponse(today, pending, confirmed, cancelled, total);
    }

    public List<Long> lastSevenDaysCounts() {
        LocalDate now = LocalDate.now();
        return java.util.stream.IntStream.rangeClosed(0, 6)
                .mapToObj(dayIndex -> now.minusDays(6L - dayIndex))
                .map(reservationRepository::countByReservationDate)
                .toList();
    }

    private String generateReference() {
        String candidate;
        do {
            int random = ThreadLocalRandom.current().nextInt(100000, 999999);
            candidate = "MDY-" + random;
        } while (reservationRepository.existsByReferenceCode(candidate));
        return candidate;
    }

    private String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
