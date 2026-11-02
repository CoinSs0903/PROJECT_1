package com.auction.auction.entity;

import com.auction.common.entity.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
public class Auction extends BaseEntity {

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "starting_price", nullable = false)
    private long startingPrice;

    @Column(name = "step_price", nullable = false)
    private long stepPrice;

    @Column(name = "reserve_price")
    private Long reservePrice;

    @Column(name = "buy_it_now_price")
    private Long buyItNowPrice;

    @Column(name = "is_reserve_met", nullable = false)
    private boolean isReserveMet = false;

    @Column(name = "current_price", nullable = false)
    private long currentPrice;

    @Column(name = "highest_bidder_id")
    private Long highestBidderId;

    @Column(name = "bid_count", nullable = false)
    private int bidCount = 0;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AuctionStatus status = AuctionStatus.ACTIVE;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    public Auction() {}

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

    public boolean isReserveMet() {
        return isReserveMet;
    }

    public void setReserveMet(boolean reserveMet) {
        isReserveMet = reserveMet;
    }

    public long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public Long getHighestBidderId() {
        return highestBidderId;
    }

    public void setHighestBidderId(Long highestBidderId) {
        this.highestBidderId = highestBidderId;
    }

    public int getBidCount() {
        return bidCount;
    }

    public void setBidCount(int bidCount) {
        this.bidCount = bidCount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public AuctionStatus getStatus() {
        return status;
    }

    public void setStatus(AuctionStatus status) {
        this.status = status;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}

