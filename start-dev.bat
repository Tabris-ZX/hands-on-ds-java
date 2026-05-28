@echo off
setlocal

cd /d "%~dp0"

where npm >nul 2>nul
if errorlevel 1 (
    echo [ERROR] npm was not found in PATH, cannot start frontend.
    goto start_backend
)

if not exist "frontend\package.json" (
    echo [ERROR] frontend\package.json was not found.
    goto start_backend
)

if not exist "frontend\node_modules" (
    echo [INFO] frontend\node_modules was not found. Run "npm install" in the frontend directory first.
    goto start_backend
)

echo [INFO] Starting frontend dev server...
start "frontend-dev" cmd /k "cd /d "%~dp0frontend" && npm run dev"

:start_backend
where mvn >nul 2>nul
if errorlevel 1 (
    echo [ERROR] Maven was not found in PATH, cannot start backend.
    exit /b 1
)

echo [INFO] Starting backend...
start "backend-dev" cmd /k "cd /d "%~dp0" && mvn spring-boot:run"

echo [INFO] Frontend and backend start commands have been sent.
exit /b 0
