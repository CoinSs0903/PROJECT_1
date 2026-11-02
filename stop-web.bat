@echo off
chcp 65001 > nul
title DỪNG HỆ THỐNG ĐẤU GIÁ
echo ===================================================
echo   ĐANG DỪNG HỆ THỐNG ĐẤU GIÁ (CỔNG 8080)
echo ===================================================

echo Đang tìm và tắt tiến trình Java chạy trên cổng 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr :8080') do (
    echo Đang đóng tiến trình PID: %%a...
    taskkill /F /PID %%a > nul 2>&1
)

echo.
echo [OK] Đã tắt Web Server thành công! Cổng 8080 đã được giải phóng.
echo.
pause

