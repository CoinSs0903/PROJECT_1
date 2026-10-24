@echo off
chcp 65001 > nul
title Hệ Thống Đấu Giá Trực Tuyến - Online Auction System

echo =======================================================================
echo     🏛️ HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN (ONLINE AUCTION SYSTEM)
echo     Môn học: Phát Triển Phần Mềm Hướng Đối Tượng (PTPMHĐT)
echo =======================================================================
echo.

:: 1. Kiểm tra MySQL cổng 3306
echo [1/3] Đang kiểm tra kết nối MySQL trên cổng 3306...
netstat -ano | findstr :3306 > nul
if %errorlevel% neq 0 (
    echo [CẢNH BÁO] Chưa phát hiện dịch vụ MySQL chạy trên cổng 3306!
    echo Vui lòng mở XAMPP Control Panel và bấm Start tại dòng MySQL trước.
    echo.
) else (
    echo [OK] MySQL đang hoạt động sẵn sàng.
)

:: 2. Mở giao diện Web Client
echo [2/3] Đang mở giao diện Web App trên trình duyệt...
start "" "%~dp0frontend\web_app\index.html"

:: 3. Khởi động Backend Spring Boot
echo [3/3] Đang khởi động Backend Spring Boot (cổng 8080)...
echo Bạn có thể truy cập backend tại: http://localhost:8080
echo Nhấn Ctrl + C để dừng dịch vụ khi cần.
echo.

cd /d "%~dp0backend"
call mvnw.cmd spring-boot:run

pause
