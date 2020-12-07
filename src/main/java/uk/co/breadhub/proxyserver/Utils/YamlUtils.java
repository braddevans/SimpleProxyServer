package uk.co.breadhub.proxyserver.Utils;

import uk.co.breadhub.proxyserver.ProxyServer;
import uk.co.breadhub.proxyserver.Utils.configuration.Configuration;
import uk.co.breadhub.proxyserver.Utils.configuration.ConfigurationSection;
import uk.co.breadhub.proxyserver.Utils.configuration.file.FileConfiguration;
import uk.co.breadhub.proxyserver.Utils.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlUtils {
    public static FileConfiguration createYamlFile(File f) {
        return YamlConfiguration.loadConfiguration(f);
    }

    public static void saveYamlFile(FileConfiguration c, File f) {
        try {
            c.save(f);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static File createFile(String filename) {
        File c = new File(ProxyServer.currentRelativePath.toFile().getAbsolutePath());
        c.mkdir();
        File f = new File(ProxyServer.currentRelativePath.toFile().getAbsolutePath() + File.separator + filename);

        if (!f.exists()) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return f;
    }

    public static void setupDefaultConfig() {
        try {
            FileConfiguration config = new YamlConfiguration();
            config.set("Servers.test.hostname", "pornhub.local");
            config.set("Servers.test.localPort", 25565);
            config.set("Servers.test.remotePort", 25565);

            saveYamlFile(config, ProxyServer.configFile);
        } catch (Exception e) {}
    }
}
