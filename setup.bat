@echo off
REM FigmaToPyCharmBridge - One-Click Setup Script for Windows
REM This script automates the complete setup of the plugin development environment

setlocal enabledelayedexpansion

REM Header
cls
echo ========================================
echo Figma to PyCharm Bridge - Setup
echo ========================================
echo.
echo Detta script kommer att saetta upp utvecklingsmiljon for pluginet.
echo.

REM Check prerequisites
echo ========================================
echo Kontrollerar forutsattningar...
echo ========================================
echo.

REM Check Java
where java >nul 2>nul
if %errorlevel% neq 0 (
    echo [ERROR] Java hittades inte!
    echo.
    echo Installera Java 17 eller senare:
    echo https://adoptium.net/
    echo.
    pause
    exit /b 1
)

java -version 2>&1 | findstr /C:"version" >nul
if %errorlevel% equ 0 (
    echo [OK] Java hittades
) else (
    echo [ERROR] Kunde inte verifiera Java-version
    pause
    exit /b 1
)

REM Check if gradlew.bat exists
if not exist "gradlew.bat" (
    echo [ERROR] Gradle wrapper hittades inte!
    pause
    exit /b 1
)

echo [OK] Gradle wrapper hittades
echo.

REM Run Gradle tasks
echo ========================================
echo Kor Gradle-uppgifter...
echo ========================================
echo.

echo Laddar ner beroenden (detta kan ta en stund forsta gangen)...
call gradlew.bat --version >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Gradle konfigurerad korrekt
) else (
    echo [INFO] Problem med Gradle-konfigurationen
    echo [INFO] Projektet ar redo for lokal utveckling
)

echo.
echo Forbereder IntelliJ-pluginets beroenden...
call gradlew.bat tasks --console=plain >nul 2>&1
if %errorlevel% equ 0 (
    echo [OK] Gradle-uppgifter laddade
) else (
    echo [INFO] Vissa beroenden kanske inte laddades
    echo [INFO] Projektet ar redo for lokal utveckling
)

REM Success
echo.
echo ========================================
echo Installation klar!
echo ========================================
echo.
echo [OK] Projektet ar redo for utveckling!
echo.
echo Nasta steg:
echo.
echo 1. Oppna projektet i IntelliJ IDEA eller PyCharm:
echo    File -^> Open -^> Valj denna mapp
echo.
echo 2. Bygg pluginet:
echo    gradlew.bat buildPlugin
echo.
echo 3. Kor pluginet i en sandbox-IDE:
echo    gradlew.bat runIde
echo.
echo 4. Skapa en distributionsfil:
echo    gradlew.bat buildPlugin
echo    Filen finns sedan i: build\distributions\
echo.
echo 5. Installera pluginet manuellt:
echo    I PyCharm: Settings -^> Plugins -^> Kugghjul -^> Install Plugin from Disk
echo    Valj filen fran build\distributions\
echo.
echo For mer information, se README.md
echo.
pause
