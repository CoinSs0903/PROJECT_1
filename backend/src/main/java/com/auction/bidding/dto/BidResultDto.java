package com.auction.bidding.dto;

public class BidResultDto {
    private boolean success;
    private String message;
    private long currentPrice;
    private int bidCount;
    private String highestBidderMasked;
    private boolean isReserveMet;
    private boolean leading;

    public BidResultDto() {}

    public BidResultDto(boolean success, String message, long currentPrice, int bidCount, String highestBidderMasked, boolean isReserveMet, boolean leading) {
        this.success = success;
        this.message = message;
        this.currentPrice = currentPrice;
        this.bidCount = bidCount;
        this.highestBidderMasked = highestBidderMasked;
        this.isReserveMet = isReserveMet;
        this.leading = leading;
    }

    public static BidResultDto success(String message, long currentPrice, int bidCount, String highestBidderMasked, boolean isReserveMet, boolean leading) {
        return new BidResultDto(true, message, currentPrice, bidCount, highestBidderMasked, isReserveMet, leading);
    }

    public static BidResultDto failure(String message) {
        return new BidResultDto(false, message, 0L, 0, null, false, false);
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public boolean isLeading() {
        return leading;
    }

    public void setLeading(boolean leading) {
        this.leading = leading;
    }
}
