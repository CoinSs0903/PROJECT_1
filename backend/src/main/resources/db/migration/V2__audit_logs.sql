-- =============================================================================
-- V2__audit_logs.sql - Khởi tạo bảng nhật ký kiểm toán bất biến (audit_logs)
-- Căn cứ: docs/PLAN.md (Nhiệm vụ Ngày 8 - Module audit)
-- =============================================================================

CREATE TABLE audit_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    action VARCHAR(100) NOT NULL,
    user_id BIGINT NULL,
    entity_name VARCHAR(100) NULL,
    entity_id BIGINT NULL,
    details TEXT NULL,
    ip_address VARCHAR(50) NULL,
    created_at DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

