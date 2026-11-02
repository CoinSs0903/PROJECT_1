package com.auction.bidding.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PlaceBidRequest {

    @NotNull(message = "Max amount is required")
    @Min(value = 1, message = "Bid amount must be positive")
    private Long maxAmount;

    public PlaceBidRequest() {}

    public PlaceBidRequest(Long maxAmount) {
        this.maxAmount = maxAmount;
    }

    public Long getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(Long maxAmount) {
        this.maxAmount = maxAmount;
    }
}

