package my.gdx.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import my.gdx.game.entities.Entity;
public class ServerAntenna extends Thread{ 
    ServerSocket socket;
    int port;
    private static final long serialVersionUID = 1L; 
    ArrayList<Account> connections;

    public ServerAntenna(int port){
        this.port = port; 
        connections = new ArrayList<Account>();
        try{
            socket = new ServerSocket(port); 
            Server.appendToLogs("Server successfully created on "+socket);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public void run(){
        while(true){
            try {
                Socket user = socket.accept();
                Server.appendToLogs("User successfully connected on port "+user.getPort());
                Account usersocket = new Account(user);
                connections.add(usersocket);
                usersocket.start();
                
            }catch(UnknownHostException ex) {
                ex.printStackTrace();
            }catch(SocketException sock){
                
            }catch(IOException e){
                e.printStackTrace();
            }
        }//ends while
        
    }//ends run
    
    public void close(){
        try {
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void sendEntity(Entity e){
        if(connections.size() == 0) return;
        for(int i =0; i < connections.size(); i++){
            try{
                connections.get(i).sendEntity(e);
            }catch(Exception e2){
                if(connections.size() > 0)
                connections.remove(i);
                else connections.clear();
            }
        }
    }
}//ends class