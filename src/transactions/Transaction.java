package transactions;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import com.sun.rowset.CachedRowSetImpl;

public interface Transaction {
	
	public static int ISO_READ_UNCOMMITTED = 1;
	public static int ISO_READ_COMMITTED = 2;
	public static int ISO_REPEATABLE_READ = 3;
	public static int ISO_SERIALIZABLE = 4;
	
	public static String ISO_READ_UNCOMMITTED_LBL	="READ UNCOMMITTED";
	public static String ISO_READ_COMMITTED_LBL	    ="READ COMMITTED";
	public static String ISO_REPEATABLE_READ_LBL	="REPEATABLE READ";
	public static String ISO_SERIALIZABLE_LBL		="SERIALIZABLE";
	
	
	public void setIsolationLevel(int iso_level);
	
	public int getIsolationLevel();
	
	public void beginTransaction() throws SQLException;
	
	public CachedRowSetImpl getResultSet();
	
	public boolean isDonePopulating();
	
	public String getScope();
	
	public String getQuery() ;
	
	public void start();
	
	public void rollback();
	
	public void commit();
	
	public void setName(String name);
	
	public String getName();
	
	public void setConnection();
	public void setConnectionReplica();
	
}
