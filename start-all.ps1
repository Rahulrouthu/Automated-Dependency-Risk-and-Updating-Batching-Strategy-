# =============================================================================
# Automated Dependency Risk & Batching Platform - Full Stack Launcher
# =============================================================================

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " 🛡️ DepRisk: Automated Dependency Risk & Batching Platform" -ForegroundColor Green
Write-Host " Capstone Engineering Project - DevOps Domain" -ForegroundColor Yellow
Write-Host "================================================================" -ForegroundColor Cyan

$rootDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendDir = Join-Path $rootDir "backend"
$frontendDir = Join-Path $rootDir "frontend"

Write-Host "`n[1/3] Checking Spring Boot Backend..." -ForegroundColor White
$mvnw = Join-Path $backendDir "mvnw.cmd"

# Start Backend in new process window
Write-Host "Starting Spring Boot Backend on http://localhost:8085..." -ForegroundColor Green
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$backendDir'; .\mvnw.cmd spring-boot:run" -WindowStyle Normal

# Wait for backend health
Write-Host "`n[2/3] Waiting for Backend to initialize..." -ForegroundColor White
$retries = 20
$backendReady = $false
while ($retries -gt 0) {
    try {
        $health = Invoke-RestMethod -Uri "http://localhost:8085/api/health" -Method Get -ErrorAction SilentlyContinue
        if ($health.status -eq "UP") {
            $backendReady = $true
            break
        }
    } catch {}
    Start-Sleep -Seconds 1
    $retries--
}

if ($backendReady) {
    Write-Host "✓ Backend is UP and Healthy on http://localhost:8085" -ForegroundColor Green
} else {
    Write-Host "⚠️ Backend is initializing in background window." -ForegroundColor Yellow
}

# Start Frontend
Write-Host "`n[3/3] Starting React 18 + Vite Frontend Dashboard..." -ForegroundColor White
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$frontendDir'; npm run dev" -WindowStyle Normal

Start-Sleep -Seconds 3
Write-Host "`n🚀 DepRisk Dashboard is ready!" -ForegroundColor Green
Write-Host "🌐 Opening Browser at: http://localhost:5173" -ForegroundColor Cyan
Start-Process "http://localhost:5173"
