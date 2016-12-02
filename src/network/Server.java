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
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

import Controller.Controller;


public class Server implements Runnable{
    public static ArrayList<InetAddress> ConnectionArray = new ArrayList<InetAddress>();
    public static ArrayList<String> CurrentNodes = new ArrayList<String>();
    static int port;
    int flag=0;
    Controller parent;
    
    public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public Server(int port, Controller parent){
    	this.port = port;
    	this.parent = parent;
    }
	
	public void addNodeName(String name){
		CurrentNodes.add(name);
	}
	
	
    
    public void run(){
        try{
            ServerSocket SERVER = new ServerSocket(port);
            parent.printMessage("Server started.");
            
            while(true){
            	if(flag==1){
            		parent.printMessage("SERVER STOPPED");
            		break;
            	}
            	
            	Socket SOCK = SERVER.accept();
            	if(!ConnectionArray.contains(SOCK.getInetAddress())){
            		parent.printMessage("SOCKET added to connections");
            		ConnectionArray.add(SOCK.getInetAddress());
            		//addNode(SOCK);
            	}
                
                new ServerReturn(SOCK, parent);
            }
            parent.printMessage("SERVER STOP CHECK");
            SERVER.close();
        }catch(IOException X){
                X.printStackTrace();
        }
   }
}
