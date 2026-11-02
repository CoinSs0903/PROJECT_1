package com.auction.catalog.entity;

public enum ItemCondition {
    NEW("Mới 100%"),
    LIKE_NEW("Như mới (99%)"),
    USED("Đã qua sử dụng");

    private final String displayName;

    ItemCondition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

