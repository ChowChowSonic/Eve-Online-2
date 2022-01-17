package my.gdx.server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import com.badlogic.gdx.math.Vector3;

import my.gdx.game.entities.Entity;
import my.gdx.game.entities.KillableEntity;
import my.gdx.game.entities.Player;
import my.gdx.game.inventory.InventoryItems;
import my.gdx.game.inventory.Item;

class Account extends Thread {
    Socket user;
    DataInputStream din;
    ObjectOutputStream dout;
    private static final long serialVersionUID = 1L;
    Player userEntity;
    Server connectedWorld; 
    private boolean isrunning = false;
    
    public Account(Socket s, Server conn) {
        user = s;
        connectedWorld = conn; 
        try {
            this.dout = new ObjectOutputStream(s.getOutputStream());
            this.din = new DataInputStream(s.getInputStream());
            short cmd = din.readShort();
            if (cmd == 0)
            userEntity = (Player) connectedWorld.getEntityCopy(din.readLong());
            dout.writeObject((Entity) userEntity);
            dout.flush();
            isrunning = true;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("user forced to disconnect from port: " + user.getPort());
            disconnect();
        }
    }
    
    @Override
    public void run() {
        Loop: while (isrunning) {
            try {
                float x=0, y =0, z=0; 
                short cmd = din.readShort();
                //System.out.println("CMD: " + cmd);
                
                switch (cmd) {
                    case 0:
                    long ID = din.readLong();
                    // System.out.println(ID+" Entity copy requested");
                    Entity e = connectedWorld.getEntityCopy(ID);
                    if (e != null) {
                        sendEntity(e);
                    }
                    break;
                    
                    case 1:// accelPlayer
                    x = din.readFloat(); y = din.readFloat(); z = din.readFloat();
                    userEntity.setAccelerating(!userEntity.getAccel().hasOppositeDirection(new Vector3(x, y, z)), x,
                    y, z);
                    break;
                    case 2:// decelPlayer
                    if (!this.userEntity.isBoosting()) {
                        Vector3 vel = this.userEntity.getVel();
                        this.userEntity.setAccelerating(false);
                        vel.x -= (Math.abs(vel.x) > 0.06) ? vel.x / 100 : vel.x / 10;
                        vel.y -= (Math.abs(vel.y) > 0.06) ? vel.y / 100 : vel.y / 10;
                        vel.z -= (Math.abs(vel.z) > 0.06) ? vel.z / 100 : vel.z / 10;
                        this.userEntity.setVel(vel.x, vel.y, vel.z);
                        if (vel.len() < Entity.METER / (100 * this.userEntity.getMass())) {
                            vel.setZero();
                        }
                    }
                    break;
                    
                    case 3:// Boostplayer
                    x = din.readFloat();
                    y = din.readFloat();
                    z = din.readFloat();
                    boolean isBoosting = din.readBoolean();
                    //System.out.println(x + " "+ y + " " + z + " "+ isBoosting);
                    this.userEntity.rotate(x, y, z);
                    this.userEntity.setTetheringStationID(0);
                    this.userEntity.setAccelerating(false);
                    this.userEntity.setBoosting(isBoosting);
                    break;
                    
                    case 4:// DropinventoryItem
                    InventoryItems template = InventoryItems.values()[din.readInt()]; // Deserialize the item
                    // template -- It's an Enum,
                    // so I just get the
                    // Enum.ordinal() value
                    Item i = new Item(template, din.readInt());
                    connectedWorld.DropItem(this.userEntity, i);
                    break;
                    case 5:
                    Entity from = connectedWorld.getEntityCopy(din.readLong());
                    Entity to = connectedWorld.getEntityCopy(din.readLong());
                    Item item = new Item(InventoryItems.values()[din.readInt()], din.readInt());
                    if(from.getPos().dst(userEntity.getPos()) <= 100000*(userEntity.getSize()+from.getSize())*Entity.METER && to.getPos().dst(userEntity.getPos()) <= 100000*(userEntity.getSize()+to.getSize())*Entity.METER){
                        System.out.println(
                        "Attempting to transfer item from " + from.toString() + " to " + to.toString());
                        if (from.inventory.transferInventoryItemTo(to.inventory, item)) {
                            System.out.println("Transfer successful!");
                        } else {
                            System.out.println("Transfer failed!");
                        }
                    }else {System.out.println("Transfer failed! "+from.toString()+" is too far away from"+to.toString());}
                    break;
                    case 6:
                    Entity victim = connectedWorld.getEntityCopy(din.readLong()); 
                    if(victim instanceof KillableEntity){
                        KillableEntity victim2 = (KillableEntity) victim;
                        victim2.dealDamage(20);
                        System.out.println(userEntity.toString() + " Has shot at " + victim.toString() +"!");
                        connectedWorld.SpawnARFSDefenseForce(userEntity); 
                    }
                    break;
                }
                
            } catch (SocketException e) {
                System.out.println("user disconnected from port: " + user.getPort());
                disconnect();break Loop;
            } catch(EOFException disconnect){
                //This is normal, an EOFException is what happens when someone disconnects.
                System.out.println("user disconnected from port: " + user.getPort());
                disconnect();break Loop;
            }catch (Exception e) {
                e.printStackTrace();
                System.out.println("user forced to disconnect from port: " + user.getPort());
                disconnect();break Loop;
            } // ends outer catch
        } // ends while
        
    }// ends run
    
    public void sendEntity(Entity e) throws Exception {
        if(isrunning){
            dout.writeObject(e);
            dout.flush();
            dout.reset();
        }
    }
    
    public boolean isRunning() {
        return isrunning;
    }
    
    public int getPort() {
        return user.getPort();
    }
    
    public void changeWorld(Server s){
        this.connectedWorld = s; 
    }
    
    public void disconnect(){
        if(!connectedWorld.connectedPlayers.remove(this)){
            System.out.print("This account was not located in the server's player list!");
        } 
        connectedWorld.removeEntity(userEntity);
        isrunning = false; 
        
        try{
            isrunning = false;
            this.interrupt();
            user.close();
        }catch(Exception e){
            System.out.println("inner try statement errored out!");
            e.printStackTrace();
        }
    }
    
}// ends class