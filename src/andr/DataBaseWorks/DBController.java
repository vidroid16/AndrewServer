package andr.DataBaseWorks;

import andr.DataBaseWorks.JDBCConnector;
import andr.Passport;
import andr.Udp.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class DBController {

    private JDBCConnector connector;

    public DBController(JDBCConnector connector){
        this.connector = connector;
    }

    public void addPassToDB(Passport passport){
        passport.getInsertSqlQueries().forEach(p -> connector.execSQLUpdate(p.replace("DEFAULT", String.valueOf(passport.getID()))));
    }
    public void addPassToDB(Passport passport, User user){
        passport.getInsertSqlQueries().forEach(p -> connector.execSQLUpdate(
                p.replace("DEFAULT", String.valueOf(passport.getID() * 10 + user.hashCode() * 100)).
                        replace("USER", user.getLogin())
        ));
    }



    public void removePassFromDB(Passport passport){
        passport.getDelSqlQueries().forEach(p -> connector.execSQLUpdate(p.replace("DEFAULT", String.valueOf(passport.getID()))));
    }
    public void removePassFromDB(Passport passport, User user){
        passport.getInsertSqlQueries().forEach(p -> connector.execSQLUpdate(
                p.replace("DEFAULT", String.valueOf(passport.getID() * 10 + user.hashCode() * 100)).
                        replace("USER", user.getLogin())
        ));
    }

    public ArrayList<Pair<String,Passport>> getPassportFromDB() throws SQLException {
        Pair<PreparedStatement, ResultSet> pair = connector.execSQLQuery("SELECT * FROM passports;");
        ResultSet set = pair.getValue();
        //Получили id-шники костюмов
        ArrayList<Pair<Integer,String>> costumeKeys = new ArrayList<>();
        while(set.next()){
            costumeKeys.add(new Pair<>(set.getInt("id"), set.getString(DBConst.USER_PASS)));
        }
        pair.getKey().close();
        for(Pair<Integer,String> keyPair : costumeKeys){
            connector.execSQLQuery("SELECT * FROM ");
        }

    }

}
