package com.auction.order.repository;

import com.auction.order.entity.Order;
import com.auction.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByAuctionId(Long auctionId);
    List<Order> findByBuyerIdOrderByCreatedAtDesc(Long buyerId);
    List<Order> findBySellerIdOrderByCreatedAtDesc(Long sellerId);
    List<Order> findByStatusAndPaymentDeadlineBefore(OrderStatus status, LocalDateTime deadline);
}
