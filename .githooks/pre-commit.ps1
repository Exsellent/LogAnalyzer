# Отключаем вывод команд
$ErrorActionPreference = "Stop"

Write-Host "Running Checkstyle..."
mvn checkstyle:check

if ($LASTEXITCODE -ne 0) {
    Write-Host "Checkstyle failed, commit aborted."
    exit 1
}

Write-Host "Checkstyle passed, formatting code..."
mvn formatter:format

if ($LASTEXITCODE -ne 0) {
    Write-Host "Formatting failed, commit aborted."
    exit 1
}

Write-Host "Pre-commit checks passed successfully."
exit 0
