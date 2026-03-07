$ErrorActionPreference = "Stop"

$outputDir = "output/pdf"
$outputPath = Join-Path $outputDir "lokigame-app-summary.pdf"

if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}

function Escape-PdfText {
    param([string]$Text)
    return $Text.Replace("\", "\\").Replace("(", "\(").Replace(")", "\)")
}

function Wrap-Line {
    param(
        [string]$Text,
        [int]$MaxLen,
        [string]$Indent = ""
    )

    if ([string]::IsNullOrWhiteSpace($Text)) {
        return @("")
    }

    $words = $Text -split "\s+"
    $out = New-Object System.Collections.Generic.List[string]
    $current = ""

    foreach ($w in $words) {
        $candidate = if ($current.Length -eq 0) { $w } else { "$current $w" }
        $limit = if ($out.Count -eq 0) { $MaxLen } else { [Math]::Max(10, $MaxLen - $Indent.Length) }

        if ($candidate.Length -le $limit) {
            $current = $candidate
        } else {
            if ($current.Length -gt 0) {
                $out.Add($current)
            }
            $current = if ($out.Count -eq 0) { $w } else { "$Indent$w" }
        }
    }

    if ($current.Length -gt 0) {
        $out.Add($current)
    }

    return $out
}

$sourceLines = @(
    @{ Font = "F2"; Size = 16; Text = "LokiGame App Summary"; Wrap = $false },
    @{ Font = "F1"; Size = 9; Text = "Generated from repository evidence only (code, config, and docs in this repo)."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = ""; Wrap = $false },

    @{ Font = "F2"; Size = 11; Text = "What It Is"; Wrap = $false },
    @{ Font = "F1"; Size = 9; Text = "LokiGame is a Spring Boot backend for a server-authoritative RPG/gacha-style game."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "It provides authenticated REST APIs for hero, dungeon, battle, inventory, and economy systems."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = ""; Wrap = $false },

    @{ Font = "F2"; Size = 11; Text = "Who It Is For"; Wrap = $false },
    @{ Font = "F1"; Size = 9; Text = "Primary persona: game client/backend developers integrating and operating a live game API backend."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = ""; Wrap = $false },

    @{ Font = "F2"; Size = 11; Text = "What It Does"; Wrap = $false },
    @{ Font = "F1"; Size = 9; Text = "- JWT auth APIs (register/login/refresh) with Spring Security filters and role checks."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Hero workflows, including class/origin/world data and hero roll/management endpoints."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Dungeon and dungeon-run orchestration, including seeds, scaling, and rewards paths."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Battle simulation endpoints backed by battle service/engine classes."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Inventory/equipment flows with generated equipment and owned inventory items."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Economy/admin flows for currency request submission and review/approval endpoints."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Session tracking hooks with Redis configured in application properties."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = ""; Wrap = $false },

    @{ Font = "F2"; Size = 11; Text = "How It Works (Architecture)"; Wrap = $false },
    @{ Font = "F1"; Size = 9; Text = "- API layer: controllers under src/main/java/.../controller expose REST endpoints."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Business layer: services under .../service implement game logic and orchestration."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Core logic: generator (HeroFactory, EquipmentGenerator) and engine (BattleEngine)."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Data layer: Spring Data repositories persist entities to PostgreSQL (JPA/Hibernate)."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Security layer: SecurityConfig + JwtAuthenticationFilter + JwtTokenProvider."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Startup/data flow: GameDataInitialize loads static game data from resources/data/*.json."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "- Infra services: Redis configured; exact production cache/session strategy Not found in repo."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = ""; Wrap = $false },

    @{ Font = "F2"; Size = 11; Text = "How To Run (Minimal)"; Wrap = $false },
    @{ Font = "F1"; Size = 9; Text = "1. In project root, run: docker-compose up --build"; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "2. Wait for backend startup logs (service on port 8080)."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "3. Verify API: http://localhost:8080/swagger-ui/index.html or /actuator/health."; Wrap = $true },
    @{ Font = "F1"; Size = 9; Text = "4. Alternative local run: set PostgreSQL/Redis env vars, then run mvn spring-boot:run."; Wrap = $true }
)

$renderLines = New-Object System.Collections.Generic.List[object]
foreach ($line in $sourceLines) {
    if (-not $line.Wrap) {
        $renderLines.Add($line)
        continue
    }

    $indent = ""
    if ($line.Text.StartsWith("- ")) { $indent = "  " }
    if ($line.Text -match "^[0-9]+\. ") { $indent = "   " }

    $wrapped = Wrap-Line -Text $line.Text -MaxLen 92 -Indent $indent
    foreach ($w in $wrapped) {
        $renderLines.Add(@{ Font = $line.Font; Size = $line.Size; Text = $w })
    }
}

$content = New-Object System.Text.StringBuilder
$null = $content.AppendLine("BT")
$y = 770
foreach ($line in $renderLines) {
    $font = $line.Font
    $size = $line.Size
    $text = Escape-PdfText $line.Text
    $null = $content.AppendLine("/$font $size Tf")
    $null = $content.AppendLine("1 0 0 1 40 $y Tm ($text) Tj")
    $y -= 14
}
$null = $content.AppendLine("ET")

$stream = $content.ToString()
$streamLength = [System.Text.Encoding]::ASCII.GetByteCount($stream)

$objects = @(
    "<< /Type /Catalog /Pages 2 0 R >>",
    "<< /Type /Pages /Kids [3 0 R] /Count 1 >>",
    "<< /Type /Page /Parent 2 0 R /MediaBox [0 0 612 792] /Resources << /Font << /F1 4 0 R /F2 6 0 R >> >> /Contents 5 0 R >>",
    "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>",
    "<< /Length $streamLength >>`nstream`n$stream`nendstream",
    "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica-Bold >>"
)

$pdf = New-Object System.Text.StringBuilder
$null = $pdf.Append("%PDF-1.4`n")
$offsets = @()

for ($i = 0; $i -lt $objects.Count; $i++) {
    $offset = [System.Text.Encoding]::ASCII.GetByteCount($pdf.ToString())
    $offsets += $offset
    $objNum = $i + 1
    $null = $pdf.Append("$objNum 0 obj`n")
    $null = $pdf.Append($objects[$i])
    $null = $pdf.Append("`nendobj`n")
}

$xrefOffset = [System.Text.Encoding]::ASCII.GetByteCount($pdf.ToString())
$null = $pdf.Append("xref`n")
$null = $pdf.Append("0 $($objects.Count + 1)`n")
$null = $pdf.Append("0000000000 65535 f `n")
foreach ($off in $offsets) {
    $null = $pdf.Append(("{0:0000000000} 00000 n `n" -f $off))
}
$null = $pdf.Append("trailer`n")
$null = $pdf.Append("<< /Size $($objects.Count + 1) /Root 1 0 R >>`n")
$null = $pdf.Append("startxref`n")
$null = $pdf.Append("$xrefOffset`n")
$null = $pdf.Append("%%EOF`n")

$resolvedOutput = (Resolve-Path $outputDir).Path
$finalPath = Join-Path $resolvedOutput "lokigame-app-summary.pdf"
[System.IO.File]::WriteAllBytes($finalPath, [System.Text.Encoding]::ASCII.GetBytes($pdf.ToString()))

Write-Output "LinesRendered=$($renderLines.Count)"
Write-Output $finalPath
