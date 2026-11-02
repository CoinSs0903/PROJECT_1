@echo off
chcp 65001 > nul
title HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN - PROXY BIDDING
echo =======================================================================
echo   🚀 KHỞI ĐỘNG HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN (ONLINE AUCTION SYSTEM)
echo =======================================================================
echo.

echo [1/3] Kiểm tra kết nối MySQL cổng 3306...
netstat -ano | findstr :3306 > nul
if %errorlevel% neq 0 (
    echo [!] CẢNH BÁO: Chưa tìm thấy dịch vụ MySQL chạy trên cổng 3306!
    echo [*] Vui lòng mở XAMPP Control Panel và bấm "Start" ở mục MySQL trước.
    echo [*] Đang thử tự động khởi động MySQL từ C:\xampp...
    if exist C:\xampp\mysql_start.bat (
        start /b C:\xampp\mysql_start.bat
        timeout /t 3 > nul
    )
) else (
    echo [OK] MySQL đang hoạt động bình thường trên cổng 3306!
)

echo.
echo [2/3] Kiểm tra giải phóng cổng 8080 nếu có tiến trình cũ...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    echo [*] Đang giải phóng cổng 8080 từ PID: %%a...
    taskkill /F /PID %%a > nul 2>&1
)

echo.
echo [3/3] Đang khởi động Server Spring Boot (cổng 8080)...
echo [*] Khởi động chế độ an toàn (tránh lỗi ký tự tiếng Việt Đ trong đường dẫn)...
echo [*] Vui lòng đợi trong giây lát...
echo.
echo -----------------------------------------------------------------------
echo   🌐 SAU KHI SERVER CHẠY XONG, HÃY MỞ TRÌNH DUYỆT TRUY CẬP:
echo      👉 Trang chủ / Đấu giá: http://localhost:8080
echo      👉 Trang quản trị Admin: http://localhost:8080/admin/dashboard
echo      👉 Đăng nhập Admin: admin / admin123
echo      👉 Đăng nhập Người mua: buyer1 / password
echo -----------------------------------------------------------------------
echo.

cd /d "%~dp0backend"
call .\mvnw.cmd test -DforkCount=0 -Dtest=TestServerRunner

pause

