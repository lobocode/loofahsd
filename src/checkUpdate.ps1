# I thought about using C# or C++ to access WMI and Windows Update API. But, powershell does it more easily.
# Defining the policy to run powershell scripts Unrestricted
$policy = Get-ExecutionPolicy

if ($policy -ne "Unrestricted") {
  Set-ExecutionPolicy Unrestricted
}

##$currentCulture = [System.Threading.Thread]::CurrentThread.CurrentCulture
#$resourceManager = [System.Resources.ResourceManager] "i18n", (Get-Module -Name MyModule).ModuleBase

#function GetResourceString($name) {
#  return $resourceManager.GetString($name, $currentCulture)
#}


# Define a function to check for outdated drivers
function Check-OutdatedDrivers {
  Write-Output (GetResourceString "installedDrivers")
  # Get a list of all installed drivers on the system
  $installedDrivers = Get-WmiObject -Class Win32_PnPSignedDriver | Select-Object DeviceName, DriverVersion

  Write-Output (GetResourceString "outdatedDrivers")
  # Initialize an array to store outdated drivers
  $outdatedDrivers = @()

  Write-Output (GetResourceString "latestVersion")
  # Loop through each installed driver
  foreach ($installedDriver in $installedDrivers) {
    # Get the latest version of the driver from Windows Update
    $latestVersion = (Get-WindowsDriver -Online | Where-Object {$_.DeviceName -eq $driverName -and $_.DriverVersion -lt $driverVersion})


    # Compare the installed version with the latest version
    if ($installedDriver.DriverVersion -lt $latestVersion) {
      # Add the outdated driver to the array
      $outdatedDrivers += $installedDriver.DeviceName

      # Write a log entry for the outdated driver
      Write-Output "$(Get-Date) - Driver $($installedDriver.DeviceName) está desatualizado. Versão instalada: $($installedDriver.DriverVersion), Última versão: $($latestVersion)" | Out-File -FilePath "C:\drivers_update.log" -Append
    }
  }

  Write-Output (GetResourceString "upgradeOutdatedDrivers")
  # Update the outdated drivers using the Windows Update API
  foreach ($outdatedDriver in $outdatedDrivers) {
    # Search for the latest version of the driver on Windows Update
    $driverUpdate = Get-WindowsDriver -Online -DeviceName $outdatedDriver | Select-Object -First 1

    # Install the latest version of the driver
    Add-WindowsDriver -Online -Driver $driverUpdate
  }
}

Write-Output (GetResourceString "checkOutdatedDriversFunction")
# Call the function to check for outdated drivers
Check-OutdatedDrivers

## TODO
# Write an efficient translation system
