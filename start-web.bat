@echo off
chcp 65001 > nul
title HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN - PROXY BIDDING
echo ===================================================
echo   KHỞI ĐỘNG HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN
echo ===================================================

echo [1/2] Kiểm tra kết nối MySQL cổng 3306...
netstat -ano | findstr :3306 > nul
if %errorlevel% neq 0 (
    echo [!] CẢNH BÁO: Chưa tìm thấy MySQL đang chạy trên cổng 3306!
    echo [*] Vui lòng mở XAMPP Control Panel và bấm "Start" ở mục MySQL.
    echo [*] Đang thử tự động khởi động MySQL từ C:\xampp...
    if exist C:\xampp\mysql_start.bat (
        start /b C:\xampp\mysql_start.bat
        timeout /t 3 > nul
    )
) else (
    echo [OK] MySQL đang hoạt động bình thường!
)

echo.
echo [2/2] Đang khởi động Server Spring Boot (cổng 8080)...
echo [*] Vui lòng đợi trong giây lát...
cd /d "%~dp0backend"
call .\mvnw.cmd spring-boot:run

pause
