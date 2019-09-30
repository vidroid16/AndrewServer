import andr.DataBaseWorks.DBController;
import andr.DataBaseWorks.JDBCConnector;
import andr.Passport;
import andr.Photo;
import andr.Udp.User;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.ArrayList;

public class Test {
    public static void main(String[] args) throws SQLException{
        System.out.println(8);


        JDBCConnector connector = new JDBCConnector();
        DBController controller = new DBController(connector);
        System.out.println(controller.generateUID());
    }
}
