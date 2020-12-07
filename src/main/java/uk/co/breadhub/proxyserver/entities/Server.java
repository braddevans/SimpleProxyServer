package uk.co.breadhub.proxyserver.entities;

public class Server {

    private String hostname;
    private int localPort;
    private int remotePort;

    public Server(String host, int localPort, int remotePort){
        this.hostname = host;
        this.localPort = localPort;
        this.remotePort = remotePort;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public int getLocalPort() {
        return localPort;
    }

    protected void setLocalPort(int localPort) {
        this.localPort = localPort;
    }

    public int getRemotePort() {
        return remotePort;
    }

    public void setRemotePort(int remotePort) {
        this.remotePort = remotePort;
    }

    @Override
    public String toString() {
        return "Server{" +
                "hostname='" + hostname + '\'' +
                ", localPort=" + localPort +
                ", remotePort=" + remotePort +
                '}';
    }
}
