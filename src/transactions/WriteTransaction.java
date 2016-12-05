package transactions;
import java.io.Serializable;
import com.sun.rowset.CachedRowSetImpl;

import database.DBConnect;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class WriteTransaction implements Transaction, Runnable, Serializable{
	String scope;
	String query;
	Connection con;
	Statement stmt;
	CachedRowSetImpl cs;
	boolean toCommit;
	boolean isDonePopulating;
	int iso_level;
	String name;
	
	public WriteTransaction(String query, String scope, boolean toCommit, int iso_level){
		this.query = query;
		this.scope = scope;
		con = DBConnect.getConnection();
		isDonePopulating = false;
		this.toCommit = toCommit;
		this.iso_level = iso_level;
	}
	
	@Override
	public void setIsolationLevel(int iso_level) {
		// TODO Auto-generated method stub
		try {
			switch(iso_level) {
			case ISO_READ_UNCOMMITTED: //ps.setString(1, "READ UNCOMMITTED"); 
					con.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
				break;
			case ISO_READ_COMMITTED: //ps.setString(1, "READ COMMITTED");
					con.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
				break;
			case ISO_REPEATABLE_READ: //ps.setString(1, "REPEATABLE READ");
					con.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
			 	break;
			case ISO_SERIALIZABLE: //ps.setString(1, "SERIALIZABLE");
					con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				break;
			default: //ps.setString(1, "SERIALIZABLE");
					con.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
				break;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	@Override
	public void beginTransaction() throws SQLException{
		java.util.Date date= new java.util.Date();
		con.setAutoCommit(false);
		stmt = con.createStatement();
	}
	
	

	@Override
	public void rollback() {
		try{
			con.rollback();
		} catch (SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public void end() {
		try{
			if(toCommit)
				con.commit();
			else
				con.rollback();
		} catch (SQLException e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void commit() {
		try{
			con.commit();
		} catch (SQLException e){
			e.printStackTrace();
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			beginTransaction();
			start();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void start() {
		try{
			String lock="";
			lock = "LOCK TABLES databyyear WRITE;";
			
			stmt.execute(lock);
			String SQL = query;
			stmt.executeUpdate(SQL);
			
			isDonePopulating = true;
			String unlock = "UNLOCK TABLES;";
			stmt.execute(unlock);
		}catch(SQLException e){
			e.printStackTrace();
		}
		
	}
	
	public CachedRowSetImpl getResultSet(){
		return null;
	}


	public String getQuery() {
		return query;
	}
	
	public boolean isDonePopulating(){
		return isDonePopulating;
	}

	@Override
	public String getScope() {
		// TODO Auto-generated method stub
		return scope;
	}

	public boolean isToCommit() {
		return toCommit;
	}

	@Override
	public int getIsolationLevel() {
		// TODO Auto-generated method stub
		return iso_level;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public void setConnection() {
		// TODO Auto-generated method stub
		con = DBConnect.getConnection();
		
	}

	@Override
	public void setConnectionReplica() {
		// TODO Auto-generated method stub
		con = DBConnect.getConnectionReplica();
		
	}

	
}
