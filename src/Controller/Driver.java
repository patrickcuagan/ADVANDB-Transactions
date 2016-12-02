package Controller;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

import Controller.Controller;
import View.MainFrame;
import transactions.Transaction;
import transactions.Transaction1;
import transactions.Transaction2;

import com.sun.rowset.CachedRowSetImpl;

public class Driver {
	
	
	public static void main(String args[]) {
		final int PORT=1515;
		
		Controller runner = new Controller();
		boolean isLocal = true;
		runner.setName("MARINDUQUE");
		runner.startServer(PORT);
		runner.startClient();
		Scanner sc = new Scanner(System.in);
		int condition=0;
		
		/*ArrayList<Transaction> transs = new ArrayList();
		transs.add(new Transaction1("UPDATE hpq_death SET mdeadage = '500' WHERE hpq_hh_id = 203424","MARINDUQUE", true,Transaction.ISO_READ_UNCOMMITTED));
		transs.add(new Transaction2("SELECT hpq_hh_id, death_line, mdeadsx, mdeadage, mdeady, mdeady_o FROM hpq_death","MARINDUQUE",Transaction.ISO_READ_UNCOMMITTED));
	
		condition = sc.nextInt();
		runner.executeTransactions(transs);*/
	
	}
	
	public static void printMessage(String message){
		System.out.println(message);
	}
	
	public static void printResultSet(CachedRowSetImpl rs){
		try{
			System.out.println("TRANSACTION BATCH:!!!!!!!!!");
			while(rs.next()){
				System.out.println("RS: "+rs.getString(1));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
}
