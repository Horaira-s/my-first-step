@echo off
REM Run from project folder: double-click or run in cmd/powershell
cd /d "%~dp0"

echo Compiling Java sources...
javac -d out src\*.java Exceptions\*.java
if ERRORLEVEL 1 (
  echo.
  echo Compilation failed. Check the messages above.
  pause
  exit /b 1
)

echo Launching browser at http://localhost:8080/
start "" "http://localhost:8080/"

echo Starting Library web server (press Ctrl+C to stop)...
java -cp out src.LibraryWebServer 8080

pause
