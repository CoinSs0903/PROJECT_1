package com.auction.system.facade;

public interface SystemConfigFacade {
    int getPlatformFeePercent();
    int getPaymentDeadlineHours();
    int getMaxUnpaidStrikes();
    long calculateMinimumNextBid(long currentPrice);
}

