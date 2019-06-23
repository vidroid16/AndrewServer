package Udp;

import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.util.Arrays;

public class Server {
    public void on() {
        try {
            Selector selector = Selector.open();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
