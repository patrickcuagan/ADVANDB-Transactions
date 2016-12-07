
package network;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Christian Gabriel
 */

import java.io.*;
import java.net.*;
import java.sql.ResultSet;
import java.util.Scanner;

import com.sun.rowset.CachedRowSetImpl;

import Controller.Controller;
import Model.Constants;
import transactions.WriteTransaction;
import transactions.ReadTransaction;

public class ServerReturn{
    
    private Scanner INPUT;
    private InputStream extractor;
    private PrintWriter OUT;
    String MESSAGE ="";
    Controller parent;
    byte[] mybytearray=new byte[65000000];
    int current=0;
    String messageType="";
    
    public ServerReturn(Socket X, Controller parent){
    	this.parent = parent;
    	parent.printMessage("Created server return");
        RECEIVE(X);
    }
    
    public synchronized void RECEIVE(Socket X){
    	parent.printMessage("IN RECIEVE");
    	extractByte(X);
    	if(current!=-1){
    		extractHeader(mybytearray);
    		System.out.println("message: "+messageType);
    		try{
	        	if(messageType.contains("\"READREQUEST\"")){
	        		parent.printMessage("IN READREQUEST");
	        		SerializableTrans st = (SerializableTrans) deserialize(mybytearray);
	        		ReadTransaction t = new ReadTransaction(st.getQuery(),st.getScope(), st.getIso_level());
	        		t.setIsolationLevel(st.getIso_level());
	            	t.beginTransaction();
	            	t.start();
	            	DatabaseObject dbo = new DatabaseObject(t.getResultSet(), st.getTransName());
	            	String first="\"RETURNREADREQUEST\" ";
	            	byte[] prefix = first.getBytes();
	            	byte[] dboBytes = serialize(dbo);
	            	byte[] finalByte = byteConcat(prefix, dboBytes);
	            	parent.printMessage("ADDRESS IN RETURN REQUEST: "+X.getInetAddress());
	            	parent.sendToHost(finalByte, X.getInetAddress());
	            	
	        	}else if(messageType.contains("\"RETURNREADREQUEST\"")){
	        		DatabaseObject dbo = (DatabaseObject)deserialize(mybytearray);
	        		CachedRowSetImpl rs = dbo.getResultSet();
	        		parent.printResultSet(rs, dbo.getTransName());
	        	}else if(messageType.contains("\"READREQUESTCOMBINE\"")){
	        		SerializableTrans st = (SerializableTrans) deserialize(mybytearray);
	        		ReadTransaction t = new ReadTransaction(st.getQuery(),st.getScope(), st.getIso_level());
	        		t.setIsolationLevel(st.getIso_level());
	            	t.beginTransaction();
	            	t.start();
	            	DatabaseObject dbo = new DatabaseObject(t.getResultSet(), st.getTransName());
	            	String first="\"RETURNREADREQUESTCOMBINE\" ";
	            	byte[] prefix = first.getBytes();
	            	byte[] dboBytes = serialize(dbo);
	            	byte[] finalByte = byteConcat(prefix, dboBytes);
	            	parent.printMessage("ADDRESS IN RETURN REQUEST: "+X.getInetAddress());
	            	parent.sendToHost(finalByte, X.getInetAddress());
	        	}else if(messageType.contains("\"RETURNREADREQUESTCOMBINE\"")){
	        		DatabaseObject dbo = (DatabaseObject)deserialize(mybytearray);
	        		CachedRowSetImpl rs = dbo.getResultSet();
	        		parent.printCombinedResultSet(rs, dbo.getTransName());
	        		
	        	/*}else if(messageType.contains("\"WRITEREQUEST\"")){
	        		parent.printMessage("IN WRITEREQUEST");
	        		SerializableTrans st = (SerializableTrans) deserialize(mybytearray);
	        		Transaction1 t = new Transaction1(st.getQuery(),st.getScope(), st.isToCommit());
	            	t.beginTransaction();
	            	t.start();
	            	t.end();
	            	
	            	if(t.getScope().equalsIgnoreCase("BOTH")){
	            		String first = "\"OKAYCOMMIT\" ";
            			parent.sendToHost(first.getBytes(), X.getInetAddress());
	            		if(parent.isAddressOf("PALAWAN", X.getInetAddress())){
	            			String message = "\"ORDERWRITE\" ";
                            byte[] prefix = message.getBytes();
                            SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit());
                            byte[] trans = serialize(sertrans);
                            byte[] fin = byteConcat(prefix, trans);
                            parent.sendToHost(fin, parent.getAddressOf("MARINDUQUE"));
	            		}else if(parent.isAddressOf("MARINDUQUE", X.getInetAddress())){
	            			String message = "\"ORDERWRITE\" ";
                            byte[] prefix = message.getBytes();
                            SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit());
                            byte[] trans = serialize(sertrans);
                            byte[] fin = byteConcat(prefix, trans);
                            parent.sendToHost(fin, parent.getAddressOf("PALAWAN"));
	            		}
	            	}else if(t.getScope().equalsIgnoreCase("PALAWAN")){
	            		if(parent.isAddressOf("PALAWAN", X.getInetAddress())){
	            			String first = "\"OKAYCOMMIT\" ";
	            			parent.sendToHost(first.getBytes(), X.getInetAddress());
	            		}else{
	            			String message = "\"ORDERWRITE\" ";
                            byte[] prefix = message.getBytes();
                            SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit());
                            byte[] trans = serialize(sertrans);
                            byte[] fin = byteConcat(prefix, trans);
                            parent.sendToHost(fin, parent.getAddressOf("PALAWAN"));
	            		}
	            	}else{
	            		if(parent.isAddressOf("MARINDUQUE", X.getInetAddress())){
	            			String first = "\"OKAYCOMMIT\" ";
	            			parent.sendToHost(first.getBytes(), X.getInetAddress());
	            		}else{
	            			String message = "\"ORDERWRITE\" ";
                            byte[] prefix = message.getBytes();
                            SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit());
                            byte[] trans = serialize(sertrans);
                            byte[] fin = byteConcat(prefix, trans);
                            parent.sendToHost(fin, parent.getAddressOf("MARINDUQUE"));
	            		}
	            	}*/
	        	/*}else if(messageType.contains("\"OKAYCOMMIT\"")){
	        		parent.commitPendingWrite();
	        	}else if(messageType.contains("\"ORDERWRITE\"")){
	        		SerializableTrans writetrans = (SerializableTrans) deserialize(mybytearray);
	        		Transaction1 t = new Transaction1(writetrans.getQuery(),writetrans.getScope(), writetrans.isToCommit());
	            	t.beginTransaction();
	            	t.start();
	            	t.end();*/
	        	}else if(messageType.contains("\"WRITEREQUEST\"")){
	        		parent.printMessage("IN WRITEREQUEST");
	        		SerializableTrans st = (SerializableTrans) deserialize(mybytearray);
	        		System.out.println("SCOPE: "+st.getScope());
	        		WriteTransaction t = new WriteTransaction(st.getQuery(),st.getScope(), st.isToCommit(), st.getIso_level());
	        		t.setName(st.getTransName());
	        		t.setIsolationLevel(st.getIso_level());
	            	parent.partialCommit(t);
	            	
	            	if(t.getScope().equalsIgnoreCase(Constants.HOST_EUROPE_AMERICA)){
	            		if(parent.isAddressOf(Constants.HOST_EUROPE_AMERICA, X.getInetAddress())){
	            			String msg = "\"GOCOMMIT\" ";
	    	            	SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit(), t.getIsolationLevel(), t.getName());
	    	            	byte[] prefix = msg.getBytes();
	                        byte[] trans = serialize(sertrans);
	                        byte[] fin = byteConcat(prefix, trans);
	    	            	parent.sendToHost(fin, X.getInetAddress());
	            		}else{
	            			String message = "\"ORDERWRITE\" ";
                            byte[] prefix = message.getBytes();
                            SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit(), t.getIsolationLevel(), t.getName());
                            byte[] trans = serialize(sertrans);
                            byte[] fin = byteConcat(prefix, trans);
                            parent.sendToHost(fin, parent.getAddressOf(Constants.HOST_EUROPE_AMERICA));
	            		}
	            	}else if(t.getScope().equalsIgnoreCase(Constants.HOST_ASIA_AFRICA)){
	            		if(parent.isAddressOf(Constants.HOST_ASIA_AFRICA, X.getInetAddress())){
	            			String msg = "\"GOCOMMIT\" ";
	    	            	SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit(), t.getIsolationLevel(), t.getName());
	    	            	byte[] prefix = msg.getBytes();
	                        byte[] trans = serialize(sertrans);
	                        byte[] fin = byteConcat(prefix, trans);
	    	            	parent.sendToHost(fin, X.getInetAddress());
	            		}else{
	            			String message = "\"ORDERWRITE\" ";
                            byte[] prefix = message.getBytes();
                            SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit(), t.getIsolationLevel(), t.getName());
                            byte[] trans = serialize(sertrans);
                            byte[] fin = byteConcat(prefix, trans);
                            parent.sendToHost(fin, parent.getAddressOf(Constants.HOST_ASIA_AFRICA));
	            		}
	            	}
	            	System.out.println(t.getScope());
	            	if(t.getScope().equalsIgnoreCase(Constants.HOST_EUROPE_AMERICAS_REPLICA)){
	            		System.out.println("---EURAM REPLICA-----");
            			
	            		if(parent.getReplicaName().equals(t.getScope())){
	            			
	    	        		t.setConnectionReplica();
	    	            	t.beginTransaction();
	    	            	t.start();
	    	            	t.end();
	    	            	
	            		}else{
	            			String msg = "\"GOCOMMIT\" ";
	    	            	SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit(), t.getIsolationLevel(), t.getName());
	    	            	byte[] prefix = msg.getBytes();
	                        byte[] trans = serialize(sertrans);
	                        byte[] fin = byteConcat(prefix, trans);
	    	            	parent.sendToHost(fin, parent.getAddressOf(Constants.HOST_EUROPE_AMERICA));
	            		}
	            		
	            	}else if(t.getScope().equalsIgnoreCase(Constants.HOST_ASIA_AFRICAS_REPLICA)){
	            		System.out.println("---ASAF REPLICA-----");
            			
	            		if(parent.getReplicaName().equals(t.getScope())){
	            			
	    	        		t.setConnectionReplica();
	    	            	t.beginTransaction();
	    	            	t.start();
	    	            	t.end();
	    	            	
	            		}else{
	            			String msg = "\"GOCOMMIT\" ";
	    	            	SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit(), t.getIsolationLevel(), t.getName());
	    	            	byte[] prefix = msg.getBytes();
	                        byte[] trans = serialize(sertrans);
	                        byte[] fin = byteConcat(prefix, trans);
	    	            	parent.sendToHost(fin, parent.getAddressOf(Constants.HOST_ASIA_AFRICA));
	            		}
	            	}else if(t.getScope().equalsIgnoreCase(Constants.HOST_ALLS_REPLICA)){
	            		System.out.println("---ALLS REPLICA-----");
	            			
	            		if(parent.getReplicaName().equals(t.getScope())){
	            			
	    	        		t.setConnectionReplica();
	    	            	t.beginTransaction();
	    	            	t.start();
	    	            	t.end();
	    	            	
	            		}else{
	            			String msg = "\"GOCOMMIT\" ";
	    	            	SerializableTrans sertrans = new SerializableTrans(t.getQuery(), t.getScope(), t.isToCommit(), t.getIsolationLevel(), t.getName());
	    	            	byte[] prefix = msg.getBytes();
	                        byte[] trans = serialize(sertrans);
	                        byte[] fin = byteConcat(prefix, trans);
	    	            	parent.sendToHost(fin, parent.getAddressOf(Constants.HOST_ALL));
	            		}
	            	}
	            	
	        	}else if(messageType.contains("\"GOCOMMIT\"")){
	        		SerializableTrans st = (SerializableTrans) deserialize(mybytearray);
	        		WriteTransaction t = new WriteTransaction(st.getQuery(),st.getScope(), st.isToCommit(), st.getIso_level());
	        		t.setName(st.getTransName());
	        		t.setIsolationLevel(st.getIso_level());
	        		
	        		System.out.println(t.getScope() + " = " + parent.getReplicaName());
	        		
	        		if(t.getScope().equalsIgnoreCase(parent.getReplicaName()))
	        			t.setConnectionReplica();
	        		
	            	t.beginTransaction();
	            	t.start();
	            	t.end();
	            	
	        		String msg = "\"DONECOMMIT\" ";
	        		parent.sendToHost(msg.getBytes(), X.getInetAddress());
	        	}else if(messageType.contains("\"ORDERWRITE\"")){
	        		SerializableTrans writetrans = (SerializableTrans) deserialize(mybytearray);
	        		WriteTransaction t = new WriteTransaction(writetrans.getQuery(),writetrans.getScope(), writetrans.isToCommit(), writetrans.getIso_level());
	            	t.setIsolationLevel(writetrans.getIso_level());

	        		if(t.getScope().equalsIgnoreCase(parent.getReplicaName()))
	        			t.setConnectionReplica();
	        		

	        		t.beginTransaction();
	            	t.start();
	            	t.end();
	            	
	            	String msg = "\"DONEWRITING\" ";
	        		parent.sendToHost(msg.getBytes(), X.getInetAddress());
	        	}else if(messageType.contains("\"DONECOMMIT\"") || messageType.contains("\"DONEWRITING\"")){
	        		System.out.println("WENTHERE");
	        		parent.commitPendingWrite();
	        	}
	        	X.close();
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    }
    
    public void extractHeader(byte[] X){
        char z;
        boolean condition = false;
        int i=0;
        String temp="";
        int count=0;
        while(condition==false){
            z=(char) X[i];
            temp=temp+z;
            if(z=='"' && count==0){
                count++;
            }
            else if(z=='"' && count==1){
                condition=true;
            }
            i++;
        }
        messageType=temp;
        parent.printMessage("EXTRACT HEADER: "+messageType);
        current=current-(i+1);
        if(current>0){
            byte[] splitter=new byte[current];
            System.arraycopy(mybytearray,i+1,splitter,0,splitter.length);
            mybytearray=splitter;
        }
        System.out.println("EXTRACT HEADER");
    }
    
    
    public void extractByte(Socket X){
        try{
        	parent.printMessage(X.getInetAddress().toString()+" XXX "+X.getInetAddress().getLocalHost().toString());
        	parent.printMessage("in extract byte: "+current);
            int bytesread;
            parent.printMessage("after bytesread");
            extractor = X.getInputStream();
            parent.printMessage("after extractor");
            bytesread = extractor.read(mybytearray, 0, mybytearray.length);
            parent.printMessage("after bytesread");
            current = bytesread;
            
            do{
            	parent.printMessage("bytesread: "+bytesread);
                bytesread=extractor.read(mybytearray,current,mybytearray.length-current);
                if(bytesread >= 0)
                    current += bytesread;
            }while(bytesread>-1);
            parent.printMessage("current: "+current);
        }
        catch(Exception e1){
            e1.printStackTrace();
        }
    }
    
    public static byte[] serialize(Object obj) throws IOException {
	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    ObjectOutputStream os = new ObjectOutputStream(out);
	    os.writeObject(obj);
	    return out.toByteArray();
	}
    
	public static Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
	    ByteArrayInputStream in = new ByteArrayInputStream(data);
	    ObjectInputStream is = new ObjectInputStream(in);
	    return is.readObject();
	}
    
	public static byte[] byteConcat(byte[] A, byte[] B) {
        int aLen = A.length;
        int bLen = B.length;
        byte[] C = new byte[aLen + bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
    }
        
}
