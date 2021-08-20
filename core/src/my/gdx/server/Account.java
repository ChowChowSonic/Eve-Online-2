package my.gdx.server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.badlogic.gdx.math.Vector3;

import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
import my.gdx.game.inventory.InventoryItems;
import my.gdx.game.inventory.Item;

class Account extends Thread{
    Socket user;
    DataInputStream din;
    ObjectOutputStream dout;
    private static final long serialVersionUID = 1L;
    Player userEntity;
    private boolean isrunning = false;
    
    public Account(Socket s){
        user = s; 
        try{
            this.dout = new ObjectOutputStream(s.getOutputStream());
            this.din = new DataInputStream(s.getInputStream());
            short cmd = din.readShort();
            if(cmd==0)
            userEntity = (Player) Server.getEntityCopy(din.readLong()); 
            dout.writeObject((Entity) userEntity);
            dout.flush();
            isrunning = true; 
        }catch(Exception e){
            e.printStackTrace();
            Server.appendToLogs("user forced to disconnect from port: "+user.getPort());
            Server.removeEntity(userEntity);
        }
    }
    @Override
    public void run(){
        while(isrunning){
            try{
                short cmd = din.readShort();
                System.out.println(cmd+"");
                
                switch(cmd){
                    case 0:
                    long ID = din.readLong(); 
                    //System.out.println(ID+" Entity copy requested");
                    Entity e = Server.getEntityCopy(ID); 
                    if(e !=null){
                        sendEntity(e);
                    }
                    break;

                    case 1://accelPlayer
                    //System.out.println(ID+" Entity Movement requested");
                    float x = din.readFloat(), y=din.readFloat(), z=din.readFloat(); 
                    //Server.appendToLogs(x+" "+y+" "+z);
                    Server.AccelerateEntity(this.userEntity.getID(),x,y,z); 
                    break;

                    case 2://decelPlayer
                    Server.stopEntity(din.readLong()); 
                    break;
                    
                    case 3://Boostplayer
                    x = din.readFloat(); y = din.readFloat(); z = din.readFloat(); 
                    boolean isBoosting = din.readBoolean(); 
                    Server.boostPlayer(this.userEntity.getID(),x,y,z); 
                    break; 

                    case 4://DropinventoryItem
                    InventoryItems template = InventoryItems.values()[din.readInt()]; //Deserialize the item template -- It's an Enum, so I just get the Enum.ordinal() value
                    Item i = new Item(template, din.readInt()); 
                    Server.DropItem(this.userEntity, i);
                    break; 
                }
            }catch(SocketException e){
                Server.appendToLogs("user forced to disconnect from port: "+user.getPort());
                Server.removeEntity(userEntity);
                try {
                    user.close();
                    isrunning = false;
                    this.interrupt();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }catch(Exception e){
                e.printStackTrace();
                Server.appendToLogs("user forced to disconnect from port: "+user.getPort());
                Server.removeEntity(userEntity);
                try {
                    user.close();
                    isrunning = false;
                    this.interrupt();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }//ends outer catch
        }//ends while
    }//ends run
    
    public void sendEntity(Entity e) throws Exception{
        dout.writeObject(e); 
        dout.flush();
        dout.reset();
    }
    public boolean isRunning() {
        return isrunning; 
    }
    public int getPort(){
        return user.getPort();
    }
}//ends class