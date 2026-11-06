@echo off
chcp 65001 > nul
title KIỂM THỬ TỰ ĐỘNG - ONLINE AUCTION SYSTEM

echo =======================================================================
echo    🧪 CHẠY BỘ KIỂM THỬ TỰ ĐỘNG (AUTOMATED TEST SUITE)
echo    Dự án: Hệ Thống Đấu Giá Trực Tuyến - Spring Boot & JUnit 5
echo =======================================================================
echo.
echo Đang quét và thực thi toàn bộ test cases tự động...
echo.

cd /d "%~dp0backend"
call .\mvnw.cmd test -Dtest=*Test

echo.
echo =======================================================================
echo    🎉 HOÀN TẤT KIỂM THỬ! Bạn có thể xem kết quả chi tiết ở trên.
echo =======================================================================
pause
