@echo off
setlocal enabledelayedexpansion
echo [JosheJung Helper] Starting Robust Build...

:: 1. Set Project Root (FIXED: Corrected the variable syntax)
set "PROJECT_DIR=%~dp0"
set "LIB_PATH=%PROJECT_DIR%lib"
set "BIN_PATH=%PROJECT_DIR%bin"
set "SRC_PATH=%PROJECT_DIR%src"

:: Debug: Let's see the paths to ensure they are correct
echo [DEBUG] Project Dir: "%PROJECT_DIR%"
echo [DEBUG] Looking for source in: "%SRC_PATH%"

:: 2. Create bin folder if missing
if not exist "%BIN_PATH%" (
mkdir "%BIN_PATH%"
)

:: 3. Compilation Step
echo [JosheJung Helper] Compiling classes from src...
cd /d "%SRC_PATH%"
javac --module-path "%LIB_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.media -d "%BIN_PATH%" *.java
set COMPILATION_ERRORLEVEL=%errorlevel%
cd /d "%PROJECT_DIR%"

if %COMPILATION_ERRORLEVEL% neq 0 (
echo.
echo [ERROR] Compilation failed!
echo Please ensure your .java files are inside the 'src' folder.
echo Current Path: %CD%
pause
exit /b %COMPILATION_ERRORLEVEL%
)

echo [JosheJung Helper] Compilation Successful.
echo [JosheJung Helper] Launching Josh-e-Jung...

:: 4. Execution Step
java --module-path "%LIB_PATH%" --add-modules javafx.controls,javafx.fxml,javafx.media "-Djava.library.path=%LIB_PATH%" --enable-native-access=javafx.graphics -cp "%BIN_PATH%" App 2> "%PROJECT_DIR%error_log.txt"

if %errorlevel% neq 0 (
echo.
echo [ERROR] Runtime Crash detected!
echo Printing error_log.txt:
echo --------------------------------------------------
if exist error_log.txt (
type error_log.txt
) else (
echo Error log file not found, but Java returned an error code.
)
echo --------------------------------------------------
pause
)

echo.
echo [JosheJung Helper] Process Finished.
pause