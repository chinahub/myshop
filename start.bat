@echo off
rem myshop backend and frontend integrated startup (requires JDK 25 and local MySQL)
set "JAVA_HOME=D:\jdk\jdk-25.0.4"
set "PATH=%JAVA_HOME%in;%PATH%"
cd /d "%~dp0"
echo Starting myshop...  http://localhost:8080
java -jar myshop-manager\myshop-manager-web	arget\myshop-manager-boot.jar
pause
