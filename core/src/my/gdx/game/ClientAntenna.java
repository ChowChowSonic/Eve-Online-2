package my.gdx.game;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
import my.gdx.game.inventory.*;
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
            outgoing = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Outputstream successfully created");
            incoming = new ObjectInputStream(clientSocket.getInputStream()); 
            System.out.println("InputStream successfully created");
            Player p = (Player)requestEntity(0L); 
            EveOnline2.addEntity(p);
            //outgoing = (DataOutputStream) DataOutputStream.nullOutputStream();
            this.isRunning = true; 
        }catch(ConnectException e){
            System.out.println("Connenction error:");
            e.printStackTrace();
            this.close();
            System.exit(1);
        }catch(UnknownHostException e){
            System.out.println("Connenction error:");
            e.printStackTrace();
            this.close(); 
            System.exit(1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            this.close();
            System.exit(1);
        }
    }
    
    public Entity requestEntity(long ID) {
        try {
            outgoing.writeShort(0); 
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
    
    public void accelPlayer(float dirx, float diry, float dirz){
        try {
            outgoing.writeShort(1);
            outgoing.writeFloat(dirx);
            outgoing.writeFloat(diry);
            outgoing.writeFloat(dirz);
            outgoing.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void decelplayer(){
        try {
            outgoing.writeShort(2);
            outgoing.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public void boostPlayer(float x, float y, float z, boolean isBoosting) {
        try {
            outgoing.writeShort(3);
            outgoing.writeFloat(x);
            outgoing.writeFloat(y);
            outgoing.writeFloat(z);
            outgoing.writeBoolean(isBoosting);
            outgoing.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void dropInventoryItem(Item i){
        try{
        outgoing.writeShort(4);
        outgoing.writeInt(i.getTemplate().ordinal()); //I basically turn the template Enum into an integer determined by where its declared; then I send it
        //see i.getTemplate().ordinal() documentation for more info
        outgoing.writeInt(i.getStacksize());
        outgoing.flush();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void requestInventory(Entity e){
        try{
        outgoing.writeShort(5);
        outgoing.writeLong(e.getID());
        outgoing.flush();
        System.out.println(e.inventory.toString());
        e.inventory = (Inventory) incoming.readObject(); 
        System.out.println(e.inventory.toString());
        }catch (Exception exc){
            exc.printStackTrace();
        }
    }
    
    public void close(){
        try {
            this.isRunning = false;
            this.clientSocket.close();
            this.incoming.close();
            this.outgoing.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override 
    public void run(){
        while(isRunning){
            try {
                Entity o = (Entity) incoming.readObject();
                boolean alreadyfound = false; 
                /*for(int i = 0; i < EveOnline2.entities.size(); i++){
                    if(o.equals(EveOnline2.entities.get(i))){
                        EveOnline2.addEntity(o);
                        alreadyfound = true; 
                        break; 
                    }
                }
                if(!alreadyfound){*/
                    EveOnline2.addEntity(o);
                    //System.out.println("Entity added!");
                //}
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                this.isRunning = false; 
                System.exit(1);
            }catch(SocketException e){
                System.out.println("I'm going to assume that was an intentional disconnect/Server getting shut down and not a massive architectural failure");
                this.isRunning = false; 
                System.exit(0);
            }catch(Exception e){
                e.printStackTrace(); 
                this.isRunning = false; 
                System.exit(1);
            }
        }
    }
}//ends class