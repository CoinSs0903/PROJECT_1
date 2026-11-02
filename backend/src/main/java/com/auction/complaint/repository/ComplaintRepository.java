package com.auction.complaint.repository;

import com.auction.complaint.entity.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByOrderIdOrderByCreatedAtDesc(Long orderId);
    List<Complaint> findByComplainantIdOrderByCreatedAtDesc(Long complainantId);
    List<Complaint> findByStatusOrderByCreatedAtDesc(String status);
}

