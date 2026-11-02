package com.auction.common.util;

public final class StringUtil {

    private StringUtil() {}

    /**
     * Ẩn danh tên người dùng theo BR-24 (ví dụ: nguyenvanan -> n***n hoặc username -> u***e)
     */
    public static String maskUsername(String username) {
        if (username == null || username.isBlank()) {
            return "***";
        }
        if (username.length() <= 2) {
            return username.charAt(0) + "***";
        }
        return username.charAt(0) + "***" + username.charAt(username.length() - 1);
    }
}

