package com.auction;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class TestServerRunner {

    @Test
    void startServerForTesting() throws InterruptedException {
        System.out.println("\n=======================================================");
        System.out.println("  🚀 BACKEND SPRING BOOT SERVER ĐANG CHẠY TRÊN PORT 8080");
        System.out.println("  🌐 TRUY CẬP: http://localhost:8080");
        System.out.println("=======================================================\n");

        // Giữ server chạy liên tục để bạn thao tác trực tiếp trên trình duyệt web
        System.out.println("Máy chủ sẵn sàng nhận request từ trình duyệt...");
        Thread.sleep(Long.MAX_VALUE);
    }
}

