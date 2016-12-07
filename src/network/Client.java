package network;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

import Controller.Controller;
import Model.Constants;

public class Client implements Runnable{
    int port = 1515;
    ArrayList<InetAddress> hosts;
    ArrayList<String> names;
    PrintWriter OUT;
    Controller parent;
    int flag=0;
    
    public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}
    
    public int getHostsSize(){
    	return hosts.size();
    }
    
    public Client(Controller parent){
        this.parent = parent;
        hosts = new ArrayList();
        names = new ArrayList();
        
        try {
			System.out.println(InetAddress.getLocalHost());
		
        	hosts.add(InetAddress.getByName("172.20.10.7"));
        	hosts.add(InetAddress.getByName("172.20.10.6"));
        	
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //hosts.add(InetAddress.getByName("ip"));
        names.add(Constants.HOST_EUROPE_AMERICA);
        names.add(Constants.HOST_ASIA_AFRICA);
        
        
        //names.add("PALAWAN");
        
    }
    
    public void run(){
        try{
            try{
                CheckStream(); 
            }
            finally{
                //DISCONNECT();
            }
        }
        catch(Exception X){
        	X.printStackTrace();
            System.out.print(X);
        }
    }
    
    public void CheckStream(){
        while(true){
        	if(flag==1){
        		parent.printMessage("CLIENT STOPPED");
        		break;
        	}
            RECEIVE();
        }
    }
    
    public void RECEIVE(){
        
    }
    
    public void SEND (byte[] msg, InetAddress receiver){
    	System.out.println("SENDING TO : "+receiver);
    	try{
			Socket temp = new Socket(receiver,port);
			OutputStream output = temp.getOutputStream();
	        output.write(msg, 0, msg.length);
	        output.flush();
	        temp.close();
            output.close();
    	}catch(IOException e){
    		e.printStackTrace();
    	}
    }
    
    public boolean checkAllRegionsIfExists(){
    	try{
    		System.out.println("CHECKING ALL: "+hosts.get(names.indexOf(Constants.HOST_ALL)));
    		Socket s = new Socket(hosts.get(names.indexOf(Constants.HOST_ALL)), port);
    		s.close();
    	}catch(IOException e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    public boolean checkAsiaAfricaIfExists(){
    	try{
    		System.out.println("CHECKING AA: "+hosts.get(names.indexOf(Constants.HOST_ASIA_AFRICA)));
    		Socket s = new Socket(hosts.get(names.indexOf(Constants.HOST_ASIA_AFRICA)), port);
    		s.close();
    	}catch(IOException e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    public boolean checkEuropeAmericaIfExists(){
    	try{
    		System.out.println("CHECKING EA: "+hosts.get(names.indexOf(Constants.HOST_EUROPE_AMERICA)));
    		Socket s = new Socket(hosts.get(names.indexOf(Constants.HOST_EUROPE_AMERICA)), port);
    		s.close();
    	}catch(IOException e){
    		e.printStackTrace();
    		return false;
    	}
    	return true;
    }
    
    public InetAddress getAddressFromName(String name){
    	System.out.println(name);
    	System.out.println(names.toString());
    	
    	return hosts.get(names.indexOf(name));
    }
            
}
