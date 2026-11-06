@echo off
title AUTOMATED TEST RUNNER - ONLINE AUCTION SYSTEM
echo =======================================================================
echo    KIEM THU TU DONG - AUTOMATED TEST SUITE
echo    Du an: He Thong Dau Gia Truc Tuyen - Spring Boot JUnit 5
echo =======================================================================
echo.
echo Dang thuc thi 41 test cases tu dong... Vui long doi giay lat...
echo.

set "SCRIPT_DIR=%~dp0"
cd /d "%SCRIPT_DIR%backend"
call .\mvnw.cmd test -DforkCount=0 "-Dtest=*Test"

echo.
echo =======================================================================
echo    HOAN TAT KIEM THU!
echo =======================================================================
pause
