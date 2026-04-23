package tn.mondelys.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.mondelys.backend.model.ContactMessage;

public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {
    long countByReadByAdminFalse();
}
