package com.auction.bidding.dto;

import java.time.LocalDateTime;

public class BidHistoryDto {
    private Long id;
    private Long bidderId;
    private String maskedBidderUsername;
    private long bidAmount;
    private LocalDateTime bidTime;
    private boolean isAutoBid;

    public BidHistoryDto() {}

    public BidHistoryDto(Long id, Long bidderId, String maskedBidderUsername, long bidAmount, LocalDateTime bidTime, boolean isAutoBid) {
        this.id = id;
        this.bidderId = bidderId;
        this.maskedBidderUsername = maskedBidderUsername;
        this.bidAmount = bidAmount;
        this.bidTime = bidTime;
        this.isAutoBid = isAutoBid;
    }

    public Long getId() {
        return id;
    }

    public Long getBidderId() {
        return bidderId;
    }

    public String getMaskedBidderUsername() {
        return maskedBidderUsername;
    }

    public long getBidAmount() {
        return bidAmount;
    }

    public LocalDateTime getBidTime() {
        return bidTime;
    }

    public boolean isAutoBid() {
        return isAutoBid;
    }
}
