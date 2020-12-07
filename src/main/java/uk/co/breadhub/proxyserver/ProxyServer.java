package uk.co.breadhub.proxyserver;

import uk.co.breadhub.proxyserver.Utils.YamlUtils;
import uk.co.breadhub.proxyserver.Utils.configuration.file.FileConfiguration;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

@SuppressWarnings("ALL")
public class ProxyServer {

    public static FileConfiguration fileConfiguration;
    public static File configFile;
    public static Path currentRelativePath = Paths.get("");
    public static boolean active = true;

    public static HashMap<String, Thread> runnables = new HashMap<>();

    public static void main(String[] args) {
        try {
            System.out.println("Does " + new File(currentRelativePath.toAbsolutePath() + File.separator + "config.yml").toString() + " Exist?");
            if (!new File(currentRelativePath.toAbsolutePath() + File.separator + "config.yml").exists()) {
                configFile = YamlUtils.createFile("config.yml");
                fileConfiguration = YamlUtils.createYamlFile(configFile);
                YamlUtils.setupDefaultConfig();
            }
            else {
                configFile = YamlUtils.createFile("config.yml");
                fileConfiguration = YamlUtils.createYamlFile(configFile);
            }

            for (String key : getConfig().getConfigurationSection("Servers").getKeys(false)) {
                String hostname = getConfig().getString("Servers." + key + ".hostname");
                int localPort = getConfig().getInt("Servers." + key + ".localPort");
                int remotePort = getConfig().getInt("Servers." + key + ".remotePort");
                System.out.println("Starting proxy for Server [" + key + "] On "
                        + hostname
                        + ":"
                        + remotePort
                        + " on port "
                        + localPort
                );
                runnables.put(key, new Thread(() -> {
                    try {
                        runServer(key, hostname, remotePort, localPort);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }));
            }
            while (active){
                for (Thread r: runnables.values()) {
                    r.start();
                }
            }
        } catch (Exception e) {
        }
    }

    public static void runServer(String thread, String host, int remoteport, int localport) throws IOException {
        ServerSocket ss = new ServerSocket(localport);
        final byte[] request = new byte[1024];
        byte[] reply = new byte[4096];

        while (true) {
            Socket client = null, server = null;
            try {
                client = ss.accept();
                final InputStream from_client = client.getInputStream();
                final OutputStream to_client = client.getOutputStream();

                try { server = new Socket(host, remoteport); } catch (IOException e) {
                    PrintWriter out = new PrintWriter(new OutputStreamWriter(to_client));
                    out.println("Proxy server cannot connect to " + host + ":" + remoteport + ":\n" + e);
                    out.flush();
                    client.close();
                    continue;
                }

                // Get server streams.
                final InputStream from_server = server.getInputStream();
                final OutputStream to_server = server.getOutputStream();

                System.out.println("[Proxy] -> [" + thread + "] -> Client ["  + client.getInetAddress().toString().replace("/","") + ":" + client.getPort() + "] -> Requesting -> [" + server.getInetAddress() + "]");

                Thread t = new Thread(() -> {
                    int bytes_read;
                    try {
                        while ((bytes_read = from_client.read(request)) != -1) {
                            to_server.write(request, 0, bytes_read);
                            to_server.flush();
                        }
                    } catch (IOException e) {}

                    try {to_server.close();} catch (IOException e) {}
                });

                t.start();

                int bytes_read;
                try {
                    while ((bytes_read = from_server.read(reply)) != -1) {
                        to_client.write(reply, 0, bytes_read);
                        to_client.flush();
                    }
                } catch (IOException e) {}

                to_client.close();
            } catch (IOException e) { System.err.println(e); } finally {
                try {
                    if (server != null) { server.close(); }
                    if (client != null) { client.close(); }
                } catch (IOException e) {}
            }
        }
    }

    public static FileConfiguration getConfig() {
        return fileConfiguration;
    }

}
