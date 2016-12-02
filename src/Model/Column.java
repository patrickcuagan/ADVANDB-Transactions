package Model;

public class Column {
	
	private String colName;
	private String name;
	private int type;
	
	public Column(String colName, String name, int type) {
		this.setColName(colName);
		this.setName(name);
		this.setType(type);
	}
	
	public void setColName(String colName) {
		this.colName = colName;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(int type) {
		this.type = type;
	}
	
	public String getColName() {
		return colName;
	}
	
	public String getName() {
		return name;
	}
	
	public int getType() {
		return type;
	}

}
