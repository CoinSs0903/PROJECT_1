package com.auction.wallet.facade;

public interface WalletFacade {
    long getAvailableBalance(Long userId);
    void createWalletForUser(Long userId);
    void topUp(Long userId, long amount, String idempotencyKey);
    void withdraw(Long userId, long amount, String bankAccount, String idempotencyKey);
    void payOrder(Long buyerId, Long orderId, long amount, String idempotencyKey);
    void payoutSeller(Long sellerId, Long orderId, long finalPrice, double feePercent, String idempotencyKey);
    void refundOrder(Long buyerId, Long orderId, long amount, String idempotencyKey);
}

