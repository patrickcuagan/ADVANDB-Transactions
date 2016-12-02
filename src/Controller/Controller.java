package Controller;

import com.sun.rowset.CachedRowSetImpl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.swing.JOptionPane;

import network.Client;
import network.SerializableTrans;
import network.Server;
import transactions.ReadTransaction;
import transactions.Transaction;
import transactions.WriteTransaction;
import View.MainFrame;
import Controller.Driver;
import Model.Constants;

public class Controller {

    private int iso_level = 1;
    private Client myClient;
    private Server myServer;
    private CachedRowSetImpl cs;
    private String name = "";
    private ArrayList<Transaction> transactions;
    private ArrayList<String> queries, scopes;
    WriteTransaction pendingWrite;
    Thread client, server;
    private MainFrame main;

    public Controller() {
    	main = new MainFrame(this);
        transactions = new ArrayList();
        myClient = null;
        myServer = null;
        pendingWrite = null;
        cs = null;
    }

    public void executeTransactions(ArrayList<Transaction> transactionsList) {

        try {
        	for (Transaction transaction : transactionsList){
        		
        		if (transaction instanceof ReadTransaction) {
        			switch(transaction.getScope()){
        				case Constants.HOST_ASIA_AFRICA: 	readAsiaAfrica((ReadTransaction)transaction);System.out.println("MAR"); break; 
        				case Constants.HOST_EUROPE_AMERICA: readEuroAmerica((ReadTransaction)transaction); break;
        				case Constants.HOST_ALL:			readAllRegions((ReadTransaction)transaction); break;
        			}
        		}
        		else if (transaction instanceof WriteTransaction) {
	        			switch(transaction.getScope()){
	    				case Constants.HOST_ASIA_AFRICA: 	writeAsiaAfrica((WriteTransaction)transaction); break; 
	    				case Constants.HOST_EUROPE_AMERICA: writeEuroAmerica((WriteTransaction)transaction); break;
	    				case Constants.HOST_ALL:			writeAllRegions((WriteTransaction)transaction); break;
	    			}
	    		}
        		
        		
        	}
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    
    public void writeEuroAmerica(Transaction t){
    	switch(name){
    		case Constants.HOST_ALL:
    			//send to euroam + tell euroam to write
    			if(myClient.checkEuropeAmericaIfExists()){
    				try{
	            		partialCommit(t);
	            		String message = "\"ORDERWRITE\" ";
	                    byte[] prefix = message.getBytes();
	                    System.out.println(((WriteTransaction)t).isToCommit()+" :INSIDE WRITE_EURO_AM");
	                    SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), ((WriteTransaction)t).isToCommit(), t.getIsolationLevel(), t.getName());
	                    byte[] trans = serialize(sertrans);
	                    byte[] fin = byteConcat(prefix, trans);
	                    myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_EUROPE_AMERICA));
    				}catch(Exception E){
    					E.printStackTrace();
    				}
    				
    			}
    			break;
    		case Constants.HOST_EUROPE_AMERICA:
    		case Constants.HOST_ASIA_AFRICA:
    			//send to all_reg
    			if(myClient.checkAllRegionsIfExists()){
                	try {
                        String message = "\"WRITEREQUEST\" ";
                        byte[] prefix = message.getBytes();
                        SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((WriteTransaction)t).isToCommit(), t.getIsolationLevel(), t.getName());
                        byte[] trans = serialize(st);
                        byte[] fin = byteConcat(prefix, trans);
                        myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_ALL));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }    				
    			}
    			break;
    	}
    }
    
    public void writeAsiaAfrica(Transaction t){
    	switch(name){
		case Constants.HOST_ALL:
			//send to euroam + tell euroam to write
			if(myClient.checkEuropeAmericaIfExists()){
				try{
            		partialCommit(t);
            		String message = "\"ORDERWRITE\" ";
                    byte[] prefix = message.getBytes();
                    System.out.println(((WriteTransaction)t).isToCommit()+" :INSIDE WRITE_ASIA_AFRICA");
                    SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), ((WriteTransaction)t).isToCommit(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(sertrans);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_ASIA_AFRICA));
				}catch(Exception E){
					E.printStackTrace();
				}
				
			}
			break;
		case Constants.HOST_EUROPE_AMERICA:
		case Constants.HOST_ASIA_AFRICA:
			//send to all_reg
			if(myClient.checkAllRegionsIfExists()){
            	try {
                    String message = "\"WRITEREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((WriteTransaction)t).isToCommit(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_ALL));
                } catch (IOException e) {
                    e.printStackTrace();
                }    				
			}
			break;
	}
    }   
    
    public void writeAllRegions(Transaction t){
    	switch(name){
    		case Constants.HOST_ALL:
    			//begin writing + send requests
                if (myClient.checkAsiaAfricaIfExists() && myClient.checkEuropeAmericaIfExists()) {
                	try {
                		pendingWrite = (WriteTransaction) t;
                		pendingWrite.beginTransaction();
                		pendingWrite.start();
                        String message = "\"WRITEREQUEST\" ";
                        byte[] prefix = message.getBytes();
                        SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((WriteTransaction)t).isToCommit(), t.getIsolationLevel(), t.getName());
                        byte[] trans = serialize(st);
                        byte[] fin = byteConcat(prefix, trans);
                        myClient.SEND(fin, myClient.getAddressFromName("PALAWAN"));
                        myClient.SEND(fin, myClient.getAddressFromName("MARINDUQUE"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e){
                    	e.printStackTrace();
                    }
                } else {
                    //return false;
                }
    			break;
    		case Constants.HOST_EUROPE_AMERICA:
                if (myClient.checkAllRegionsIfExists() && myClient.checkEuropeAmericaIfExists()) {
                	try {
                		pendingWrite = (WriteTransaction) t;
                		pendingWrite.beginTransaction();
                		pendingWrite.start();
                        String message = "\"WRITEREQUEST\" ";
                        byte[] prefix = message.getBytes();
                        SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((WriteTransaction)t).isToCommit(), t.getIsolationLevel(), t.getName());
                        byte[] trans = serialize(st);
                        byte[] fin = byteConcat(prefix, trans);
                        myClient.SEND(fin, myClient.getAddressFromName("PALAWAN"));
                        myClient.SEND(fin, myClient.getAddressFromName("CENTRAL"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e){
                    	e.printStackTrace();
                    }
                } else {
                    //return false;
                }
    			break;
    		case Constants.HOST_ASIA_AFRICA:
    			//do nothing
    			break;
    	}
    }
    
    public void readEuroAmerica(ReadTransaction t){
    	switch(name){
			case Constants.HOST_ALL:
				break;
			case Constants.HOST_EUROPE_AMERICA:
	            Thread x = new Thread(t);
	            x.start();
	            while (true) {
	                if (t.isDonePopulating()) {
	                    break;
	                }
	            }
	            printResultSet(t.getResultSet(),t.getName());
				break;
			case Constants.HOST_ASIA_AFRICA:
				break;
			default:
				System.out.println("A NEEDED SERVER IS DOWN");
    	}
    }

    public void readAsiaAfrica(ReadTransaction t){
    	switch(name){
			case Constants.HOST_ALL:
				break;
			case Constants.HOST_EUROPE_AMERICA:
				break;
			case Constants.HOST_ASIA_AFRICA:
	            Thread x = new Thread(t);
	            x.start();
	            while (true) {
	                if (t.isDonePopulating()) {
	                    break;
	                }
	            }
	            printResultSet(t.getResultSet(),t.getName());
				break;
			default:
				System.out.println("A NEEDED SERVER IS DOWN");
    	}
    }
    
    public void readAllRegions(ReadTransaction t){
    	switch(name){
		case Constants.HOST_ALL:
			break;
		case Constants.HOST_EUROPE_AMERICA:
			break;
		case Constants.HOST_ASIA_AFRICA:
			break;
    	}
    }
    
    public void readPalawan(Transaction2 t) throws SQLException {
        if (name.equalsIgnoreCase("PALAWAN")) {
            Thread x = new Thread(t);
            x.start();
            while (true) {
                if (t.isDonePopulating()) {
                    break;
                }
            }
            printResultSet(t.getResultSet(),t.getName());
        } else if (name.equalsIgnoreCase("CENTRAL")) {
        	String editQuery="";
        	if(t.getQuery().contains("WHERE") || t.getQuery().contains("where") || t.getQuery().contains("Where")){
        		editQuery= t.getQuery()+" AND location='Palawan' ";
        	}else{
        		editQuery= t.getQuery()+" WHERE location='Palawan' ";
        	}
        	t.setQuery(editQuery);
            Thread x = new Thread(t);
            x.start();
            while (true) {
                if (t.isDonePopulating()) {
                    break;
                }
            }
            printResultSet(t.getResultSet(),t.getName());
        } else if (name.equalsIgnoreCase("MARINDUQUE")) {
            if (myClient.checkCentralIfExists()) {
                try {
                	String editQuery="";
                	if(t.getQuery().contains("WHERE") || t.getQuery().contains("where") || t.getQuery().contains("Where")){
                		editQuery= t.getQuery()+" AND location='Palawan' ";
                	}else{
                		editQuery= t.getQuery()+" WHERE location='Palawan' ";
                	}
                	t.setQuery(editQuery);
                	
                    String message = "\"READREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("CENTRAL"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (myClient.checkEuropeAmericaIfExists()){
            	try {
                    String message = "\"READREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("PALAWAN"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
            	Driver.printMessage("A NEEDED SERVER IS DOWN");
            }
        }
    }

    public void readMarinduque(Transaction2 t) throws SQLException {
        if (name.equalsIgnoreCase("MARINDUQUE")) {
            Thread x = new Thread(t);
            x.start();
            while (true) {
                if (t.isDonePopulating()) {
                    break;
                }
            }
            printResultSet(t.getResultSet(),t.getName());
        } else if (name.equalsIgnoreCase("CENTRAL")) {
        	String editQuery="";
        	if(t.getQuery().contains("WHERE") || t.getQuery().contains("where") || t.getQuery().contains("Where")){
        		editQuery= t.getQuery()+" AND location='Marinduque' ";
        	}else{
        		editQuery= t.getQuery()+" WHERE location='Marinduque' ";
        	}
        	t.setQuery(editQuery);
            Thread x = new Thread(t);
            x.start();
            while (true) {
                if (t.isDonePopulating()) {
                    break;
                }
            }
            printResultSet(t.getResultSet(),t.getName());
        } else if (name.equalsIgnoreCase("PALAWAN")) {
            if (myClient.checkCentralIfExists()) {
                try {
                	String editQuery="";
                	if(t.getQuery().contains("WHERE") || t.getQuery().contains("where") || t.getQuery().contains("Where")){
                		editQuery= t.getQuery()+" AND location='Marinduque' ";
                	}else{
                		editQuery= t.getQuery()+" WHERE location='Marinduque' ";
                	}
                	t.setQuery(editQuery);
                    String message = "\"READREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("CENTRAL"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (myClient.checkMarinduqueIfExists()) { 
            	try {
                    String message = "\"READREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("MARINDUQUE"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                Driver.printMessage("A NEEDED SERVER IS DOWN");
            }
        }
    }

    public void readBoth(Transaction2 t) throws SQLException {
        if (name.equalsIgnoreCase("CENTRAL")) {
            Thread x = new Thread(t);
            x.start();
            while (true) {
                if (t.isDonePopulating()) {
                    break;
                }
            }
            printResultSet(t.getResultSet(),t.getName());
        } else {
            if (myClient.checkCentralIfExists()) {
                try {
                    Driver.printMessage("CENTRAL EXISTS");
                    String message = "\"READREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("CENTRAL"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (name.equalsIgnoreCase("MARINDUQUE")) {
                    if (myClient.checkEuropeAmericaIfExists()) {
                        try {
                            t.beginTransaction();
                            t.start();
                            cs = t.getResultSet();

                            Driver.printMessage("CENTRAL EXISTS");
                            String message = "\"READREQUESTCOMBINE\" ";
                            byte[] prefix = message.getBytes();
                            SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(),t.getIsolationLevel(), t.getName());
                            byte[] trans = serialize(st);
                            byte[] fin = byteConcat(prefix, trans);
                            myClient.SEND(fin, myClient.getAddressFromName("PALAWAN"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Driver.printMessage("A NEEDED SERVER IS DOWN");
                    }
                } else {
                    if (myClient.checkMarinduqueIfExists()) {
                        try {
                            t.beginTransaction();
                            t.start();
                            cs = t.getResultSet();

                            Driver.printMessage("CENTRAL EXISTS");
                            String message = "\"READREQUESTCOMBINE\" ";
                            byte[] prefix = message.getBytes();
                            SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), t.getIsolationLevel(), t.getName());
                            byte[] trans = serialize(st);
                            byte[] fin = byteConcat(prefix, trans);
                            myClient.SEND(fin, myClient.getAddressFromName("MARINDUQUE"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Driver.printMessage("A NEEDED SERVER IS DOWN");
                    }
                }
            }
        }
    }
    
    
    public void commitPendingWrite(){
    	if(pendingWrite!=null){
	    	pendingWrite.end();
	    	pendingWrite = null;
    	}
    }
    
    public void partialCommit(Transaction t){
    	pendingWrite = (WriteTransaction)t;
    	try {
    		pendingWrite.beginTransaction();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	pendingWrite.start();
    }
    
    public boolean isAddressOf(String host, InetAddress address){
    	return myClient.getAddressFromName(host).equals(address);
    }
    
    public InetAddress getAddressOf(String host){
    	return myClient.getAddressFromName(host);
    }

    public static byte[] byteConcat(byte[] A, byte[] B) {
        int aLen = A.length;
        int bLen = B.length;
        byte[] C = new byte[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }

    public void addNodeName(String name) {
        myServer.addNodeName(name);
    }

    public static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ObjectOutputStream os = new ObjectOutputStream(out);
        os.writeObject(obj);
        return out.toByteArray();
    }

    public Client getMyClient() {
        return myClient;
    }

    public Server getMyServer() {
        return myServer;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void printMessage(String message) {
        Driver.printMessage(message);
    }

    public void sendToHost(byte[] msg, InetAddress receiver) {
        myClient.SEND(msg, receiver);
    }

    public void startClient() {
        myClient = new Client(this);
        client = new Thread(myClient);
        client.start();
    }

    public void startServer(int port) {
        myServer = new Server(port, this);
        server = new Thread(myServer);
        server.start();
    }

    public void stopServer() {
        myServer.setFlag(1);
        myServer = null;
    }

    public void setIsoLevel(int iso_level) {
        this.iso_level = iso_level;
    }

    public void printResultSet(CachedRowSetImpl rs, String tableName) {
        //Driver.printResultSet(rs);
        main.updateTable(tableName, rs, null);
    }

    public void printCombinedResultSet(CachedRowSetImpl rs2, String tableName) {
    	//tablename = cs.getName();
//        Driver.printResultSet(cs);
//        cs = null;
//        Driver.printResultSet(rs2);
        main.updateTable(tableName, cs, rs2);
    }

    public CachedRowSetImpl getCs() {
        return cs;
    }

    public void setCs(CachedRowSetImpl cs) {
        this.cs = cs;
    }

}
