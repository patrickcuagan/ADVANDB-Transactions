package Model;

import java.util.ArrayList;
import java.util.Arrays;

public class ComboBoxConstants {
	
	private static int HAVING = 0;
	private static int FILTER = 1;

	public static ArrayList<Column> OPTIONS_QUERY = new ArrayList<Column>(Arrays.asList(
			new Column("hpq_hh_id", "Household ID", FILTER),
			new Column("mdeadage", "Age", FILTER)));
		
	public static Column findColumn(String name){
		ArrayList<Column> cols = OPTIONS_QUERY;
		for (Column c: cols){
			if (name.equals(c.getColName()) || name.equals(c.getName())){
				return c;
			}
		}
		return null;
	}
}