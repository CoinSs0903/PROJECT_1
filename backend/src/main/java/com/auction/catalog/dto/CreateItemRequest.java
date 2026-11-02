package com.auction.catalog.dto;

import com.auction.catalog.entity.ItemCondition;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public class CreateItemRequest {

    @NotNull(message = "Vui lòng chọn danh mục")
    private Long categoryId;

    @NotBlank(message = "Tên món hàng không được để trống")
    private String title;

    private String description;

    @NotNull(message = "Vui lòng chọn tình trạng sản phẩm")
    private ItemCondition itemCondition = ItemCondition.USED;

    private String mainImageUrl;

    private List<String> additionalImageUrls;

    public CreateItemRequest() {}

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public List<String> getAdditionalImageUrls() {
        return additionalImageUrls;
    }

    public void setAdditionalImageUrls(List<String> additionalImageUrls) {
        this.additionalImageUrls = additionalImageUrls;
    }
}

