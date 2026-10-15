# Download JDK 21 and the Android SDK packages this project builds with.
# Files land in .build-env\ next to this script. local.properties points Gradle at that SDK.
$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
$EnvDir = Join-Path $Root ".build-env"
$SdkDir = Join-Path $EnvDir "android-sdk"
$JdkDir = Join-Path $EnvDir "jdk"
$CmdlineRev = "14742923"
$Packages = @(
    "platforms;android-37.0",
    "build-tools;37.0.0",
    "build-tools;36.0.0",
    "platform-tools"
)

function Get-JavaMajor {
    $java = Get-Command java -ErrorAction SilentlyContinue
    if (-not $java) { return 0 }
    $previous = $ErrorActionPreference
    $ErrorActionPreference = "Continue"
    $text = (& java -version 2>&1 | Out-String)
    $ErrorActionPreference = $previous
    if ($text -match 'version "(\d+)') { return [int]$Matches[1] }
    return 0
}

New-Item -ItemType Directory -Force -Path $EnvDir | Out-Null

$javaMajor = Get-JavaMajor
if ($javaMajor -ge 21) {
    Write-Host "已有 JDK $javaMajor，跳过 JDK 下载"
} else {
    $arch = if ([Environment]::Is64BitOperatingSystem) { "x64" } else {
        throw "需要 64 位 Windows"
    }
    Write-Host "下载 Temurin JDK 21 (windows/$arch)"
    $zip = Join-Path $EnvDir "jdk.zip"
    Invoke-WebRequest -Uri "https://api.adoptium.net/v3/binary/latest/21/ga/windows/$arch/jdk/hotspot/normal/eclipse?project=jdk" -OutFile $zip
    if (Test-Path $JdkDir) { Remove-Item -Recurse -Force $JdkDir }
    $expand = Join-Path $EnvDir "jdk-expand"
    if (Test-Path $expand) { Remove-Item -Recurse -Force $expand }
    Expand-Archive -Path $zip -DestinationPath $expand
    $homeDir = Get-ChildItem $expand -Directory | Select-Object -First 1
    Move-Item $homeDir.FullName $JdkDir
    Remove-Item -Recurse -Force $expand
    Remove-Item -Force $zip
    $env:JAVA_HOME = $JdkDir
    $env:Path = "$(Join-Path $JdkDir 'bin');$env:Path"
}

$sdkmanager = Join-Path $SdkDir "cmdline-tools\latest\bin\sdkmanager.bat"
if (-not (Test-Path $sdkmanager)) {
    Write-Host "下载 Android 命令行工具"
    $zip = Join-Path $EnvDir "cmdline-tools.zip"
    Invoke-WebRequest -Uri "https://dl.google.com/android/repository/commandlinetools-win-$CmdlineRev`_latest.zip" -OutFile $zip
    $expand = Join-Path $EnvDir "cmdline-expand"
    if (Test-Path $expand) { Remove-Item -Recurse -Force $expand }
    Expand-Archive -Path $zip -DestinationPath $expand
    $latest = Join-Path $SdkDir "cmdline-tools\latest"
    New-Item -ItemType Directory -Force -Path (Split-Path $latest) | Out-Null
    if (Test-Path $latest) { Remove-Item -Recurse -Force $latest }
    $inner = Join-Path $expand "cmdline-tools"
    if (Test-Path $inner) {
        Move-Item $inner $latest
    } else {
        Move-Item $expand $latest
    }
    if (Test-Path $expand) { Remove-Item -Recurse -Force $expand }
    Remove-Item -Force $zip
}

Write-Host "安装 Android SDK 组件: $($Packages -join ' ')"
$yes = (1..200 | ForEach-Object { "y" }) -join "`n"
$yes | & $sdkmanager --sdk_root=$SdkDir --licenses | Out-Null
& $sdkmanager --sdk_root=$SdkDir @Packages
if ($LASTEXITCODE -ne 0) { throw "sdkmanager 安装失败，退出码 $LASTEXITCODE" }

$sdkPath = ($SdkDir -replace '\\', '/')
Set-Content -Path (Join-Path $Root "V2rayNG\local.properties") -Value "sdk.dir=$sdkPath" -Encoding ascii
Write-Host "构建环境已放在 $EnvDir"
Write-Host "下一步: cd V2rayNG; .\gradlew.bat :app:assemblePlaystoreDebug `"-PABI_FILTERS=arm64-v8a`""
