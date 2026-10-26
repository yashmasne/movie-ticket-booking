# ==============================================================
# CinePass - Movie Ticket Management System PowerShell Launcher
# ==============================================================
param(
    [switch]$H2,
    [switch]$Build,
    [switch]$Help
)

if ($Help) {
    Write-Host "Usage: .\run.ps1 [-H2] [-Build] [-Help]" -ForegroundColor Yellow
    Write-Host "  -H2     : Run with H2 In-Memory DB (zero MySQL configuration needed)"
    Write-Host "  -Build  : Rebuild the project using Maven before launching"
    exit 0
}

Write-Host "==============================================================" -ForegroundColor Cyan
Write-Host "   CinePass - Movie Ticket Management System" -ForegroundColor Green
Write-Host "   Java Spring Boot + MySQL + HTML/CSS/JavaScript" -ForegroundColor Green
Write-Host "==============================================================" -ForegroundColor Cyan
Write-Host ""

# Check and free port 8080 if an old instance is still running
$busy8080 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
if ($busy8080) {
    Write-Host "[*] Port 8080 is currently occupied. Stopping previous instance..." -ForegroundColor Yellow
    foreach ($conn in $busy8080) {
        if ($conn.OwningProcess -and $conn.OwningProcess -ne 0) {
            Stop-Process -Id $conn.OwningProcess -Force -ErrorAction SilentlyContinue
        }
    }
    Start-Sleep -Seconds 1
}

$useH2 = $H2.IsPresent

if (-not $useH2) {
    # Check if MySQL is listening on port 3306
    $portInUse = Get-NetTCPConnection -LocalPort 3306 -ErrorAction SilentlyContinue
    if (-not $portInUse) {
        if (Test-Path "C:\xampp\mysql\bin\mysqld.exe") {
            Write-Host "[*] Port 3306 is inactive. Starting MySQL Server from XAMPP..." -ForegroundColor Yellow
            Start-Process -FilePath "C:\xampp\mysql\bin\mysqld.exe" -ArgumentList "--defaults-file=mysql\bin\my.ini", "--standalone" -WorkingDirectory "C:\xampp" -WindowStyle Hidden
            $retries = 15
            while ($retries -gt 0 -and -not (Get-NetTCPConnection -LocalPort 3306 -ErrorAction SilentlyContinue)) {
                Start-Sleep -Seconds 1
                $retries--
            }
            if (Get-NetTCPConnection -LocalPort 3306 -ErrorAction SilentlyContinue) {
                Write-Host "[*] MySQL Server started successfully on port 3306." -ForegroundColor Green
            } else {
                Write-Host "[!] Warning: MySQL port 3306 not responding after start attempt." -ForegroundColor Red
                Write-Host "[*] Auto-switching to H2 In-Memory Database for seamless execution..." -ForegroundColor Yellow
                $useH2 = $true
            }
        } else {
            Write-Host "[!] Warning: MySQL not found on port 3306 and XAMPP not found." -ForegroundColor Yellow
            Write-Host "[*] Auto-switching to H2 In-Memory Database for seamless execution..." -ForegroundColor Yellow
            $useH2 = $true
        }
    } else {
        Write-Host "[*] MySQL service detected on port 3306." -ForegroundColor Green
        
        # Test if MySQL connection succeeds with configured credentials
        $dbUser = "root"
        $dbPass = ""
        $propsFile = Join-Path $PSScriptRoot "src\main\resources\application.properties"
        if (Test-Path $propsFile) {
            Get-Content $propsFile | ForEach-Object {
                if ($_ -match '^\s*spring\.datasource\.username\s*=\s*(.*)$') { $dbUser = $matches[1].Trim() }
                if ($_ -match '^\s*spring\.datasource\.password\s*=\s*(.*)$') { $dbPass = $matches[1].Trim() }
            }
        }
        
        $mysqlCli = (Get-Command mysql -ErrorAction SilentlyContinue | Select-Object -ExpandProperty Source)
        if (-not $mysqlCli -and (Test-Path "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe")) {
            $mysqlCli = "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe"
        }
        if (-not $mysqlCli -and (Test-Path "C:\xampp\mysql\bin\mysql.exe")) {
            $mysqlCli = "C:\xampp\mysql\bin\mysql.exe"
        }
        
        if ($mysqlCli) {
            $cliArgs = @("-u", $dbUser)
            if ($dbPass -ne "") { $cliArgs += "-p$dbPass" }
            $cliArgs += @("-e", "SELECT 1;")
            & $mysqlCli @cliArgs 2>&1 | Out-Null
            if ($LASTEXITCODE -ne 0) {
                Write-Host "[!] Note: MySQL root requires a password not configured in application.properties." -ForegroundColor Yellow
                Write-Host "[*] Auto-switching to H2 In-Memory Database for zero-setup execution..." -ForegroundColor Yellow
                $useH2 = $true
            } else {
                Write-Host "[*] MySQL authentication successful for user '$dbUser'." -ForegroundColor Green
            }
        }
    }
}

# Optional build step
if ($Build) {
    Write-Host "[*] Building application with Maven..." -ForegroundColor Cyan
    & "$PSScriptRoot\mvnw.cmd" clean package -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Host "[!] Build failed. Exiting." -ForegroundColor Red
        exit $LASTEXITCODE
    }
}

Write-Host ""
$jarPath = Join-Path $PSScriptRoot "target\movie-ticket-booking-1.0.0.jar"
$profileArg = if ($useH2) { "--spring.profiles.active=h2" } else { "" }

if ($useH2) {
    Write-Host "[*] Running mode: H2 In-Memory Database (Zero MySQL setup needed)" -ForegroundColor Magenta
} else {
    Write-Host "[*] Running mode: MySQL Database (port 3306)" -ForegroundColor Green
}

if (Test-Path $jarPath) {
    Write-Host "[*] Launching application from target\movie-ticket-booking-1.0.0.jar..." -ForegroundColor Cyan
    if ($profileArg) {
        java -jar "$jarPath" $profileArg
    } else {
        java -jar "$jarPath"
        if ($LASTEXITCODE -ne 0) {
            Write-Host "[!] MySQL execution encountered an error. Falling back to H2 In-Memory Database..." -ForegroundColor Yellow
            java -jar "$jarPath" --spring.profiles.active=h2
        }
    }
} else {
    Write-Host "[*] JAR not found. Launching via Maven wrapper..." -ForegroundColor Cyan
    $mvnArgs = @("spring-boot:run")
    if ($profileArg) {
        $mvnArgs += "-Dspring-boot.run.arguments=$profileArg"
    }
    & "$PSScriptRoot\mvnw.cmd" @mvnArgs
}
