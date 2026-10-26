@REM ----------------------------------------------------------------------------
@REM Movie Ticket Booking - Smart Maven Wrapper Batch Script
@REM ----------------------------------------------------------------------------
@echo off
setlocal enabledelayedexpansion

set "MVN_CMD="

@REM 1. Check if mvn.cmd is available in PATH
where mvn.cmd >nul 2>&1
if %ERRORLEVEL% equ 0 (
    for /f "delims=" %%I in ('where mvn.cmd') do (
        if not defined MVN_CMD set "MVN_CMD=%%I"
    )
)

@REM 2. Check MAVEN_HOME or M2_HOME
if not defined MVN_CMD if defined MAVEN_HOME (
    if exist "%MAVEN_HOME%\bin\mvn.cmd" set "MVN_CMD=%MAVEN_HOME%\bin\mvn.cmd"
)
if not defined MVN_CMD if defined M2_HOME (
    if exist "%M2_HOME%\bin\mvn.cmd" set "MVN_CMD=%M2_HOME%\bin\mvn.cmd"
)

@REM 3. Check ~/.m2/apache-maven-3.9.6
if not defined MVN_CMD (
    if exist "%USERPROFILE%\.m2\apache-maven-3.9.6\bin\mvn.cmd" (
        set "MVN_CMD=%USERPROFILE%\.m2\apache-maven-3.9.6\bin\mvn.cmd"
    )
)

@REM 4. Search any apache-maven in ~/.m2
if not defined MVN_CMD (
    for /d %%D in ("%USERPROFILE%\.m2\apache-maven*") do (
        if exist "%%D\bin\mvn.cmd" (
            set "MVN_CMD=%%D\bin\mvn.cmd"
        )
    )
)

@REM 5. If found, execute Maven
if defined MVN_CMD (
    call "!MVN_CMD!" %*
    exit /b !ERRORLEVEL!
)

@REM 6. Fallback if Maven is not installed: run JAR directly if spring-boot:run was requested
if "%1"=="spring-boot:run" (
    if exist "%~dp0target\movie-ticket-booking-1.0.0.jar" (
        echo [INFO] Maven not in PATH; launching built application directly...
        java -jar "%~dp0target\movie-ticket-booking-1.0.0.jar"
        exit /b %ERRORLEVEL%
    )
)

echo [ERROR] Apache Maven was not found in PATH or at %USERPROFILE%\.m2\apache-maven-3.9.6.
echo Please install Apache Maven or use java -jar target\movie-ticket-booking-1.0.0.jar.
exit /b 1
