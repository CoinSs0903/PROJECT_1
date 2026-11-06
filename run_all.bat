@echo off
chcp 65001 > nul
title HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN - ONLINE AUCTION SYSTEM

echo =======================================================================
echo     🏛️ HỆ THỐNG ĐẤU GIÁ TRỰC TUYẾN TỰ ĐỘNG (ONLINE AUCTION SYSTEM)
echo     Môn học: Phát Triển Phần Mềm Hướng Đối Tượng (PTPMHĐT)
echo =======================================================================
echo.

:: 1. Kiểm tra MySQL cổng 3306
echo [1/3] Đang kiểm tra kết nối MySQL trên cổng 3306...
netstat -ano | findstr :3306 > nul
if %errorlevel% neq 0 (
    echo [CẢNH BÁO] Chưa phát hiện dịch vụ MySQL chạy trên cổng 3306!
    echo Vui lòng mở XAMPP Control Panel và bấm Start tại dòng MySQL trước.
    if exist C:\xampp\mysql_start.bat (
        start /b C:\xampp\mysql_start.bat
        timeout /t 3 > nul
    )
    echo.
) else (
    echo [OK] MySQL đang hoạt động sẵn sàng.
)

:: 2. Dọn dẹp cổng 8080 nếu đang bị chiếm
echo [2/3] Kiểm tra giải phóng cổng 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    taskkill /F /PID %%a > nul 2>&1
)

:: 3. Khởi động Backend Spring Boot & mở Web Portal
echo [3/3] Đang khởi động máy chủ Spring Boot (cổng 8080)...
echo -----------------------------------------------------------------------
echo   🌐 HỆ THỐNG SẼ CHẠY TẠI ĐỊA CHỈ:
echo      👉 Trang chủ: http://localhost:8080
echo      👉 Trang quản trị: http://localhost:8080/admin
echo      👉 Tài khoản Admin: admin / admin123
echo      👉 Tài khoản Seller: seller1 / 123456
echo      👉 Tài khoản Buyer: buyer1 / 123456
echo -----------------------------------------------------------------------
echo.

start "" "http://localhost:8080"

cd /d "%~dp0backend"
call .\mvnw.cmd test -DforkCount=0 -Dtest=TestServerRunner

pause
