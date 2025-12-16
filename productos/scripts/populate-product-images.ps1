param(
    [string]$ApiUrl = 'http://localhost:8083/api/products',
    [string]$UploadDir = 'src\main\java\com\fixsy\productos\images',
    [switch]$Force
)

$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Definition
$projectDir = Split-Path -Parent $scriptDir
$imagesPath = Join-Path $projectDir $UploadDir
if (-not (Test-Path $imagesPath)) {
    Write-Error "Upload directory not found: $imagesPath"
    return
}
$imagesDir = (Resolve-Path $imagesPath).ProviderPath

$seedMap = @{
    'filtros'      = 'filtros2.webp'
    'frenos'       = 'png-clipart-car-brake-pad-disc-brake-vehicle-car-car-automobile-repair-shop.png'
    'suspension'   = 'suspension.png'
    'electricidad' = 'electricidad.jpg'
    'lubricantes'  = 'png-transparent-car-oil-motor-oil-lubricant-engine-twostroke-engine-lubrication-base.png'
    'motor'        = 'pngtree-truck-fuel-oil-filter-png-image_11484952.png'
    'combustible'  = 'tres-filtros-aceite-motor-automovil_207928-40.avif'
    'default'      = 'images.jpg'
}

Write-Host "Fetching products from $ApiUrl"
try {
    $products = Invoke-RestMethod -Uri $ApiUrl
} catch {
    $message = $_.Exception.Message
    Write-Error ("Cannot connect to {0}: {1}" -f $ApiUrl, $message)
    return
}

if (-not $products) {
    Write-Warning "No products returned from $ApiUrl."
    return
}

Write-Host "Using image directory: $imagesDir"

$copied = @()
$missingSeeds = @()

foreach ($product in $products) {
    if (-not $product.imageUrl) { continue }
    $fileName = [System.IO.Path]::GetFileName([uri]$product.imageUrl)
    $targetPath = Join-Path $imagesDir $fileName

    if ((Test-Path $targetPath) -and (-not $Force)) {
        Write-Host "$fileName already exists; skipping"
        continue
    }

    $categoryKey = $null
    if ($product.categoria) {
        $categoryKey = ($product.categoria -as [string]).ToLowerInvariant()
    }
    $seedName = $seedMap[$categoryKey]
    if (-not $seedName) {
        $seedName = $seedMap['default']
    }

    $seedPath = Join-Path $imagesDir $seedName
    if (-not (Test-Path $seedPath)) {
        $missingSeeds += $seedName
        Write-Warning "Missing seed: $seedPath (product $($product.id))"
        continue
    }

    Copy-Item -Path $seedPath -Destination $targetPath -Force
    $copied += $fileName
    Write-Host "Copied $seedName -> $fileName"
}

if ($missingSeeds) {
    Write-Warning "Seeds missing: $(($missingSeeds | Sort-Object -Unique) -join ', ')"
}

if ($copied) {
    Write-Host "Created files:"
    $copied | ForEach-Object { Write-Host "  $_" }
} else {
    Write-Host "No new files were copied."
}
