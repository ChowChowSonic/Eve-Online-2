package my.gdx.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import my.gdx.game.entities.Entity;
public class ServerAntenna extends Thread{ 
    ServerSocket socket;
    int port;
    private static final long serialVersionUID = 1L; 
    Server connectedWorld; //Change this to an array later. 

    public ServerAntenna(int port, Server s){
        this.port = port; 
        connectedWorld = s; 
        try{
            socket = new ServerSocket(port); 
            System.out.println("Server successfully created on "+socket);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    @Override
    public void run(){
        while(true){
            try {
                Socket user = socket.accept();
                System.out.println("User successfully connected on port "+user.getPort());
                Account usersocket = new Account(user, connectedWorld);
                connectedWorld.connectedPlayers.add(usersocket); 
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
    
}//ends class