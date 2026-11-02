package com.auction.auction.dto;

import com.auction.auction.entity.AuctionStatus;
import java.time.LocalDateTime;

public class AuctionResponseDto {
    private Long id;
    private Long itemId;
    private String itemTitle;
    private String itemImageUrl;
    private String categoryName;
    private long startingPrice;
    private long stepPrice;
    private long currentPrice;
    private Long buyItNowPrice;
    private boolean isReserveMet;
    private int bidCount;
    private String highestBidderMasked;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private AuctionStatus status;
    private boolean buyItNowAvailable;

    public AuctionResponseDto() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public String getItemImageUrl() {
        return itemImageUrl;
    }

    public void setItemImageUrl(String itemImageUrl) {
        this.itemImageUrl = itemImageUrl;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public long getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(long currentPrice) {
        this.currentPrice = currentPrice;
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

    public boolean isBuyItNowAvailable() {
        return buyItNowAvailable;
    }

    public void setBuyItNowAvailable(boolean buyItNowAvailable) {
        this.buyItNowAvailable = buyItNowAvailable;
    }
}

