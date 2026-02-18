<#
Script: remove-secret.ps1
Purpose: Install/run git-filter-repo on a mirror repo to remove a leaked secret,
          then verify the secret is no longer present.
Usage (PowerShell):
  cd <mirror-repo-dir>
  ..\scripts\remove-secret.ps1
#>

$secret = 'AIzaSyA2KlwBX3mkFo30om9LUFYQhpqLoa_BNhE'
$replacementsFile = "replacements.txt"

Write-Host "Working dir: $(Get-Location)" -ForegroundColor Cyan

# Ensure replacements.txt exists
if (-Not (Test-Path $replacementsFile)) {
    Write-Host "Creating $replacementsFile with replacement mapping..." -ForegroundColor Yellow
    "$secret==>REDACTED-API-KEY" | Out-File -Encoding utf8 $replacementsFile
} else {
    Write-Host "$replacementsFile already exists." -ForegroundColor Green
}

# Check git-filter-repo
Write-Host "Checking for git-filter-repo..." -NoNewline
try {
    & git filter-repo --version > $null 2>&1
    if ($LASTEXITCODE -eq 0) { Write-Host " found." -ForegroundColor Green }
} catch {
    Write-Host " not found." -ForegroundColor Yellow
    Write-Host "Attempting to install git-filter-repo via pip (user install)..." -ForegroundColor Cyan
    # Try pip install --user
    python -m pip install --user git-filter-repo
    if ($LASTEXITCODE -ne 0) {
        Write-Host "pip install failed or python not available.\nFallback: use the BFG repo-cleaner (manual step)." -ForegroundColor Red
        Write-Host "See https://rtyley.github.io/bfg-repo-cleaner/ for instructions." -ForegroundColor Yellow
        exit 1
    }
    Write-Host "Installed git-filter-repo. You may need to re-open PowerShell for PATH changes." -ForegroundColor Green
}

# Run git-filter-repo
Write-Host "Running git-filter-repo --replace-text $replacementsFile" -ForegroundColor Cyan
# Recommended to run in a mirror/bare repo
& git filter-repo --replace-text $replacementsFile
if ($LASTEXITCODE -ne 0) {
    Write-Host "git-filter-repo failed. Consider using BFG as alternative." -ForegroundColor Red
    exit 1
}

# Cleanup and GC
Write-Host "Cleaning reflogs and running git gc..." -ForegroundColor Cyan
& git reflog expire --expire=now --all
& git gc --prune=now --aggressive

# Verify
Write-Host "Verifying the secret is gone (git grep)..." -ForegroundColor Cyan
& git grep "$secret"
if ($LASTEXITCODE -ne 0) {
    Write-Host "Secret not found in repository (grep returned non-zero)." -ForegroundColor Green
} else {
    Write-Host "Secret still found! Investigate further." -ForegroundColor Red
}

Write-Host "Done. If successful, force-push the mirror to remote (coordinate with team):" -ForegroundColor Yellow
Write-Host "  git push --force --mirror origin" -ForegroundColor White
