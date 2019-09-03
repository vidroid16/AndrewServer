package andr;

import andr.Udp.Client;
import andr.Udp.MyPackage;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    public static Hashtable<Passport, Integer> hashtable = new Hashtable<>();
    public static ByteBuffer buffer = ByteBuffer.allocate(500000);
    public static ArrayList<Integer> userIds = new ArrayList<>();
    public static void main(String[] args) {
        try {
            boolean isSaved = Files.exists(Paths.get("save.xml"));
            boolean notEmpty = false;
            if(isSaved){
                notEmpty = !Files.readAllLines(Paths.get("save.xml")).isEmpty();
            }
        if(isSaved && notEmpty){
            FileController fileControl = new FileController("save.xml");
            hashtable = fileControl.readCollection(1);
            Files.delete(Paths.get("save.xml"));
        }

        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            try {
                File saveFile = new File("save.xml");
                saveFile.createNewFile();
                FileController fc = new FileController("save.xml");
                fc.writeCollection(hashtable, 6);
            }catch (IOException e){
                e.printStackTrace();
            }
        }));
        Scanner scanner = new Scanner(System.in);
        FileController controller = new FileController("file.xml");
        Command cmd = new Command();
        if(args.length == 0){
            System.out.println("Введите порт (порт не был введен)");
            System.exit(228);
        }

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
                        System.out.println("read " + key.toString());
                        TimeUnit.SECONDS.sleep(1);
                        hadleRead(key);//todo
                    }
                    if(key.isWritable()){
                        System.out.println("write "+ key.toString());
                        TimeUnit.SECONDS.sleep(1);
                        handleWrite(key);//todo
                        key.interestOps(SelectionKey.OP_READ);
                    }
                    iterator.remove();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static void handleWrite(SelectionKey selectionKey) {
        try {
            DatagramChannel channel = DatagramChannel.open();
            Client client = (Client)selectionKey.attachment();
            SocketAddress target = client.getSocketAddress();
            ByteBuffer bf = ByteBuffer.wrap(client.getMyPackage().getSerialized());
            //System.out.println(target.toString());
            //System.out.println(bf.toString());

            channel.send(bf, target);


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
    public static void hadleRead(SelectionKey selectionKey){
        DatagramChannel channel = (DatagramChannel)selectionKey.channel();
        Client client = new Client();
        selectionKey.attach(client);
        try {
            SocketAddress clientSocketAdress = channel.receive(buffer);
            client.setSocketAddress(clientSocketAdress);
            MyPackage recievedPackage = new MyPackage(buffer.array());
            buffer.clear();

            if(recievedPackage.getId() == 100 && recievedPackage.getUserId() == 0) {
                setUserId(recievedPackage);

                System.out.println("Command 100");
                client.setMyPackage(recievedPackage);
                selectionKey.interestOps(SelectionKey.OP_WRITE);

                return;
            }

            Command command = new Command();
            if(recievedPackage.getId() == 10){
                System.out.println("Command 10");
                MyPackage myPackage = command.load(recievedPackage.getUserId(), recievedPackage.getData());
                client.setMyPackage(myPackage);//todo
            }else {
                System.out.println("Command Nothing");
                MyPackage myPackage = command.parceCommand(recievedPackage.getUserId(), new String(recievedPackage.getData(), Charset.defaultCharset()));
                client.setMyPackage(myPackage);//todo
            }
            selectionKey.interestOps(SelectionKey.OP_WRITE);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void setUserId(MyPackage myPackage){
        myPackage.setUserId(userIds.size() + 1);
    }
}


