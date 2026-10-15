@echo off
rem myshop 一体化启动：后端 API + /admin/ 后台管理 + /mall/ 商城（需 JDK 25 与本地 MySQL）
set "JAVA_HOME=D:\jdk\jdk-25.0.4"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0"
echo Starting myshop...  http://localhost:8080  (admin: /admin/  mall: /mall/)
java -jar myshop-backend\myshop-manager\myshop-manager-web\target\myshop-manager-boot.jar
pause
