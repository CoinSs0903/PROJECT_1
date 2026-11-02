package com.auction.catalog.dto;

import com.auction.catalog.entity.Item;
import com.auction.catalog.entity.ItemCondition;

import java.time.LocalDateTime;
import java.util.List;

public class ItemResponseDto {
    private Long id;
    private Long sellerId;
    private Long categoryId;
    private String categoryName;
    private String title;
    private String description;
    private ItemCondition itemCondition;
    private String mainImageUrl;
    private List<String> imageUrls;
    private LocalDateTime createdAt;

    public ItemResponseDto() {}

    public static ItemResponseDto fromEntity(Item item, String categoryName, List<String> imageUrls) {
        ItemResponseDto dto = new ItemResponseDto();
        dto.setId(item.getId());
        dto.setSellerId(item.getSellerId());
        dto.setCategoryId(item.getCategoryId());
        dto.setCategoryName(categoryName);
        dto.setTitle(item.getTitle());
        dto.setDescription(item.getDescription());
        dto.setItemCondition(item.getItemCondition());
        dto.setMainImageUrl(item.getMainImageUrl());
        dto.setImageUrls(imageUrls);
        dto.setCreatedAt(item.getCreatedAt());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

