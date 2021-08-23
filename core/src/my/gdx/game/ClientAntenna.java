package my.gdx.game;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

import com.badlogic.gdx.Gdx;

import my.gdx.game.entities.Asteroid;
import my.gdx.game.entities.CelestialObject;
import my.gdx.game.entities.Crate;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
import my.gdx.game.entities.Station;
import my.gdx.game.entities.Vector3;
import my.gdx.game.entities.removedEntity;
import my.gdx.game.entities.Entity.EntityType;
import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.Item;
public class ClientAntenna extends Thread{  
    
    private Socket clientSocket;
    private DataOutputStream outgoing; 
    private DataInputStream incoming; 
    private boolean isRunning = false;
    private static final long serialVersionUID = 1L;
    
    public ClientAntenna(String ip, int port) {
        try{
            System.out.println("Attempting to create socket...");
            clientSocket = new Socket(ip, port);
            System.out.println("Socket successfully created");
            outgoing = new DataOutputStream(clientSocket.getOutputStream());
            System.out.println("Outputstream successfully created");
            incoming = new DataInputStream(clientSocket.getInputStream()); 
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
            return decryptEntity();
        } catch(ClassNotFoundException c){
            c.printStackTrace();
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    private Entity decryptEntity() throws IOException, ClassNotFoundException{
        removedEntity tempEntity = new removedEntity(incoming.readLong()); 
        
        if(tempEntity.getID() == -1l) {
            return new removedEntity(incoming.readLong()); 
        }
        
        if(EveOnline2.entities.contains(tempEntity)){
            System.out.println("True");
            incoming.readInt(); 
            for( Entity e : EveOnline2.entities){
                if(e.equals(tempEntity)) e.Deserialize(incoming);
                return null; 
            }
        }else{
            int typeID = incoming.readInt(); 
            EntityType type = EntityType.values()[typeID]; 
            switch(type){
                case PLAYER:
                Player p = new Player("", tempEntity.getID());
                p.Deserialize(incoming);
                return p;
                
                case ASTEROID:
                Asteroid d = new Asteroid("", null, 0, tempEntity.getID());
                d.Deserialize(incoming);
                return d;
                
                case DEBRIS:
                Crate c = new Crate("", new Inventory(2500), tempEntity.getID()); 
                c.Deserialize(incoming);
                return c; 
                
                case CELESTIALOBJ:
                CelestialObject o = new CelestialObject(new Vector3(), "", 0, 0, tempEntity.getID() );
                o.Deserialize(incoming);
                return o;
                
                case STATION:
                Station o2 = new Station(new Vector3(), "", 0, 0, 0, tempEntity.getID());
                o2.Deserialize(incoming);
                return o2;
                
                default:
                System.out.println("Entity not found!"); 
                System.exit(1);
                return tempEntity; 
                
            }
             
        }
        System.out.println("What the fuck");
        return tempEntity;
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
                EveOnline2.addEntity(decryptEntity());
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