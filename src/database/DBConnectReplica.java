package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnectReplica {

	    private static DBConnectReplica instance = null;

	    private String driverName;
	    private String url;
	    private String database;
	    private String param;
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
	    private DBConnectReplica() {
	        driverName = "com.mysql.jdbc.Driver";
	        url = "jdbc:mysql://localhost:3306/";
	        param = "?useSSL=false";
	        database = "europe_america";
	        username = "root";
	        password = "Phoenyx_447";//tempo
	    }
	     
	    /**
	     * returns an instance of the Database Connection
	     *
	     * @return instance of the Database Connection
	     */
	    public static DBConnectReplica getInstance() {
	        if (instance == null) {
	            instance = new DBConnectReplica();
	        }

	        return instance;
	    }
	    
	    /**
	     * returns a connection to database
	     *
	     * @return connection to database
	     */
	    public static Connection getReplicaConnection() {
	        if (instance == null) {
	            instance = new DBConnectReplica();
	        }

	        try {
	            return DriverManager.getConnection(instance.getUrl()
	                    + instance.getReplicaDatabase() + instance.getParam(),
	                    instance.getUsername(),
	                    instance.getPassword());
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
	    public String getReplicaDatabase() {
	        return database;
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

