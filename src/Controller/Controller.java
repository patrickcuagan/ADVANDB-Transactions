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
import transactions.Transaction;
import transactions.Transaction1;
import transactions.Transaction2;
import View.MainFrame;
import Controller.Driver;

public class Controller {

    private int iso_level = 1;
    private Client myClient;
    private Server myServer;
    private CachedRowSetImpl cs;
    private String name = "";
    private ArrayList<Transaction> transactions;
    private ArrayList<String> queries, scopes;
    Transaction1 pendingWrite;
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
        		if (transaction instanceof Transaction2) {
        			switch(transaction.getScope()){
        				case "MARINDUQUE": readMarinduque((Transaction2)transaction);System.out.println("MAR"); break; 
        				case "PALAWAN": readPalawan((Transaction2)transaction); break;
        				case "BOTH": readBoth((Transaction2)transaction); break;
        			}
        		}
        		else if (transaction instanceof Transaction1) {
	        			switch(transaction.getScope()){
	    				case "MARINDUQUE": writeMarinduque((Transaction1)transaction); break; 
	    				case "PALAWAN": writePalawan((Transaction1)transaction); break;
	    				case "BOTH": writeBoth((Transaction1)transaction); break;
	    			}
	    		}
        	}
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    
    public void writePalawan(Transaction t) {
        if (name.equalsIgnoreCase("CENTRAL")) {
            if (myClient.checkPalawanIfExists()) {
            	try {
            		partialCommit(t);
            		String message = "\"ORDERWRITE\" ";
                    byte[] prefix = message.getBytes();
                    System.out.println(((Transaction1)t).isToCommit()+" :INSIDE WRITEPALAWAN");
                    SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), ((Transaction1)t).isToCommit(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(sertrans);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("PALAWAN"));
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            } else {
                //return false;
            }
        } else if (name.equalsIgnoreCase("PALAWAN")) {
            if (myClient.checkCentralIfExists()) {
            	try {
                    String message = "\"WRITEREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((Transaction1)t).isToCommit(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("CENTRAL"));
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            } else {
                //return false;
            }
        } else if (name.equalsIgnoreCase("MARINDUQUE")) {
            if (myClient.checkCentralIfExists() && myClient.checkPalawanIfExists()) {
            	try {
                    String message = "\"WRITEREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((Transaction1)t).isToCommit(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("CENTRAL"));
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            } else {
                //return false;
            }
        }
    }

    public void writeMarinduque(Transaction t) {
        if (name.equalsIgnoreCase("CENTRAL")) {
            if (myClient.checkMarinduqueIfExists()) {
            	try {
            		partialCommit(t);
            		String message = "\"ORDERWRITE\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), ((Transaction1)t).isToCommit(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(sertrans);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("MARINDUQUE"));
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            } else {
                //return false;
            }
        } else if (name.equalsIgnoreCase("MARINDUQUE")) {
            if (myClient.checkCentralIfExists()) {
            	try {
                    String message = "\"WRITEREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((Transaction1)t).isToCommit(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("CENTRAL"));
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            } else {
                //return false;
            }
        } else if (name.equalsIgnoreCase("PALAWAN")) {
            if (myClient.checkCentralIfExists() && myClient.checkMarinduqueIfExists()) {
            	try {
                    String message = "\"WRITEREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((Transaction1)t).isToCommit(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("CENTRAL"));
                } catch (IOException e) {
                    e.printStackTrace();
                } 
            } else {
                //return false;
            }
        }
    }

    public void writeBoth(Transaction t) {
        if (name.equalsIgnoreCase("CENTRAL")) { //jake seo
            if (myClient.checkMarinduqueIfExists() && myClient.checkPalawanIfExists()) {
            	try {
            		pendingWrite = (Transaction1) t;
            		pendingWrite.beginTransaction();
            		pendingWrite.start();
                    String message = "\"WRITEREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((Transaction1)t).isToCommit(), t.getIsolationLevel(), t.getName());
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
        } else if (name.equalsIgnoreCase("MARINDUQUE")) {
            if (myClient.checkCentralIfExists() && myClient.checkPalawanIfExists()) {
            	try {
            		pendingWrite = (Transaction1) t;
            		pendingWrite.beginTransaction();
            		pendingWrite.start();
                    String message = "\"WRITEREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((Transaction1)t).isToCommit(), t.getIsolationLevel(), t.getName());
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
        } else {
            if (myClient.checkCentralIfExists() && myClient.checkMarinduqueIfExists()) {
                //return true;
            } else {
                //return false;
            }
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
            } else if (myClient.checkPalawanIfExists()){
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
                    if (myClient.checkPalawanIfExists()) {
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
    	pendingWrite = (Transaction1)t;
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
