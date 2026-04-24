param(
  [string]$HostName = "localhost",
  [int]$Port = 3306,
  [string]$User = "root",
  [string]$Database = "mondelys_db",
  [string]$Password = "",
  [switch]$ResetExisting
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$sqlFile = Join-Path $scriptDir "menu_seed.sql"

if (-not (Test-Path $sqlFile)) {
  throw "Fichier introuvable: $sqlFile"
}

$mysqlCommand = Get-Command mysql -ErrorAction SilentlyContinue
if (-not $mysqlCommand) {
  throw "La commande mysql est introuvable. Ouvrez MySQL Workbench ou ajoutez mysql.exe au PATH."
}

$arguments = @(
  "--host=$HostName",
  "--port=$Port",
  "--user=$User",
  $Database
)

try {
  if ($Password) {
    $env:MYSQL_PWD = $Password
  }

  $countResult = & $mysqlCommand.Source @arguments -N -B -e "SELECT COUNT(*) FROM menu"
  if ($LASTEXITCODE -ne 0) {
    throw "Impossible de verifier l'etat actuel de la table menu."
  }

  $currentCount = [int]($countResult | Select-Object -First 1)
  if ($currentCount -gt 0 -and -not $ResetExisting) {
    throw "La table menu contient deja $currentCount ligne(s). Relancez avec -ResetExisting pour recharger le seed sans doublons."
  }

  if ($currentCount -gt 0 -and $ResetExisting) {
    & $mysqlCommand.Source @arguments -e "DELETE FROM menu"
    if ($LASTEXITCODE -ne 0) {
      throw "Impossible de vider la table menu avant reimport."
    }
  }

  Get-Content -Raw -Path $sqlFile | & $mysqlCommand.Source @arguments
}
finally {
  if ($Password) {
    Remove-Item Env:MYSQL_PWD -ErrorAction SilentlyContinue
  }
}

if ($LASTEXITCODE -ne 0) {
  throw "L'import du menu a echoue."
}

Write-Host "Import termine avec succes dans $Database." -ForegroundColor Green
