package my.gdx.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/**
* The ServerAntenna class is responsible for the creation of all Server objects, alongside their management.
* This class is a Singleton; meaning that there will only ever be one in existence. The currently active antenna can be 
* retrieved by running ServerAntenna.getActiveAntenna(). 
*/
public class ServerAntenna extends Thread{ 
    private static final ServerAntenna activeAntenna = new ServerAntenna(26000); 
    private static final long serialVersionUID = 1L; 
    private static ArrayList<Server> connectedWorlds; //Change this to an array later. 
    ServerSocket socket;
    int port;
    
    private ServerAntenna(int port){
        this.port = port; 
        connectedWorlds = new ArrayList<Server>(); 
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
                Account usersocket = new Account(user, connectedWorlds.get(0));
                connectedWorlds.get(0).connectedPlayers.add(usersocket); 
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
            for(Server s : connectedWorlds){
                s.close();
            }
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
    * Sends a player, originally located in their origin world, to the destination world 
    * @param traveller - The player to send
    * @param destination - The world to send them to
    */
    public void transferPlayerTo(Account traveller, Server destination){
        Server origin = traveller.connectedWorld; 
        if(origin.connectedPlayers.contains(traveller)){
            origin.connectedPlayers.remove(traveller); 
            destination.connectedPlayers.add(traveller); 
            traveller.changeWorld(destination);
        }
    }
    
    public void createWorlds(int numberOfWorlds) {
        for(int i = numberOfWorlds; i > 0; i--){
            File entFile = new File("core\\assets\\Entities"+i+".txt"); 
            if(!entFile.exists()){
                try {
                    entFile.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } 
            }
            Server s = new Server(entFile); 
            connectedWorlds.add(s);
            s.start(); 
        }
    }
    
    public static ServerAntenna getActiveAntenna(){
        return activeAntenna; 
    }
    
}//ends class