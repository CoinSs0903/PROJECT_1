package com.auction.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoneyUtilTest {

    @Test
    @DisplayName("Định dạng tiền tệ VNĐ chuẩn có dấu chấm phân cách hàng nghìn")
    void testFormatVnd() {
        assertEquals("1.500.000 ₫", MoneyUtil.formatVnd(1_500_000L));
        assertEquals("50.000 ₫", MoneyUtil.formatVnd(50_000L));
        assertEquals("0 ₫", MoneyUtil.formatVnd(0L));
    }

    @Test
    @DisplayName("Kiểm tra bậc bước giá tối thiểu theo BR-03")
    void testCalculateBidIncrementTiers() {
        // Dưới hoặc bằng 100k -> 5k
        assertEquals(5_000L, MoneyUtil.calculateBidIncrement(50_000L));
        assertEquals(5_000L, MoneyUtil.calculateBidIncrement(100_000L));

        // Từ 100k đến 1tr -> 20k
        assertEquals(20_000L, MoneyUtil.calculateBidIncrement(100_001L));
        assertEquals(20_000L, MoneyUtil.calculateBidIncrement(1_000_000L));

        // Từ 1tr đến 10tr -> 100k
        assertEquals(100_000L, MoneyUtil.calculateBidIncrement(1_000_001L));
        assertEquals(100_000L, MoneyUtil.calculateBidIncrement(10_000_000L));

        // Trên 10tr -> 500k
        assertEquals(500_000L, MoneyUtil.calculateBidIncrement(10_000_001L));
        assertEquals(500_000L, MoneyUtil.calculateBidIncrement(50_000_000L));
    }

    @Test
    @DisplayName("Tính mức giá tối thiểu tiếp theo")
    void testCalculateMinimumNextBid() {
        assertEquals(55_000L, MoneyUtil.calculateMinimumNextBid(50_000L, 0));
        assertEquals(60_000L, MoneyUtil.calculateMinimumNextBid(50_000L, 10_000L));
    }
}

