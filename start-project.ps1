param(
    [Alias("DbUsername", "MysqlUser", "Username")]
    [string]$DbUser = $(if ($env:DB_USERNAME) { $env:DB_USERNAME } else { "root" }),
    [Alias("MysqlPassword")]
    [string]$DbPassword = $(if ($env:DB_PASSWORD) { $env:DB_PASSWORD } else { "" }),
    [Alias("Port")]
    [string]$ServerPort = $(if ($env:SERVER_PORT) { $env:SERVER_PORT } else { "8080" }),
    [string]$MailUsername = "",
    [string]$MailPassword = "",
    [string]$MailFrom = "",
    [switch]$NoBrowser
)

$ErrorActionPreference = "Stop"

function Step($message) {
    Write-Host "`n[STEP] $message" -ForegroundColor Cyan
}

function Ok($message) {
    Write-Host "[OK] $message" -ForegroundColor Green
}

function Warn($message) {
    Write-Host "[WARN] $message" -ForegroundColor Yellow
}

function Fail($message) {
    Write-Host "[ERROR] $message" -ForegroundColor Red
    exit 1
}

function ConvertTo-PlainText([Security.SecureString]$secureValue) {
    $ptr = [Runtime.InteropServices.Marshal]::SecureStringToBSTR($secureValue)
    try {
        return [Runtime.InteropServices.Marshal]::PtrToStringBSTR($ptr)
    }
    finally {
        [Runtime.InteropServices.Marshal]::ZeroFreeBSTR($ptr)
    }
}

$projectRoot = Split-Path -Parent $MyInvocation.MyCommand.Path
$backendDir = Join-Path $projectRoot "backend"
$sqlFile = Join-Path $backendDir "database\mondelys_db.sql"
$mysqlBin = "C:\tools\mysql\current\bin"
$defaultDbUrl = "jdbc:mysql://localhost:3306/mondelys_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Africa/Tunis"
$envDbUrl = if ($env:DB_URL) { $env:DB_URL.Trim() } else { "" }
$dbUrl = $defaultDbUrl
$serverPort = $ServerPort

if ($envDbUrl) {
    if ($envDbUrl -like "jdbc:mysql:*") {
        $dbUrl = $envDbUrl
    }
    elseif ($envDbUrl -like "jdbc:h2:*") {
        Warn "DB_URL H2 detecte dans l'environnement. Utilisation automatique de MySQL pour cette execution."
    }
    else {
        Fail "DB_URL non supporte: $envDbUrl"
    }
}

$usesMySql = $dbUrl -like "jdbc:mysql:*"

if (-not (Test-Path $backendDir)) {
    Fail "Dossier backend introuvable: $backendDir"
}

if (-not (Get-Command java -ErrorAction SilentlyContinue)) {
    Fail "Java introuvable. Installez Java 17+ puis relancez."
}

if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    Fail "Maven introuvable. Installez Maven puis relancez."
}

Step "Nettoyage du serveur (port $serverPort)"
$listener = Get-NetTCPConnection -LocalPort $serverPort -State Listen -ErrorAction SilentlyContinue | Select-Object -First 1
if ($listener) {
    $procId = $listener.OwningProcess
    try {
        $proc = Get-Process -Id $procId -ErrorAction Stop
        Stop-Process -Id $procId -Force
        Ok "Processus arrete sur ${serverPort}: $($proc.ProcessName) (PID $procId)"
    }
    catch {
        Warn "Impossible d'arreter PID $procId."
    }
}
else {
    Ok "Aucun processus sur le port $serverPort"
}

Step "Initialisation base de donnees"
if (-not $usesMySql) {
    Fail "Ce script est configure pour MySQL uniquement. Definissez DB_URL avec un jdbc:mysql:..."
}

Step "Preparation MySQL CLI"
if (Test-Path (Join-Path $mysqlBin "mysql.exe")) {
    $env:Path += ";$mysqlBin"
    Ok "MySQL CLI detecte dans $mysqlBin"
}
elseif (-not (Get-Command mysql -ErrorAction SilentlyContinue)) {
    Fail "mysql CLI introuvable. Installez MySQL CLI ou ajoutez-le au PATH."
}

$mysqlCommand = Get-Command mysql -ErrorAction SilentlyContinue
if ($mysqlCommand -and (Test-Path $sqlFile)) {
    try {
        $env:MYSQL_PWD = $DbPassword
        Get-Content $sqlFile | mysql -u $DbUser | Out-Null
        if ($LASTEXITCODE -ne 0) {
            throw "mysql returned $LASTEXITCODE"
        }
        Ok "Base mondelys_db et tables pretes"
    }
    catch {
        Fail "Echec initialisation SQL. Verifiez DbUser/DbPassword et que MySQL tourne bien sur localhost:3306."
    }
}
else {
    Fail "Initialisation SQL impossible: mysql CLI ou fichier SQL manquant."
}

Step "Compilation backend"
Push-Location $backendDir
try {
    mvn -DskipTests compile
    if ($LASTEXITCODE -ne 0) {
        throw "compile failed"
    }
    Ok "Compilation reussie"
}
catch {
    Pop-Location
    Fail "Compilation Maven echouee"
}

Step "Lancement backend Spring Boot"
$env:DB_URL = $dbUrl
$env:SERVER_PORT = $serverPort
$env:DB_DRIVER = "com.mysql.cj.jdbc.Driver"
$env:DB_USERNAME = $DbUser
$env:DB_PASSWORD = $DbPassword

if (-not $MailUsername) {
    $MailUsername = $env:MAIL_USERNAME
}
if (-not $MailPassword) {
    $MailPassword = $env:MAIL_PASSWORD
}

if (-not $MailUsername) {
    $MailUsername = Read-Host "Email Gmail pour l'envoi des reponses (laisser vide pour ignorer)"
}

if ($MailUsername -and -not $MailPassword) {
    $secureMailPassword = Read-Host "Mot de passe d'application Gmail" -AsSecureString
    $MailPassword = ConvertTo-PlainText $secureMailPassword
}

if ($MailUsername -and $MailPassword) {
    if (-not $MailFrom) {
        $MailFrom = $MailUsername
    }

    $env:MAIL_HOST = "smtp.gmail.com"
    $env:MAIL_PORT = "587"
    $env:MAIL_SMTP_AUTH = "true"
    $env:MAIL_SMTP_STARTTLS = "true"
    $env:MAIL_USERNAME = $MailUsername
    $env:MAIL_PASSWORD = $MailPassword
    $env:MAIL_FROM = $MailFrom

    Ok "SMTP Gmail configure pour l'envoi de reponses clients"
}
else {
    Warn "SMTP non configure: la reponse email admin->client sera indisponible"
}

if (-not $env:JWT_SECRET) {
    $env:JWT_SECRET = "replace_with_a_very_long_random_secret_key_at_least_32_chars"
}

Write-Host "`n===============================" -ForegroundColor DarkCyan
Write-Host "SITE:   http://localhost:$serverPort/index.html" -ForegroundColor White
Write-Host "ADMIN:  http://localhost:$serverPort/admin" -ForegroundColor White
Write-Host "LOGIN:  admin@mondelys.tn" -ForegroundColor White
Write-Host "PASS:   Admin2026!" -ForegroundColor White
Write-Host "===============================`n" -ForegroundColor DarkCyan

if (-not $NoBrowser) {
    Start-Process "http://localhost:$serverPort/admin" | Out-Null
}

mvn spring-boot:run
$runExitCode = $LASTEXITCODE
Pop-Location
exit $runExitCode
