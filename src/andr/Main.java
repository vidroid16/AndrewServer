package andr;

import andr.DataBaseWorks.DBController;
import andr.DataBaseWorks.JDBCConnector;
import andr.DataBaseWorks.MailSender;
import andr.DataBaseWorks.MailService;
import andr.Udp.Client;
import andr.Udp.MyPackage;
import andr.Udp.User;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {
    public static Hashtable<Passport, Integer> hashtable = new Hashtable<>();
    public static ByteBuffer buffer = ByteBuffer.allocate(500000);
    public static ArrayList<Integer> userIds = new ArrayList<>();
    public static DBController db = new DBController(new JDBCConnector());

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
            System.out.println("Enter port (port was not entered)");
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
            //System.out.println(recievedPackage.getUserId());
            //System.out.println(recievedPackage.getId());
            if(recievedPackage.getId() == 200 && recievedPackage.getUserId() == 0) {
                String data = new String(recievedPackage.getData());
                String [] data2 = data.split(" ");
                String login = data2[0];
                String password = data2[1];
                User user = new User(login, password);
                System.out.println(db.isUserExists(user));
                if(db.isUserExists(user)){
                    setUserId(recievedPackage, user);
                    //todo
                    System.out.println("Command 100");
                    recievedPackage.setData("Welcome back".concat(user.getLogin()).getBytes());
                    client.setMyPackage(recievedPackage);
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                }else{
                    client.setMyPackage(new MyPackage(-10, 0,"Invalid combination of nickname and password".getBytes()));
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                }
                return;
            }
            if(recievedPackage.getId() == 201 && recievedPackage.getUserId() == 0) {
                String data = new String(recievedPackage.getData());
                String [] data2 = data.split(" ");
                String mail = data2[0];
                String login = data2[1];
                String password = data2[2];
                User user = new User(login, password, mail);
                if(db.isUserExists(user)){
                    client.setMyPackage(new MyPackage(-11, 0,"Such nickname already exists".getBytes()));
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                }else{
                    setUserId(recievedPackage, user);
                    db.addUser(user, Main.db.generateUID() + 1);
                    new Thread(()->{
                        MailSender mailSender = new MailSender(MailService.GMAIL, "shalyap3211@gmail.com", "Qwerty228");
                        mailSender.send("7Lab PROGA parol", user.getPassword(), user.getEmail());
                    }).start();
                    recievedPackage.setData("Welcome ".concat(user.getLogin()).getBytes());
                    client.setMyPackage(recievedPackage);
                    selectionKey.interestOps(SelectionKey.OP_WRITE);
                }
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int strHashCode(String str){
        int result = 13;
        int prime = 26;
        for (int i = 0; i < str.length(); i++) {
            result = result * prime + (int)str.charAt(i) * (int)Math.pow(prime, i + 1);
        }
        return result;
    }


    public static void setUserId(MyPackage myPackage, User user){
            if (Main.db.isUserExists(user)){
                myPackage.setUserId(Main.db.getUIDByNick(user.getLogin()));
            }else {
                myPackage.setUserId(Main.db.generateUID() + 1);
            }
    }
}


