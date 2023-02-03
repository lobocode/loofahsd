import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinNT;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;


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
        try (FileWriter writer = new FileWriter("outdated_drivers.csv");
            CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader("Nome do driver", "Versão atual", "Última versão disponível"))) {
            for (Driver driver : outdatedDrivers) {
                printer.printRecord(driver.getName(), driver.getCurrentVersion(), driver.getLatestVersion());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
