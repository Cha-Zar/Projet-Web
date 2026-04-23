package tn.mondelys.backend.repository;

import tn.mondelys.backend.model.Reservation;
import tn.mondelys.backend.model.ReservationStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    boolean existsByReferenceCode(String referenceCode);

    long countByReservationDate(LocalDate date);

    long countByStatus(ReservationStatus status);

    List<Reservation> findByReservationDateBetween(LocalDate startDate, LocalDate endDate, Sort sort);
}
