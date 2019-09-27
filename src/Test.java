import andr.DataBaseWorks.DBController;
import andr.DataBaseWorks.JDBCConnector;
import andr.Passport;
import andr.Photo;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

public class Test {
    public static void main(String[] args) throws UnknownHostException {
//        JDBCConnector connector = new JDBCConnector();
//        DBController controller = new DBController(connector);
//
//        Passport passport = new Passport("Val", "Bond", "Eugine", new Photo(true, 400, 300, "https://sun9-67.userapi.com/c857616/v857616567/7565d/9eqw7LHiJ2o.jpg"));
//        passport.setID(286);
//        controller.removePassFromDB(passport);

        System.out.println(InetAddress.getLocalHost().toString());
    }
}
