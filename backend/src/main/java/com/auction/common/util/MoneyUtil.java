package com.auction.common.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public final class MoneyUtil {

    private static final DecimalFormat VND_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("vi", "VN"));
        symbols.setGroupingSeparator('.');
        VND_FORMAT = new DecimalFormat("#,###", symbols);
    }

    private MoneyUtil() {}

    /**
     * Định dạng số tiền sang chuẩn hiển thị tiếng Việt (ví dụ: 1.500.000 ₫)
     */
    public static String formatVnd(long amount) {
        return VND_FORMAT.format(amount) + " ₫";
    }

    /**
     * Tính bước giá tối thiểu chuẩn theo các bậc quy định (BR-03)
     */
    public static long calculateBidIncrement(long currentPrice) {
        if (currentPrice <= 100_000L) {
            return 5_000L;
        } else if (currentPrice <= 1_000_000L) {
            return 20_000L;
        } else if (currentPrice <= 10_000_000L) {
            return 100_000L;
        } else {
            return 500_000L;
        }
    }

    /**
     * Tính mức giá tối thiểu tiếp theo mà người dùng được phép đặt
     */
    public static long calculateMinimumNextBid(long currentPrice, long customStepPrice) {
        long step = customStepPrice > 0 ? customStepPrice : calculateBidIncrement(currentPrice);
        return currentPrice + step;
    }
}

