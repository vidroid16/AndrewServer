package andr.DataBaseWorks;

import java.sql.*;

public class JDBCConnector extends DBConfigs {

    private final String DB_URL = "jdbc:postgresql://"+dbHost+":"+dbPort+"/" + dbName;
    private final String USER = dbUser;
    private final String PASS = dbPassword;

    private static final FileLogger logger = new FileLogger();

    private Connection connection;

    public JDBCConnector() {
        LoadingPrinter loadingPrinter = new LoadingPrinter();
        boolean firstCreation = true;
        while(true) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                loadingPrinter.stop();
                System.out.println("\nСоединение с БД установлено!");
                break;
            } catch (ClassNotFoundException e) {
                System.err.println("Ошибка JDBC драйвер не найден!");
                System.exit(1);
            } catch (SQLException e) {
                if (e.getSQLState().equals("08001")) {
                    if(firstCreation) {
                        System.out.println("Ошибка: невозможно подключиться к серверу БД, так как сервер не доступен!");
                        System.out.println("Попытка подключения:");
                        new Thread(loadingPrinter::printLoadingLine).start();
                        firstCreation = false;
                    }
                }
            }
        }
        
	//TODO: Строки с запросами sql на создание необходимых таблиц а затем выполнение этих запросов с помощью execSQLUpdate(...);


        String passports_table = String.format("CREATE TABLE IF NOT EXISTS %s(\n" +
                "                         id INT,\n" +
                "                         %s VARCHAR,\n" +
                "                         %s VARCHAR,\n" +
                "                         %s VARCHAR,\n" +
                "                         %s VARCHAR,\n" +
                "                         %s VARCHAR,\n" +
                "                         PRIMARY KEY(id)\n" +
                ");", DBConst.PASSPORT_TABLE, DBConst.NAME_PASS, DBConst.SERNAME_PASS, DBConst.PATR_PASS, DBConst.TIME_PASS, DBConst.USER_PASS);

        String photos_table = String.format("CREATE TABLE IF NOT EXISTS %s(\n" +
                "                         id INT,\n" +
                "                         %s BOOLEAN,\n" +
                "                         %s INT,\n" +
                "                         %s INT,\n" +
                "                         %s VARCHAR,\n" +
                "                         PRIMARY KEY(id)\n" +
                ");", DBConst.PHOTO_TABLE, DBConst.IS_COLORED_PHOTO, DBConst.WIDTH_PHOTO, DBConst.HEIGHT_PHOTO, DBConst.LINK_PHOTO);

        String birth_table = String.format("CREATE TABLE IF NOT EXISTS %s(\n" +
                "                         id INT,\n" +
                "                         %s INT,\n" +
                "                         %s INT,\n" +
                "                         %s VARCHAR,\n" +
                "                         PRIMARY KEY(id)\n" +
                ");", DBConst.BIRTHDATE_TABLE, DBConst.DAY_BIRTH, DBConst.YEAR_BIRTH, DBConst.MON_BIRTH);


        execSQLUpdate(passports_table);
        execSQLUpdate(photos_table);
        execSQLUpdate(birth_table);
	/*
		Например:
		String table = "CREATE TABLE IF NOT EXISTS table(id INT, name VARCHAR, PRIMARY KEY(id));";
		execSQLUpdate(table);
	*/
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * Executing a query and returning resultSet. After calling this method and processing resultSet, you must call PreparedStatement.close() method.
     * @param query SQL query
     * @return Pair of PreparedStatement and ResultSet
     */
    public Pair<PreparedStatement, ResultSet> execSQLQuery(String query){
            try {
                PreparedStatement statement = connection.
                        prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();
                return new Pair<>(statement, resultSet);
            } catch (SQLException e) {
                if (e.getSQLState().equals("08001") || e.getSQLState().equals("08006")) {
                    resetConnection();
                    return execSQLQuery(query);
                }
                else {
                    logger.log(e.getMessage() + "\nSqlState = " + e.getSQLState());
                    return null;
                }
            }
    }

    private boolean resetConnection(){
        LoadingPrinter loadingPrinter = new LoadingPrinter();
        boolean firstCreation = true;
        while(true) {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(DB_URL, USER, PASS);
                loadingPrinter.stop();
                System.out.println("\nСоединение с БД установлено!");
                return true;
            } catch (ClassNotFoundException e) {
                System.err.println("Ошибка JDBC драйвер не найден!");
                System.exit(1);
            } catch (SQLException e) {
                if (e.getSQLState().equals("08001")) {
                    if(firstCreation) {
                        new Thread(loadingPrinter::printLoadingLine).start();
                        firstCreation = false;
                    }
                }
            }
        }
    }

    public boolean execSQLUpdate(String query){
        try (PreparedStatement statement = connection.
                prepareStatement(query)) {
            statement.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getSQLState().equals("08001") || e.getSQLState().equals("08006")) {
                resetConnection();
                return execSQLUpdate(query);
            }
            else {
                logger.log(e.getMessage() + "\nSqlState = " + e.getSQLState());
                return false;
            }
        }
    }
}

