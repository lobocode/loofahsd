import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinNT;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

public class CheckHardware {

    public void checkHardware() {
        // Verifica se o usuário tem permissão de administrador
        if (!isAdmin()) {
            System.out.println("Este programa precisa de permissão de administrador para ser executado.");
            return;
        }

        // Caminho do arquivo YAML de saída..preciso definir isso melhor
        String outputPath = "C:\\xpto.yaml";

        // Obtém uma lista de todos os dispositivos instalados no sistema
        WinNT.HANDLE hDevInfo = SetupApi.INSTANCE.SetupDiGetClassDevs(null, null, null, SetupApi.DIGCF_ALLCLASSES | SetupApi.DIGCF_PRESENT);

        // Armazena as informações dos dispositivos em uma lista
        List<Dispositivo> dispositivos = new ArrayList<>();
        for (int i = 0;; i++) {
            SetupApi.SP_DEVINFO_DATA deviceInfoData = new SetupApi.SP_DEVINFO_DATA();
            deviceInfoData.cbSize = deviceInfoData.size();

            if (!SetupApi.INSTANCE.SetupDiEnumDeviceInfo(hDevInfo, i, deviceInfoData)) {
                break;
            }

            Dispositivo dispositivo = new Dispositivo();
            dispositivo.nome = SetupApi.INSTANCE.SetupDiGetDeviceRegistryProperty(hDevInfo, deviceInfoData, SetupApi.SPDRP_DEVICEDESC, null);
            dispositivo.fabricante = SetupApi.INSTANCE.SetupDiGetDeviceRegistryProperty(hDevInfo, deviceInfoData, SetupApi.SPDRP_MFG, null);
            dispositivo.identificador = SetupApi.INSTANCE.SetupDiGetDeviceInstanceId(hDevInfo, deviceInfoData);

            dispositivos.add(dispositivo);
        }

        // Gera o arquivo YAML
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(outputPath)) {
            writer.write(yaml.dump(dispositivos));
        } catch (IOException e) {
            System.out.println("Erro ao escrever o arquivo YAML: " + outputPath);
            e.printStackTrace();
        }
    }

    private boolean isAdmin() {
    try {
        return (System.getProperty("os.name").toLowerCase().contains("windows")) ?
        (new File("C:\\Windows\\Temp")).canWrite() : true;
    } catch (Exception e) {
        return false;
    }
}

