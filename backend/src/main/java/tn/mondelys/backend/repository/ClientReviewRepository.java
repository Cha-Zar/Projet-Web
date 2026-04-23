package tn.mondelys.backend.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import tn.mondelys.backend.model.ClientReview;

import java.util.List;

public interface ClientReviewRepository extends JpaRepository<ClientReview, Long> {
    long countByRespondedFalse();

    List<ClientReview> findAllByRating(Integer rating, Sort sort);
}
