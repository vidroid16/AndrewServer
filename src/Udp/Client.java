package Udp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.sql.SQLOutput;

public class Client {
    private SocketAddress socketAddress;
    private MyPackage myPackage;

    public MyPackage getMyPackage() {
        return myPackage;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public void setMyPackage(MyPackage myPackage) {
        this.myPackage = myPackage;
    }

    public void setSocketAddress(SocketAddress socketAddress) {
        this.socketAddress = socketAddress;
    }
}
