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
    private static final long serialVersionUID = 1L;
    
    public ClientAntenna(String ip, int port) {
        try{
            System.out.println("Attempting to create socket...");
            clientSocket = new Socket(ip, port);
            System.out.println("Socket successfully created");
            incoming = new ObjectInputStream(clientSocket.getInputStream()); 
            System.out.println("InputStream successfully created");
            outgoing = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Outputstream successfully created");
            EveOnline2.addEntity( (Player)requestEntity(0L));
            
            this.isRunning = true; 
        }catch(ConnectException e){
			System.out.println("Connenction error:");
			e.printStackTrace();
			System.exit(1);
		}catch(UnknownHostException e){
            System.out.println("Connenction error:");
			e.printStackTrace();
			System.exit(1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
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
    public void close(){
        try {
            this.clientSocket.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override 
    public void run(){
        while(isRunning){
            try {
                Object o = incoming.readObject(); 
                System.out.println(o.toString());
                EveOnline2.updateEntity( (Entity) o);
            } catch (ClassNotFoundException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}//ends class