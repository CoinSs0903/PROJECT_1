package com.auction.bidding.entity;

import com.auction.common.entity.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "proxy_bids", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"auction_id", "bidder_id"})
})
public class ProxyBid extends BaseEntity {

    @Column(name = "auction_id", nullable = false)
    private Long auctionId;

    @Column(name = "bidder_id", nullable = false)
    private Long bidderId;

    @Column(name = "max_amount", nullable = false)
    private long maxAmount;

    public ProxyBid() {}

    public Long getAuctionId() {
        return auctionId;
    }

    public void setAuctionId(Long auctionId) {
        this.auctionId = auctionId;
    }

    public Long getBidderId() {
        return bidderId;
    }

    public void setBidderId(Long bidderId) {
        this.bidderId = bidderId;
    }

    public long getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(long maxAmount) {
        this.maxAmount = maxAmount;
    }
}

