import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinNT;

public class DriverBackup {

    public voic driverBackup() {

        // Caminho da pasta de backup
        String backupPath = "C:\\Drivers Backup\\";
        
        // Cria a pasta de backup se ela não existir
        File backupFolder = new File(backupPath);
        if (!backupFolder.exists()) {
            backupFolder.mkdir();
        }

        // Obtém uma lista de todos os dispositivos instalados no sistema
        WinNT.HANDLE hDevInfo = SetupApi.INSTANCE.SetupDiGetClassDevs(null, null, null, SetupApi.DIGCF_ALLCLASSES | SetupApi.DIGCF_PRESENT);

        // Percorre a lista de dispositivos para fazer backup de seus drivers
        for (int i = 0;; i++) {
            SetupApi.SP_DEVINFO_DATA deviceInfoData = new SetupApi.SP_DEVINFO_DATA();
            deviceInfoData.cbSize = deviceInfoData.size();

            if (!SetupApi.INSTANCE.SetupDiEnumDeviceInfo(hDevInfo, i, deviceInfoData)) {
                break;
            }

            // Obtém o caminho para o arquivo INF do driver
            String driverPath = SetupApi.INSTANCE.SetupDiGetDriverInfoPath(hDevInfo, deviceInfoData);
            if (driverPath == null) {
                continue;
            }

            // Faz o backup do arquivo INF para a pasta de backup
            Path source = Paths.get(driverPath);
            Path destination = Paths.get(backupPath + source.getFileName());
            try {
                Files.copy(source, destination);
            } catch (IOException e) {
                System.out.println("Erro ao fazer backup do driver: " + source.getFileName());
                e.printStackTrace();
            }
        }
    }
}
