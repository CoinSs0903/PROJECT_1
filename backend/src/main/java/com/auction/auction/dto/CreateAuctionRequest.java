package com.auction.auction.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class CreateAuctionRequest {

    @NotNull(message = "Item ID is required")
    private Long itemId;

    @Min(value = 0, message = "Starting price must be >= 0")
    private long startingPrice;

    @Min(value = 1000, message = "Step price must be at least 1,000 VND")
    private long stepPrice;

    private Long reservePrice;

    private Long buyItNowPrice;

    @Min(value = 1, message = "Duration must be at least 1 day")
    private int durationDays = 3;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    public CreateAuctionRequest() {}

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public long getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(long startingPrice) {
        this.startingPrice = startingPrice;
    }

    public long getStepPrice() {
        return stepPrice;
    }

    public void setStepPrice(long stepPrice) {
        this.stepPrice = stepPrice;
    }

    public Long getReservePrice() {
        return reservePrice;
    }

    public void setReservePrice(Long reservePrice) {
        this.reservePrice = reservePrice;
    }

    public Long getBuyItNowPrice() {
        return buyItNowPrice;
    }

    public void setBuyItNowPrice(Long buyItNowPrice) {
        this.buyItNowPrice = buyItNowPrice;
    }

    public int getDurationDays() {
        return durationDays;
    }

    public void setDurationDays(int durationDays) {
        this.durationDays = durationDays;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
}
