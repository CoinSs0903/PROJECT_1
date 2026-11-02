package com.auction.realtime.dto;

import java.time.LocalDateTime;

public class BidUpdateEvent {
    private Long auctionId;
    private long currentPrice;
    private int bidCount;
    private String highestBidderMasked;
    private boolean isReserveMet;
    private LocalDateTime timestamp;

    public BidUpdateEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public BidUpdateEvent(Long auctionId, long currentPrice, int bidCount, String highestBidderMasked, boolean isReserveMet) {
        this.auctionId = auctionId;
        this.currentPrice = currentPrice;
        this.bidCount = bidCount;
        this.highestBidderMasked = highestBidderMasked;
        this.isReserveMet = isReserveMet;
        this.timestamp = LocalDateTime.now();
    }

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(long currentPrice) {
        this.currentPrice = currentPrice;
    }

    public int getBidCount() {
        return bidCount;
    }

    public void setBidCount(int bidCount) {
        this.bidCount = bidCount;
    }

    public String getHighestBidderMasked() {
        return highestBidderMasked;
    }

    public void setHighestBidderMasked(String highestBidderMasked) {
        this.highestBidderMasked = highestBidderMasked;
    }

    public boolean isReserveMet() {
        return isReserveMet;
    }

    public void setReserveMet(boolean reserveMet) {
        isReserveMet = reserveMet;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}

