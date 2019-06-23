import Udp.Client;
import Udp.MyPackage;
import Udp.Server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static Hashtable<Passport, Integer> hashtable = new Hashtable<>();
    public static ByteBuffer buffer = ByteBuffer.allocate((Integer.MAX_VALUE/2)-1);
    public static ArrayList<Integer> userIds = new ArrayList<>();
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        FileController controller = new FileController("file.xml");
        Command cmd = new Command();
        if(args.length == 0){
            System.out.println("Введите порт (порт не был введен)");
            System.exit(228);
        }

        try {
            int port  = Integer.parseInt(args[0]);
            Selector selector = Selector.open();
            DatagramChannel datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            datagramChannel.socket().bind(new InetSocketAddress(port));
            datagramChannel.register(selector, SelectionKey.OP_READ);

            while(true){
                if(selector.select(3000) == 0){
                    System.out.println(".");
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    if(key.isReadable()){
                        hadleRead(key);//todo
                    }
                    if(key.isWritable()){
                        handleWrite();//todo
                    }
                    iterator.remove();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static class ProgramPauser{
        public static void pause(String message){
            try {
                TimeUnit.MILLISECONDS.sleep(300);
                System.out.println(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        public static void pauseErr(String message){
            try {
                TimeUnit.MILLISECONDS.sleep(500);
                System.err.println(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void hadleRead(SelectionKey selectionKey){
        DatagramChannel channel = (DatagramChannel)selectionKey.channel();
        Client client = new Client();
        selectionKey.attach(client);
        try {
            SocketAddress clientSocketAdress = channel.receive(buffer);
            client.setSocketAddress(clientSocketAdress);
            MyPackage recievedPackage = new MyPackage(buffer.array());
            buffer.clear();
            setUserId(recievedPackage);
            Command command = new Command();
            MyPackage myPackage = command.parceCommand(recievedPackage.getUserId(), new String(recievedPackage.getData(), Charset.defaultCharset()));
            client.setMyPackage(recievedPackage);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setUserId(MyPackage myPackage){
        myPackage.setUserId(userIds.size() + 1);
    }
}


