package my.gdx.server;

import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import my.gdx.game.entities.Entity;

/**
 * Handles the sending of data, all in one thread. Hopefully this will prevent datastreams from getting corrupted. 
 */
public class DataSender extends Thread{

    private ArrayList<Object> buffer; 
    private ArrayList<Account> destinations; 
    private ServerAntenna antenna; 

    public DataSender(ServerAntenna s){
        buffer = new ArrayList<Object>();
        destinations = new ArrayList<Account>();
        antenna = s; 
    }

    @Override
    public void run(){

        while(true){
            if(destinations.get(0) != null){
                Account dest = destinations.get(0); 
                try{
                dest.sendObject(buffer.get(0));
                buffer.remove(0);
                destinations.remove(0);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }else if(destinations.get(0) == null && buffer.get(0) instanceof Entity){
                antenna.sendEntity((Entity) buffer.get(0));
                buffer.remove(0);
                destinations.remove(0);
            }
        }

    }

    public void sendObject(Object j, Account s){
        buffer.add(0, j);
        destinations.add(0, s);
        
    }

    public void sendObjectToAll(Object j){
        buffer.add(0, j);
        destinations.add(0, null);
        
    }
    
}
