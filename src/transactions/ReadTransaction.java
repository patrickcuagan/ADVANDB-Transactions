package transactions;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import com.sun.rowset.CachedRowSetImpl;

import database.DBConnect;

public class ReadTransaction implements Transaction, Serializable, Runnable{
	String scope;
	String query;
	Connection con;
	Statement stmt;
	volatile CachedRowSetImpl cs;
	volatile boolean donePopulating;
	int iso_level;
	String name;
	
	public ReadTransaction(String query, String scope, int iso_level){
		this.scope = scope;
		cs = null;
		this.query = query;
		con = DBConnect.getConnection();
		donePopulating = false;
		this.iso_level = iso_level;
	}
	
	
	


	public String getQuery() {
		return query;
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

	@Override
	public void commit() {
		try{
			con.commit();
		} catch (SQLException e){
			e.printStackTrace();
		}
		
	}


	@Override
	public void start() {
		//QUERY CODE BLOCK
		try{
			String lock="";
			lock = "LOCK TABLES countryregion READ;";
			stmt.execute(lock);
			ResultSet rs = stmt.executeQuery(query);
			cs = new CachedRowSetImpl();
			cs.populate(rs);
			String unlock = "UNLOCK TABLES;";
			Statement stmt2 = con.createStatement();
			stmt2.execute(unlock);
			donePopulating = true;
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	
	public boolean isDonePopulating(){
		return donePopulating;
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
	
	public void setQuery(String query){
		this.query = query;
	}
	
	public CachedRowSetImpl getResultSet(){
		return cs;
	}





	@Override
	public String getScope() {
		// TODO Auto-generated method stub
		return scope;
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
