package andr.DataBaseWorks;

import andr.DataBaseWorks.JDBCConnector;
import andr.Passport;
import andr.Photo;
import andr.Udp.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
        //Получили id-шники паспортов
        ArrayList<Pair<Integer,String>> passportKeys = new ArrayList<>();
        while(set.next()){
            passportKeys.add(new Pair<>(set.getInt("id"), set.getString(DBConst.USER_PASS)));
        }
        pair.getKey().close();
        for(Pair<Integer,String> keyPair : passportKeys){
            connector.execSQLQuery("SELECT * FROM ");
        }
    //TODO: ДОПИСАТЬ
        return null;
    }
    //TODO: все что говорил
    public ArrayList<Passport> getUserPassportsFromDB(String userLogin){
        try {
            Pair<PreparedStatement, ResultSet> pairPassports =
                    connector.execSQLQuery("SELECT * FROM passports WHERE user_login = 'USER';".replace("USER", userLogin));
            ResultSet rsPassports = pairPassports.getValue();
            ArrayList<Passport> userPassports = new ArrayList<>();

            while (rsPassports.next()) {
                int id = rsPassports.getInt("id");
                String name = rsPassports.getString("name");
                String sername = rsPassports.getString("sername");
                String patronymic = rsPassports.getString("patronymic");
                Photo photo = null;

                Pair<PreparedStatement, ResultSet> pairPhotos =
                        connector.execSQLQuery("SELECT * FROM photos WHERE id = UID;".replace("UID", String.valueOf(id)));
                ResultSet rsPhotos = pairPhotos.getValue();
                while (rsPhotos.next()) {
                    boolean isColored = rsPhotos.getBoolean("iscolored");
                    int width = rsPhotos.getInt("width");
                    int height = rsPhotos.getInt("height");
                    String link = rsPhotos.getString("link");
                    photo = new Photo(isColored, height, width, link);
                }
                Passport p = new Passport(name, sername, patronymic, photo);
                userPassports.add(p);
            }
            return userPassports;
        }catch (SQLException e){
            return null;
        }
    }

    public void uploadUserPassportsToDB(User user, ArrayList<Passport> passports) throws SQLException{
        String nick = user.getLogin();
        Pair<PreparedStatement, ResultSet> pairPassports =
                connector.execSQLQuery("SElECT *  FROM passports WHERE user_login = 'USER';".replace("USER", nick));
        ResultSet rsPassport = pairPassports.getValue();
        ArrayList<Integer> ids = new ArrayList<>();
        while (rsPassport.next()){
            ids.add(rsPassport.getInt("id"));
        }

        for (int i : ids) {
            connector.execSQLQuery("DELETE FROM photos WHERE id = 'USER';".replace("USER", String.valueOf(i)));
            connector.execSQLQuery("DELETE FROM bdates WHERE id = 'USER';".replace("USER", String.valueOf(i)));
        }
        connector.execSQLQuery("DELETE FROM passports WHERE user_login = 'USER';".replace("USER", nick));

        for (Passport p : passports) {
            addPassToDB(p, user);
        }
        //TODO проверить на корректность
    }

    public void addUser(User user){
        connector.execSQLUpdate(String.format("INSERT INTO %s VALUES ('%s', '%s', '%s');",
                DBConst.USER_TABLE, user.getLogin(), user.getEncodedPassword(), user.getEmail()));
    }
    public void addUser(User user, int user_id){
        connector.execSQLUpdate(String.format("INSERT INTO %s VALUES (%s, '%s', '%s', '%s');",
                DBConst.USER_TABLE, user_id, user.getLogin(), user.getEncodedPassword(), user.getEmail()));
    }

    public boolean isUserExists(User user){
        try {
            String nick = user.getLogin();
            Pair<PreparedStatement, ResultSet> pairPassports =
                    connector.execSQLQuery("SElECT * FROM users WHERE nick = 'USER';".replace("USER", nick));
            if (pairPassports.getValue().next()) return true;
            else return false;
        }catch (Exception e){
            return false;
        }
        //TODO проверить на корректность
    }
    public String getNickByID(int user_id){
        try {
            Pair<PreparedStatement, ResultSet> pairPassports =
                    connector.execSQLQuery("SElECT * FROM users WHERE id = SS;".replace("SS", String.valueOf(user_id)));
            pairPassports.getValue().next();
            return pairPassports.getValue().getString("nick");
        }catch (SQLException e){
            e.printStackTrace();
            return "null";
        }
    }
    public User getUserByNick(String nick){
        try {
            Pair<PreparedStatement, ResultSet> pairPassports =
                    connector.execSQLQuery("SElECT * FROM users WHERE nick = 'SS';".replace("SS",nick));
            pairPassports.getValue().next();

            String getNick = pairPassports.getValue().getString("nick");
            String pass = pairPassports.getValue().getString("password");
            String mail = pairPassports.getValue().getString("email");

            return new User(getNick,pass,mail);
        }catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }
    public int getUIDByNick(String nick){
        try {
            Pair<PreparedStatement, ResultSet> pairPassports =
                    connector.execSQLQuery("SElECT * FROM users WHERE nick = 'SS';".replace("SS", nick));
            pairPassports.getValue().next();
            return pairPassports.getValue().getInt("id");
        }catch (SQLException e){
            e.printStackTrace();
            return 0;
        }
    }
    public int generateUID(){
        try {
            Statement ps = connector.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = ps.executeQuery("SElECT * FROM users");
            rs.last();
            return rs.getRow();
        }catch(SQLException e){
            e.printStackTrace();
            return 228;
        }
    }
}
