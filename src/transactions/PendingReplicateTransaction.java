package transactions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

import Controller.Controller;
import Model.Constants;
import network.Client;
import network.SerializableTrans;

public class PendingReplicateTransaction implements Runnable, Serializable{

	List<WriteTransaction> translist;
	Client myClient;
	Controller parent;
	Thread x;
	
	public PendingReplicateTransaction(Client c, Controller p){
		translist = new ArrayList<WriteTransaction>();
		myClient = c;
		parent = p;
		x = new Thread(this);
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
	
		System.out.println(translist.toString());
		for(int i=translist.size()-1; i>=0; i--){
			boolean exist;
			
			if(!translist.get(i).getScope().equals(parent.getReplicaName()))
				switch(translist.get(i).getScope()){
					case Constants.HOST_ALL:
						exist = myClient.checkEuropeAmericaIfExists();
						break;
					case Constants.HOST_EUROPE_AMERICA:
						exist = myClient.checkAsiaAfricaIfExists();
						break;
					case Constants.HOST_ASIA_AFRICA:
						exist = myClient.checkAllRegionsIfExists();
						break;
					default: exist = false;
			}else{
				exist = true;
			}
			System.out.println("EXISTING: "+exist);
			
			if(exist){
				WriteTransaction t = translist.get(i);
				try{
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
	                    

	    				switch(translist.get(i).getScope()){
	    					case Constants.HOST_ALL:
	    		            	parent.sendToHost(fin, parent.getAddressOf(Constants.HOST_ALL_REPLICA));
	    						break;
	    					case Constants.HOST_EUROPE_AMERICA:
	    		            	parent.sendToHost(fin, parent.getAddressOf(Constants.HOST_EUROPE_AMERICA_REPLICA));
	    						break;
	    					case Constants.HOST_ASIA_AFRICA:
	    		            	parent.sendToHost(fin, parent.getAddressOf(Constants.HOST_ASIA_AFRICA_REPLICA));
	    						break;
	    					default: exist = false;
	    				}

	        		}
	        		translist.remove(i);
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
			
		}
		

		
	}
	
	public void start(){
		
		while(true){
			x.run();
			
		}
	}
	
	public void addReplication(WriteTransaction t){
		translist.add(t);
		
		
		
		if(!t.getScope().equals(Constants.HOST_ALL)){
			WriteTransaction r = new WriteTransaction(t.getQuery(), Constants.HOST_ALL, t.isToCommit(), t.getIsolationLevel());
			translist.add(r);
		}
		x.start();

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
