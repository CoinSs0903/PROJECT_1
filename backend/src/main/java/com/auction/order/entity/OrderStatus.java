package com.auction.order.entity;

public enum OrderStatus {
    AWAITING_PAYMENT,
    PAID,
    SHIPPED,
    DELIVERED,
    COMPLETED,
    CANCELLED_UNPAID
}
