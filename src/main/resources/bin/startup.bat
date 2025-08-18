@echo off
REM FeatherJet Server Startup Script for Windows
REM Light as a feather, fast as a jet!

setlocal

REM Get the directory where this script is located
set SCRIPT_DIR=%~dp0
set FEATHERJET_HOME=%SCRIPT_DIR%..

REM Set default JVM options
set DEFAULT_JVM_OPTS=-Xms256m -Xmx1g -XX:+UseG1GC -XX:MaxGCPauseMillis=200

REM Use environment variable if set, otherwise use default
if "%FEATHERJET_JVM_OPTS%"=="" (
    set JVM_OPTS=%DEFAULT_JVM_OPTS%
) else (
    set JVM_OPTS=%FEATHERJET_JVM_OPTS%
)

REM JAR file location
set JAR_FILE=%FEATHERJET_HOME%\featherjet-server.jar

REM Check if Java is available
java -version >nul 2>&1
if errorlevel 1 (
    echo Error: Java is not installed or not in PATH
    echo Please install Java 17 or higher
    exit /b 1
)

REM Check if JAR file exists
if not exist "%JAR_FILE%" (
    echo Error: featherjet-server.jar not found at %JAR_FILE%
    exit /b 1
)

REM Print banner
echo 🚀 Starting FeatherJet Server...
echo    Home: %FEATHERJET_HOME%
echo    JVM Options: %JVM_OPTS%
echo    Arguments: %*
echo.

REM Change to FeatherJet home directory
cd /d "%FEATHERJET_HOME%"

REM Start the server
java %JVM_OPTS% -jar "%JAR_FILE%" %*
