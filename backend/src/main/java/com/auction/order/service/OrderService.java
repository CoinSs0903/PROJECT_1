package com.auction.order.service;

import com.auction.order.entity.Order;
import com.auction.order.entity.OrderStatus;
import com.auction.order.entity.PurchaseType;
import com.auction.order.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Order createOrder(Long auctionId, Long buyerId, Long sellerId, PurchaseType purchaseType, long finalPrice, long shippingFee) {
        Order order = new Order();
        order.setAuctionId(auctionId);
        order.setBuyerId(buyerId);
        order.setSellerId(sellerId);
        order.setPurchaseType(purchaseType);
        order.setFinalPrice(finalPrice);
        order.setShippingFee(shippingFee);
        order.setTotalAmount(finalPrice + shippingFee);
        order.setStatus(OrderStatus.AWAITING_PAYMENT);
        order.setPaymentDeadline(LocalDateTime.now().plusHours(72));
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Optional<Order> findById(Long orderId) {
        return orderRepository.findById(orderId);
    }

    @Transactional(readOnly = true)
    public Optional<Order> findByAuctionId(Long auctionId) {
        return orderRepository.findByAuctionId(auctionId);
    }

    @Transactional(readOnly = true)
    public List<Order> findMyPurchases(Long buyerId) {
        return orderRepository.findByBuyerIdOrderByCreatedAtDesc(buyerId);
    }

    @Transactional(readOnly = true)
    public List<Order> findMySales(Long sellerId) {
        return orderRepository.findBySellerIdOrderByCreatedAtDesc(sellerId);
    }
}
