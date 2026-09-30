param (
    [Parameter(Mandatory=$true)]
    [string]$Repo,

    [Parameter(Mandatory=$true)]
    [string]$Token
)

[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

$headers = @{
    "Authorization" = "Bearer $Token"
    "Accept"        = "application/vnd.github+json"
    "User-Agent"    = "PowerShell-GitHub-Issue-Creator"
}

Write-Host "=====================================================" -ForegroundColor Cyan
Write-Host "BẮT ĐẦU CẤU HÌNH GITHUB BACKLOG CHO REPO: $Repo" -ForegroundColor Yellow
Write-Host "=====================================================" -ForegroundColor Cyan

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$dataPath = Join-Path $scriptDir "issues_data.json"

if (-not (Test-Path $dataPath)) {
    Write-Host "Khong tim thay file du lieu issues_data.json tai $dataPath" -ForegroundColor Red
    exit 1
}

$rawJson = [System.IO.File]::ReadAllText($dataPath, [System.Text.Encoding]::UTF8)
$data = $rawJson | ConvertFrom-Json

# 1. TAO LABELS
Write-Host "`n[1/3] Dang tao Labels tren GitHub..." -ForegroundColor Green
foreach ($lbl in $data.labels) {
    $body = @{
        name        = $lbl.name
        color       = $lbl.color
        description = $lbl.description
    } | ConvertTo-Json

    try {
        $null = Invoke-RestMethod -Uri "https://api.github.com/repos/$Repo/labels" -Method Post -Headers $headers -Body $body -ContentType "application/json" -ErrorAction Stop
        Write-Host "  + Da tao Label: $($lbl.name)" -ForegroundColor Gray
    } catch {
        Write-Host "  . Label da ton tai hoac bo qua: $($lbl.name)" -ForegroundColor DarkGray
    }
}

# 2. TAO MILESTONES
Write-Host "`n[2/3] Dang tao Milestones tren GitHub..." -ForegroundColor Green
$milestoneMap = @{}
foreach ($ms in $data.milestones) {
    $body = @{
        title       = $ms.title
        description = $ms.description
    } | ConvertTo-Json

    try {
        $res = Invoke-RestMethod -Uri "https://api.github.com/repos/$Repo/milestones" -Method Post -Headers $headers -Body $body -ContentType "application/json" -ErrorAction Stop
        $milestoneMap[$ms.title] = $res.number
        Write-Host "  + Da tao Milestone: $($ms.title)" -ForegroundColor Gray
    } catch {
        Write-Host "  . Milestone da ton tai hoac bo qua: $($ms.title)" -ForegroundColor DarkGray
    }
}

# Lay danh sach milestone hien co de map neu da ton tai
if ($milestoneMap.Count -eq 0) {
    try {
        $existingMs = Invoke-RestMethod -Uri "https://api.github.com/repos/$Repo/milestones" -Method Get -Headers $headers -ErrorAction SilentlyContinue
        foreach ($m in $existingMs) {
            $milestoneMap[$m.title] = $m.number
        }
    } catch {}
}

# 3. TAO 11 ISSUES
Write-Host "`n[3/3] Dang tao 11 Issues tu tai lieu SRS..." -ForegroundColor Green
foreach ($issue in $data.issues) {
    $payload = @{
        title  = $issue.title
        body   = $issue.body
        labels = $issue.labels
    }
    if ($milestoneMap.ContainsKey($issue.milestone)) {
        $payload["milestone"] = $milestoneMap[$issue.milestone]
    }

    $jsonBody = $payload | ConvertTo-Json -Depth 5
    $utf8Bytes = [System.Text.Encoding]::UTF8.GetBytes($jsonBody)

    try {
        $res = Invoke-RestMethod -Uri "https://api.github.com/repos/$Repo/issues" -Method Post -Headers $headers -Body $utf8Bytes -ContentType "application/json; charset=utf-8" -ErrorAction Stop
        Write-Host "  + Da tao Issue #$($res.number): $($issue.title)" -ForegroundColor Cyan
    } catch {
        Write-Host "  - Loi khi tao issue $($issue.title): $($_.Exception.Message)" -ForegroundColor Red
    }
}

Write-Host "`n=====================================================" -ForegroundColor Green
Write-Host "HOAN TAT KHOI TAO BACKLOG LEN GITHUB!" -ForegroundColor Green
Write-Host "Truy cap: https://github.com/$Repo/issues de xem ket qua." -ForegroundColor Yellow
Write-Host "=====================================================" -ForegroundColor Green
