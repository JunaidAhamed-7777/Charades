param(
    [string]$Serial,
    [switch]$SkipBuild
)

$ErrorActionPreference = 'Stop'
$projectRoot = [System.IO.Path]::GetFullPath((Join-Path $PSScriptRoot '..'))
Push-Location -LiteralPath $projectRoot
try {
    if (-not $SkipBuild) {
        & python (Join-Path $PSScriptRoot 'validate_catalog.py')
        if ($LASTEXITCODE -ne 0) { throw 'Bundled content validation failed.' }
        & python (Join-Path $PSScriptRoot 'sync_policies.py') --check
        if ($LASTEXITCODE -ne 0) { throw 'Bundled legal documents are out of sync.' }
        & (Join-Path $projectRoot 'gradlew.bat') assembleDebug
        if ($LASTEXITCODE -ne 0) { throw 'APK build failed.' }
    }

    $sdkRoot = $env:ANDROID_HOME
    if (-not $sdkRoot) { $sdkRoot = $env:ANDROID_SDK_ROOT }
    if (-not $sdkRoot) { $sdkRoot = Join-Path $env:LOCALAPPDATA 'Android\Sdk' }
    $adbPath = Join-Path $sdkRoot 'platform-tools\adb.exe'
    if (-not (Test-Path -LiteralPath $adbPath)) {
        $adbCommand = Get-Command adb -ErrorAction SilentlyContinue
        if (-not $adbCommand) { throw 'ADB not found. Install Android SDK Platform-Tools or set ANDROID_HOME.' }
        $adbPath = $adbCommand.Source
    }
    $apkPath = Join-Path $projectRoot 'app\build\outputs\apk\debug\app-debug.apk'
    if (-not (Test-Path -LiteralPath $apkPath)) { throw "APK missing: $apkPath" }

    & python (Join-Path $PSScriptRoot 'verify_apk.py') $apkPath
    if ($LASTEXITCODE -ne 0) { throw 'The APK does not contain the complete current offline content.' }

    $connected = @(& $adbPath devices | Select-Object -Skip 1 | Where-Object { $_ -match '^\S+\s+device$' } | ForEach-Object { ($_ -split '\s+')[0] })
    if ($Serial) {
        if ($connected -notcontains $Serial) { throw "Device $Serial is not connected and authorized." }
    } elseif ($connected.Count -eq 1) {
        $Serial = $connected[0]
    } else {
        throw 'Connect and authorize one device, or pass -Serial to choose a device or emulator.'
    }
    & $adbPath -s $Serial install -r $apkPath
    if ($LASTEXITCODE -ne 0) { throw 'ADB installation failed.' }
    & $adbPath -s $Serial shell am start -n com.yourdomain.freecharades/.MainActivity
    if ($LASTEXITCODE -ne 0) { throw 'The app could not be launched.' }
    Write-Host 'Charades is installed. Every deck and picture is included; you can play offline now.'
} finally {
    Pop-Location
}
