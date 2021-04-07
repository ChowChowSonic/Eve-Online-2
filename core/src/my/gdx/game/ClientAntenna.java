package my.gdx.game;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
public class ClientAntenna extends Thread{  
    private Socket clientSocket;
    private DataOutputStream outgoing; 
    private ObjectInputStream incoming; 
    private boolean isRunning = false;
    
    public ClientAntenna(String ip, int port) {
        try{
            clientSocket = new Socket(ip, port);
            outgoing = new DataOutputStream(clientSocket.getOutputStream());
            EveOnline2.addEntity(requestEntity(0L));
            this.isRunning = true; 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Entity requestEntity(long ID) {
        try {
            outgoing.writeLong(ID);
            outgoing.flush(); 
            return (Entity) incoming.readObject(); 
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ClassNotFoundException e2){
            e2.printStackTrace();
        }
        return null;
    }

    public void ping(Long k){
       // outgoing.writeLong(k);
       //     outgoing.flush();
    }
    
    @Override 
    public void run(){
        while(isRunning){
           // incoming.readObject();
        }
    }
}//ends class