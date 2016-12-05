package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnect {
    private static DBConnect instance = null;
    private static DBConnect instancerep = null;

    private String driverName;
    private String url;
    private String database;
    private String param;
    private String databasereplica;
    private String username;
    private String password;

    /**
     * constructor for connection
     *
     * @param dN driver name
     * @param url URL
     * @param db database to access
     * @param un username
     * @param pw password
     */
    private DBConnect() {
        driverName = "com.mysql.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/";
        databasereplica = "europe_america";
        param = "?useSSL=false";
        database = "wdi";
        username = "root";
        password = "root";//tempo
    }
     
    /**
     * returns an instance of the Database Connection
     *
     * @return instance of the Database Connection
     */
    public static DBConnect getInstance() {
        if (instance == null) {
            instance = new DBConnect();
        }

        return instance;
    }
    
   /**
    * returns an instance of the Database Connection
    *
    * @return instance of the Database Connection
    */
   public static DBConnect getInstanceReplica() {
       if (instancerep == null) {
           instancerep = new DBConnect();
       }

       return instancerep;
   }
    /**
     * returns a connection to database
     *
     * @return connection to database
     */
    public static Connection getConnection() {
        if (instance == null) {
            instance = new DBConnect();
        }

        try {
            return DriverManager.getConnection(instance.getUrl()
                    + instance.getDatabase() + instance.getParam(),
                    instance.getUsername(),
                    instance.getPassword());
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }

    /**
     * returns a connection to database replica
     *
     * @return connection to database replica
     */
    public static Connection getConnectionReplica() {
        if (instancerep == null) {
            instancerep = new DBConnect();
        }

        try {
            return DriverManager.getConnection(instancerep.getUrl()
                    + instancerep.getDatabaseReplica(),
                    instancerep.getUsername(),
                    instancerep.getPassword());
        } catch (SQLException se) {
            se.printStackTrace();
        }

        return null;
    }
    /**
     * returns database URL
     *
     * @return database URL
     */
    public String getUrl() {
        return url;
    }

    /**
     * returns database name
     *
     * @return database name
     */
    public String getDatabase() {
        return database;
    }

    /**
     * returns database name
     *
     * @return database name
     */
    public String getDatabaseReplica() {
        return databasereplica;
    }
    /**
     * returns username
     *
     * @return username
     */
    public String getUsername() {
        return username;
    }

    private String getPassword() {
        return password;
    }

    private String getParam() {
    	return param;
    }
    
    /**
     * returns whether password is correct or not
     *
     * @param password password to checkPassword
     * @return whether password is correct or not
     */
    public boolean isCorrectPassword(String password) {
        return password.equals(this.password);
    }
}
