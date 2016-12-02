package network;
import java.io.Serializable;
import java.sql.ResultSet;

import com.sun.rowset.CachedRowSetImpl;

public class DatabaseObject implements Serializable {
	CachedRowSetImpl rs;
	String transName;
	
	
	
	public CachedRowSetImpl getRs() {
		return rs;
	}

	public void setRs(CachedRowSetImpl rs) {
		this.rs = rs;
	}

	public String getTransName() {
		return transName;
	}

	public void setTransName(String transName) {
		this.transName = transName;
	}

	public DatabaseObject(CachedRowSetImpl rs, String transName){
		this.rs = rs;
		this.transName = transName;
	}
	
	public void setResultSet(CachedRowSetImpl rs){
		this.rs = rs;
	}
	
	public CachedRowSetImpl getResultSet(){
		return rs;
	}
}
