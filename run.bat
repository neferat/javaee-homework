@echo off
echo Starting Spring Boot application...
cd /d "D:\dowmload\ssmnew"

REM 首先尝试使用Maven编译和运行
echo Compiling with Maven...
call mvn clean compile -Dmaven.compiler.source=11 -Dmaven.compiler.target=11 -q

if %ERRORLEVEL% equ 0 (
    echo Compilation successful, starting application...
    java -cp "target/classes;target/dependency/*" boot.spring.Application
) else (
    echo Maven compilation failed, trying alternative method...
    REM 直接运行已编译的类
    java -cp "target/classes" boot.spring.Application
)

pause 