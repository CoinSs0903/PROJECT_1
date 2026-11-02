package com.auction.auction.dto;

import com.auction.auction.entity.AuctionStatus;
import com.auction.bidding.dto.BidHistoryDto;
import com.auction.catalog.entity.ItemCondition;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AuctionDetailDto {
    private Long id;
    private Long itemId;
    private String itemTitle;
    private String itemDescription;
    private ItemCondition itemCondition;
    private String mainImageUrl;
    private List<String> imageUrls = new ArrayList<>();
    private Long categoryId;
    private String categoryName;
    private Long sellerId;
    private String sellerUsername;

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
    private boolean canCancel;
    private boolean userIsSeller;
    private boolean userIsHighestBidder;
    private List<BidHistoryDto> bidHistory = new ArrayList<>();

    public AuctionDetailDto() {}

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

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public ItemCondition getItemCondition() {
        return itemCondition;
    }

    public void setItemCondition(ItemCondition itemCondition) {
        this.itemCondition = itemCondition;
    }

    public String getMainImageUrl() {
        return mainImageUrl;
    }

    public void setMainImageUrl(String mainImageUrl) {
        this.mainImageUrl = mainImageUrl;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerUsername() {
        return sellerUsername;
    }

    public void setSellerUsername(String sellerUsername) {
        this.sellerUsername = sellerUsername;
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

    public boolean isCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public boolean isUserIsSeller() {
        return userIsSeller;
    }

    public void setUserIsSeller(boolean userIsSeller) {
        this.userIsSeller = userIsSeller;
    }

    public boolean isUserIsHighestBidder() {
        return userIsHighestBidder;
    }

    public void setUserIsHighestBidder(boolean userIsHighestBidder) {
        this.userIsHighestBidder = userIsHighestBidder;
    }

    public List<BidHistoryDto> getBidHistory() {
        return bidHistory;
    }

    public void setBidHistory(List<BidHistoryDto> bidHistory) {
        this.bidHistory = bidHistory;
    }
}
