# =============================================================================
# DepRisk CLI Demonstration Runner
# =============================================================================

Write-Host "================================================================" -ForegroundColor Cyan
Write-Host " DepRisk Pipeline Terminal Demonstration" -ForegroundColor Green
Write-Host "================================================================" -ForegroundColor Cyan

$health = Invoke-RestMethod -Uri "http://localhost:8085/api/health" -Method Get
Write-Host "Backend Status: $($health.status) | $($health.service)`n" -ForegroundColor Green

$repos = @(
    @{ Name = "Spring PetClinic (Maven / Java)"; Url = "https://github.com/spring-projects/spring-petclinic" },
    @{ Name = "Express.js API (npm / Node.js)"; Url = "https://github.com/expressjs/express" },
    @{ Name = "Requests Library (Python / PyPI)"; Url = "https://github.com/psf/requests" },
    @{ Name = "Vulnerable Microservice Demo"; Url = "https://github.com/devops-capstone/vulnerable-microservice-demo" }
)

foreach ($r in $repos) {
    Write-Host "----------------------------------------------------------------" -ForegroundColor Gray
    Write-Host "Scanning: $($r.Name)" -ForegroundColor Yellow
    Write-Host "URL: $($r.Url)" -ForegroundColor White
    
    $body = @{ repositoryUrl = $r.Url } | ConvertTo-Json
    $res = Invoke-RestMethod -Uri "http://localhost:8085/api/repositories/analyze" -Method Post -Body $body -ContentType "application/json"
    
    Write-Host "[+] Scanned: $($res.summary.fullName)" -ForegroundColor Green
    Write-Host "  * Total Dependencies: $($res.summary.totalDependencies)" -ForegroundColor White
    Write-Host "  * Updates Available:  $($res.summary.outdatedCount)" -ForegroundColor White
    Write-Host "  * Vulnerabilities:    $($res.summary.vulnerabilityCount) (Crit: $($res.summary.criticalCount), High: $($res.summary.highCount))" -ForegroundColor Red
    Write-Host "  * Overall Risk Level: $($res.summary.overallRiskLevel) ($($res.summary.overallRiskScore)/100)" -ForegroundColor Magenta
    Write-Host "  * Optimized Batches:  $($res.batches.Count)" -ForegroundColor Cyan
    
    Write-Host "  * Top Batches:" -ForegroundColor White
    $count = 0
    foreach ($b in $res.batches) {
        if ($count -ge 3) { break }
        Write-Host "    [$($b.batchNumber)] $($b.title) -> Risk: $($b.batchRiskLevel) ($($b.batchRiskScore))" -ForegroundColor Gray
        $count++
    }
    Write-Host ""
}

