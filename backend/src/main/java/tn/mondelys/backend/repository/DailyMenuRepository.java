package tn.mondelys.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.mondelys.backend.model.DailyMenu;

import java.time.LocalDate;
import java.util.Optional;

public interface DailyMenuRepository extends JpaRepository<DailyMenu, Long> {
    Optional<DailyMenu> findByMenuDate(LocalDate menuDate);
}
