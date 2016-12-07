package Controller;

import com.sun.rowset.CachedRowSetImpl;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
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
    	System.out.print("TEST transact: ");
    	System.out.println(transactionsList.toString());
        try {
        	for (Transaction transaction : transactionsList){
        		
        		if (transaction instanceof ReadTransaction) {
        			System.out.println("TESTREAD");
        			System.out.println(transaction.getScope());
        			switch(transaction.getScope()){
        				case Constants.REGION_ASIA_AFRICA: 		System.out.println("R_AS");readAsiaAfrica((ReadTransaction)transaction);System.out.println("MAR"); break; 
        				case Constants.REGION_EUROPE_AMERICA: 	System.out.println("R_AM");readEuroAmerica((ReadTransaction)transaction); break;
        				case Constants.REGION_BOTH:				System.out.println("R_ALL");readAllRegions((ReadTransaction)transaction); break;
        				default: System.out.println("Cannot Read");
        			}
        		}
        		else if (transaction instanceof WriteTransaction) {
        			System.out.println("TESTWRITE");
	        			switch(transaction.getScope()){
	    				case Constants.REGION_ASIA_AFRICA: 		System.out.println("W_AS");writeAsiaAfrica((WriteTransaction)transaction); break; 
	    				case Constants.REGION_EUROPE_AMERICA: 	System.out.println("W_AM");writeEuroAmerica((WriteTransaction)transaction); break;
	    				case Constants.REGION_BOTH:				System.out.println("WA_ALL");writeAllRegions((WriteTransaction)transaction); break;
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

			if(myClient.checkAsiaAfricaIfExists()){
				try{
            		partialCommit(t);
            		String message = "\"ORDERWRITE\" \"ORIGINAL\"";
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
                    
//            		partialCommit(t);
            		message = "\"ORDERWRITE\" \"REPLICATE\"";
                    prefix = message.getBytes();
                    System.out.println(((WriteTransaction)t).isToCommit()+" :INSIDE WRITE_ASIA_AFRICA");
                    SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), ((WriteTransaction)t).isToCommit(), t.getIsolationLevel(), t.getName());
                    trans = serialize(sertrans);
                    byte[] fin2 = byteConcat(prefix, trans);
                    myClient.SEND(fin2, myClient.getAddressFromName(Constants.HOST_ALL));
                    
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
//                if (myClient.checkAsiaAfricaIfExists() && myClient.checkEuropeAmericaIfExists()) {
                	try {
                		pendingWrite = (WriteTransaction) t;
                		pendingWrite.beginTransaction();
                		pendingWrite.start();
                        String message = "\"WRITEREQUEST\" ";
                        byte[] prefix = message.getBytes();
                        SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((WriteTransaction)t).isToCommit(), t.getIsolationLevel(), t.getName());
                        byte[] trans = serialize(st);
                        byte[] fin = byteConcat(prefix, trans);
//                        myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_ASIA_AFRICA));
//                        myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_EUROPE_AMERICA));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e){
                    	e.printStackTrace();
                    }
//                } else {
//                    //return false;
//                }
    			break;
    		case Constants.HOST_EUROPE_AMERICA:
                if (myClient.checkAllRegionsIfExists() && myClient.checkAsiaAfricaIfExists()) {
                	try {
                		pendingWrite = (WriteTransaction) t;
                		pendingWrite.beginTransaction();
                		pendingWrite.start();
                        String message = "\"WRITEREQUEST\" ";
                        byte[] prefix = message.getBytes();
                        SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), ((WriteTransaction)t).isToCommit(), t.getIsolationLevel(), t.getName());
                        byte[] trans = serialize(st);
                        byte[] fin = byteConcat(prefix, trans);
                        myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_ASIA_AFRICA));
                        myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_ALL));
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
    	System.out.println("TEST EUROAM");
    	Thread x;
    	String editQuery;
    	switch(name){
			case Constants.HOST_ALL:
	        	editQuery="";
	        	if(t.getQuery().contains("WHERE") || t.getQuery().contains("where") || t.getQuery().contains("Where")){
	        		editQuery= t.getQuery()+" AND region IN ('Europe & Central Asia', 'Latin America & Caribbean', 'North America') ";
	        	}else{
	        		editQuery= t.getQuery()+" WHERE region IN ('Europe & Central Asia', 'Latin America & Caribbean', 'North America') ";
	        	}
	        	t.setQuery(editQuery);
	            x = new Thread(t);
	            x.start();
	            while (true) {
	                if (t.isDonePopulating()) {
	                    break;
	                }
	            }
	            printResultSet(t.getResultSet(),t.getName());
				break;
			case Constants.HOST_EUROPE_AMERICA:
	            x = new Thread(t);
	            x.start();
	            while (true) {
	                if (t.isDonePopulating()) {
	                    break;
	                }
	            }
	            printResultSet(t.getResultSet(),t.getName());
				break;
			case Constants.HOST_ASIA_AFRICA:
				if (myClient.checkAllRegionsIfExists()) {
	                try {
	                	editQuery="";
	                	if(t.getQuery().contains("WHERE") || t.getQuery().contains("where") || t.getQuery().contains("Where")){
	    	        		editQuery= t.getQuery()+" AND region IN ('Europe & Central Asia', 'Latin America & Caribbean', 'North America') ";
	    	        	}else{
	    	        		editQuery= t.getQuery()+" WHERE region IN ('Europe & Central Asia', 'Latin America & Caribbean', 'North America') ";
	                	}
	                	t.setQuery(editQuery);
	                	
	                    String message = "\"READREQUEST\" ";
	                    byte[] prefix = message.getBytes();
	                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), t.getIsolationLevel(), t.getName());
	                    byte[] trans = serialize(st);
	                    byte[] fin = byteConcat(prefix, trans);
	                    myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_ALL));
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
	                    myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_EUROPE_AMERICA));

	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
				break;
			default:
				System.out.println("A NEEDED SERVER IS DOWN");
    	}
    }

    public void readAsiaAfrica(ReadTransaction t){
    	String editQuery;
    	Thread x;
    	switch(name){
			case Constants.HOST_ALL:
	        	editQuery="";
	        	if(t.getQuery().contains("WHERE") || t.getQuery().contains("where") || t.getQuery().contains("Where")){
	        		editQuery= t.getQuery()+" AND region IN ('East Asia and Pacific', 'Middle East & North Africa', 'South Asia', 'Sub-Saharan Africa) ";
	        	}else{
	        		editQuery= t.getQuery()+" WHERE region IN ('East Asia and Pacific', 'Middle East & North Africa', 'South Asia', 'Sub-Saharan Africa) ";
	        	}
	        	t.setQuery(editQuery);
	            x = new Thread(t);
	            x.start();
	            while (true) {
	                if (t.isDonePopulating()) {
	                    break;
	                }
	            }
	            printResultSet(t.getResultSet(),t.getName());
				break;
			case Constants.HOST_EUROPE_AMERICA:
				if (myClient.checkAllRegionsIfExists()) {
	                try {
	                	editQuery="";
	                	if(t.getQuery().contains("WHERE") || t.getQuery().contains("where") || t.getQuery().contains("Where")){
	    	        		editQuery= t.getQuery()+" AND region IN ('East Asia and Pacific', 'Middle East & North Africa', 'South Asia', 'Sub-Saharan Africa) ";
	    	        	}else{
	    	        		editQuery= t.getQuery()+" WHERE region IN ('East Asia and Pacific', 'Middle East & North Africa', 'South Asia', 'Sub-Saharan Africa) ";
	                	}
	                	t.setQuery(editQuery);
	                	
	                    String message = "\"READREQUEST\" ";
	                    byte[] prefix = message.getBytes();
	                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), t.getIsolationLevel(), t.getName());
	                    byte[] trans = serialize(st);
	                    byte[] fin = byteConcat(prefix, trans);
	                    myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_ALL));
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            } else if (myClient.checkAsiaAfricaIfExists()){
	            	try {
	                    String message = "\"READREQUEST\" ";
	                    byte[] prefix = message.getBytes();
	                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), t.getIsolationLevel(), t.getName());
	                    byte[] trans = serialize(st);
	                    byte[] fin = byteConcat(prefix, trans);
	                    myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_EUROPE_AMERICA));

	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	            }
				break;
			case Constants.HOST_ASIA_AFRICA:
	            x = new Thread(t);
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
    	Thread x;
    	
        if (name.equalsIgnoreCase(Constants.HOST_ALL)) {
            x = new Thread(t);
            x.start();
            while (true) {
                if (t.isDonePopulating()) {
                    break;
                }
            }
            printResultSet(t.getResultSet(),t.getName());
        } else {
        	if(myClient.checkAllRegionsIfExists()){
                try {
                    Driver.printMessage("ALL_REGIONS EXISTS");
                    String message = "\"READREQUEST\" ";
                    byte[] prefix = message.getBytes();
                    SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(), t.getIsolationLevel(), t.getName());
                    byte[] trans = serialize(st);
                    byte[] fin = byteConcat(prefix, trans);
                    myClient.SEND(fin, myClient.getAddressFromName("CENTRAL"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
        		
        	}
        	else{
		    	switch(name){
					case Constants.HOST_EUROPE_AMERICA:
	                    if (myClient.checkAsiaAfricaIfExists()) {
	                        try {
	                            
								t.beginTransaction();
								
	                            t.start();
	                            cs = t.getResultSet();

	                            Driver.printMessage("ASAF EXISTS");
	                            String message = "\"READREQUESTCOMBINE\" ";
	                            byte[] prefix = message.getBytes();
	                            SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(),t.getIsolationLevel(), t.getName());
	                            byte[] trans = serialize(st);
	                            byte[] fin = byteConcat(prefix, trans);
	                            myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_ASIA_AFRICA));
	                            
	                        } catch (IOException e) {
	                            e.printStackTrace();
	                        } catch (SQLException e) {
								e.printStackTrace();
							}
	                    } else {
	                        Driver.printMessage("A NEEDED SERVER IS DOWN");
	                    }
						
						break;
					case Constants.HOST_ASIA_AFRICA:
	                    if (myClient.checkEuropeAmericaIfExists()) {
	                        try {
	                            
								t.beginTransaction();
								
	                            t.start();
	                            cs = t.getResultSet();

	                            Driver.printMessage("EUAM EXISTS");
	                            String message = "\"READREQUESTCOMBINE\" ";
	                            byte[] prefix = message.getBytes();
	                            SerializableTrans st = new SerializableTrans(t.getQuery(), t.getScope(),t.getIsolationLevel(), t.getName());
	                            byte[] trans = serialize(st);
	                            byte[] fin = byteConcat(prefix, trans);
	                            myClient.SEND(fin, myClient.getAddressFromName(Constants.HOST_EUROPE_AMERICA));
	                            
	                        } catch (IOException e) {
	                            e.printStackTrace();
	                        } catch (SQLException e) {
								e.printStackTrace();
							}
	                    } else {
	                        Driver.printMessage("A NEEDED SERVER IS DOWN");
	                    }
						
						break;
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
