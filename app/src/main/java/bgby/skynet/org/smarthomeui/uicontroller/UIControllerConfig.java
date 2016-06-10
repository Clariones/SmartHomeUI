package bgby.skynet.org.smarthomeui.uicontroller;

import java.net.InetAddress;

public class UIControllerConfig {
    protected InetAddress driverProxyAddress;
    protected int driverProxyPort = -1;
    protected InetAddress multicastAddress;
    protected int multicastPort = -1;
    protected String controllerID;

    public String getControllerID() {
        return controllerID;
    }

    public void setControllerID(String controllerID) {
        this.controllerID = controllerID;
    }

    public InetAddress getDriverProxyAddress() {
        return driverProxyAddress;
    }

    public void setDriverProxyAddress(InetAddress driverProxyAddress) {
        this.driverProxyAddress = driverProxyAddress;
    }

    public int getDriverProxyPort() {
        return driverProxyPort;
    }

    public void setDriverProxyPort(int driverProxyPort) {
        this.driverProxyPort = driverProxyPort;
    }

    public InetAddress getMulticastAddress() {
        return multicastAddress;
    }

    public void setMulticastAddress(InetAddress multicastAddress) {
        this.multicastAddress = multicastAddress;
    }

    public int getMulticastPort() {
        return multicastPort;
    }

    public void setMulticastPort(int multicastPort) {
        this.multicastPort = multicastPort;
    }

}
