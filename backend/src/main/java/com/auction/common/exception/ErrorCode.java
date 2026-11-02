package com.auction.common.exception;

public enum ErrorCode {
    SUCCESS("00", "Thành công"),
    BAD_REQUEST("400", "Yêu cầu không hợp lệ"),
    UNAUTHORIZED("401", "Chưa đăng nhập hoặc phiên làm việc hết hạn"),
    FORBIDDEN("403", "Không có quyền thực hiện hành động này"),
    NOT_FOUND("404", "Không tìm thấy tài nguyên yêu cầu"),
    INTERNAL_SERVER_ERROR("500", "Lỗi xử lý nội bộ hệ thống"),

    // User errors
    USER_NOT_FOUND("USR_01", "Không tìm thấy người dùng"),
    USERNAME_ALREADY_EXISTS("USR_02", "Tên đăng nhập đã tồn tại"),
    EMAIL_ALREADY_EXISTS("USR_03", "Email đã tồn tại"),
    USER_RESTRICTED("USR_04", "Tài khoản bị hạn chế tham gia đấu giá do vi phạm"),
    USER_BANNED("USR_05", "Tài khoản đã bị cấm hoạt động"),

    // Wallet errors
    WALLET_NOT_FOUND("WAL_01", "Không tìm thấy ví người dùng"),
    INSUFFICIENT_BALANCE("WAL_02", "Số dư khả dụng trong ví không đủ"),
    INVALID_TRANSACTION_AMOUNT("WAL_03", "Số tiền giao dịch không hợp lệ"),

    // Auction & Bidding errors
    AUCTION_NOT_FOUND("AUC_01", "Không tìm thấy phiên đấu giá"),
    AUCTION_NOT_ACTIVE("AUC_02", "Phiên đấu giá hiện không ở trạng thái mở"),
    CANNOT_BID_OWN_AUCTION("AUC_03", "Người bán không được phép đặt giá trên sản phẩm của mình"),
    BID_AMOUNT_TOO_LOW("AUC_04", "Mức giá trần phải lớn hơn hoặc bằng giá tối thiểu yêu cầu"),
    BUY_IT_NOW_UNAVAILABLE("AUC_05", "Tính năng Mua ngay không còn khả dụng trên phiên này"),

    // Catalog errors
    CATEGORY_NOT_FOUND("CAT_01", "Không tìm thấy danh mục"),
    ITEM_NOT_FOUND("ITM_01", "Không tìm thấy món hàng"),
    ITEM_NOT_OWNED("ITM_02", "Bạn không có quyền thao tác trên món hàng này"),

    // Order errors
    ORDER_NOT_FOUND("ORD_01", "Không tìm thấy đơn hàng"),
    ORDER_ALREADY_PAID("ORD_02", "Đơn hàng đã được thanh toán"),
    ORDER_EXPIRED("ORD_03", "Đơn hàng đã quá hạn thanh toán");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}

