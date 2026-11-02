package com.auction.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringUtilTest {

    @Test
    @DisplayName("Ẩn danh tên người dùng dạng a***b")
    void testMaskUsername() {
        assertEquals("n***n", StringUtil.maskUsername("nguyen"));
        assertEquals("b***r", StringUtil.maskUsername("bidder"));
        assertEquals("a***", StringUtil.maskUsername("ab"));
        assertEquals("***", StringUtil.maskUsername(""));
    }
}

