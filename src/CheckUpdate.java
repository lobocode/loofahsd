import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinNT;

public class CheckUpdate {
    private static List<Driver> drivers = new ArrayList<>();

    public static void main(String[] args) {
        // Obter lista de drivers instalados no sistema
        SetupApi.SP_DEVINFO_DATA devInfoData = new SetupApi.SP_DEVINFO_DATA();
        SetupApi.SetupDiEnumDeviceInfo(hDevInfo, 0, devInfoData);
        while (SetupApi.SetupDiEnumDeviceInfo(hDevInfo, index, devInfoData)) {
            String driverDesc = SetupApi.SetupDiGetDeviceRegistryProperty(
                    hDevInfo, devInfoData, WinNT.SPDRP_DEVICEDESC, null);
            String driverVer = SetupApi.SetupDiGetDriverInfoDetail(
                    hDevInfo, devInfoData, null, WinNT.DRIVER_INFO_VERSION);
            drivers.add(new Driver(driverDesc, driverVer));
            index++;
        }

        // Verificar se os drivers estão desatualizados
        List<Driver> outdatedDrivers = new ArrayList<>();
        for (Driver driver : drivers) {
            if (driver.isOutdated()) {
                // Adicionar driver desatualizado à lista
                outdatedDrivers.add(driver);
            }
        }

        // Gravar informações dos drivers desatualizados em um arquivo .csv
        try (FileWriter writer = new FileWriter("outdated_drivers.csv")) {
            writer.append("Nome do driver");
            writer.append(",");
            writer.append("Versão atual");
            writer.append(",");
            writer.append("Última versão disponível");
            writer.append("\n");
            
            for (Driver driver : outdatedDrivers) {
                writer.append(driver.getName());
                writer.append(",");
                writer.append(driver.getCurrentVersion());
                writer.append(",");
                writer.append(driver.getLatestVersion());
                writer.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
