package tn.mondelys.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.mondelys.backend.model.AppSettings;

public interface AppSettingsRepository extends JpaRepository<AppSettings, Long> {
}
