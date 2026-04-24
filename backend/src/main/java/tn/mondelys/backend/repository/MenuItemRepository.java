package tn.mondelys.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.mondelys.backend.model.MenuItem;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findAllByCuisineIgnoreCase(String cuisine);
}
