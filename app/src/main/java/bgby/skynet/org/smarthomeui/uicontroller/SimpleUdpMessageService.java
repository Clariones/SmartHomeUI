package bgby.skynet.org.smarthomeui.uicontroller;

import org.skynet.bgby.listeningserver.DirectBroadcastMessageService;
import org.skynet.bgby.protocol.UdpData;
import org.skynet.bgby.protocol.UdpMessage;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;

/**
 * Created by Clariones on 7/7/2016.
 */
public class SimpleUdpMessageService extends DirectBroadcastMessageService {
    public void sendMessage(UdpMessage message, SocketAddress address){
        UdpData data = this.getCodec().code(message);
        data.setSocketAddress(address);
        this.sendUdpMessage(data);
    }
    @Override
    protected void createUdpSocket() throws IOException {
        listeningSocket = new DatagramSocket();
        sendingSocket = listeningSocket;
    }
}
