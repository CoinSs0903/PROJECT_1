package com.auction.complaint.repository;

import com.auction.complaint.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Review> findByOrderIdAndReviewerId(Long orderId, Long reviewerId);
    List<Review> findByRevieweeIdOrderByCreatedAtDesc(Long revieweeId);
}
