package transactions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
		x = new Thread();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		for(int i=0; i<translist.size(); i++){
			boolean exist;
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
			}
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
		            	parent.sendToHost(fin, parent.getAddressOf(t.getScope()));
	        		}
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
			}
			
		}
		
	}
	
	public void start(){
		
		while(true){
			x.start();
			
			try {
				x.wait(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void addReplication(WriteTransaction t){
		translist.add(t);
		x.notify();
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
