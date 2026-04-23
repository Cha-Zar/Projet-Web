package tn.mondelys.backend.controller;

import tn.mondelys.backend.dto.ReservationDtos;
import tn.mondelys.backend.model.ReservationStatus;
import tn.mondelys.backend.service.ReservationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/api/reservations")
    public ResponseEntity<ReservationDtos.ReservationResponse> createReservation(
            @Valid @RequestBody ReservationDtos.CreateReservationRequest request
    ) {
        return ResponseEntity.ok(reservationService.createReservation(request));
    }

    @GetMapping("/api/admin/reservations")
    public ResponseEntity<List<ReservationDtos.ReservationResponse>> listReservations(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) ReservationStatus status
    ) {
        return ResponseEntity.ok(reservationService.findAllForAdmin(search, status));
    }

    @PatchMapping("/api/admin/reservations/{id}/status")
    public ResponseEntity<ReservationDtos.ReservationResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDtos.UpdateStatusRequest request,
            Authentication authentication
    ) {
        String adminEmail = authentication == null ? "system" : authentication.getName();
        return ResponseEntity.ok(reservationService.updateStatus(id, request, adminEmail));
    }

    @GetMapping("/api/admin/dashboard")
    public ResponseEntity<ReservationDtos.DashboardResponse> dashboardStats() {
        return ResponseEntity.ok(reservationService.getDashboard());
    }

    @GetMapping("/api/admin/dashboard/weekly-reservations")
    public ResponseEntity<Map<String, Object>> weeklyReservations() {
        return ResponseEntity.ok(Map.of(
                "labels", List.of("Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"),
                "values", reservationService.lastSevenDaysCounts()
        ));
    }
}
