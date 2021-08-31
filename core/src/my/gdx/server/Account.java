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
    private boolean isrunning = false;

    public Account(Socket s) {
        user = s;
        try {
            this.dout = new ObjectOutputStream(s.getOutputStream());
            this.din = new DataInputStream(s.getInputStream());
            short cmd = din.readShort();
            if (cmd == 0)
                userEntity = (Player) Server.getEntityCopy(din.readLong());
            dout.writeObject((Entity) userEntity);
            dout.flush();
            isrunning = true;
        } catch (Exception e) {
            e.printStackTrace();
            Server.appendToLogs("user forced to disconnect from port: " + user.getPort());
            Server.removeEntity(userEntity);
        }
    }

    @Override
    public void run() {
        while (isrunning) {
            try {
                short cmd = din.readShort();
                System.out.println("CMD: " + cmd);

                switch (cmd) {
                    case 0:
                        long ID = din.readLong();
                        // System.out.println(ID+" Entity copy requested");
                        Entity e = Server.getEntityCopy(ID);
                        if (e != null) {
                            sendEntity(e);
                        }
                        break;

                    case 1:// accelPlayer
                           // System.out.println(ID+" Entity Movement requested");
                        float x = din.readFloat(), y = din.readFloat(), z = din.readFloat();
                        // Server.appendToLogs(x+" "+y+" "+z);
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
                        Server.DropItem(this.userEntity, i);
                        break;
                    case 5:
                        Entity from = Server.getEntityCopy(din.readLong());
                        Entity to = Server.getEntityCopy(din.readLong());
                        Item item = new Item(InventoryItems.values()[din.readInt()], din.readInt());
                        if(from.getPos().dst(userEntity.getPos()) <= 100000*(userEntity.getSize()+from.getSize())*Entity.METER && to.getPos().dst(userEntity.getPos()) <= 2500*(userEntity.getSize()+to.getSize())*Entity.METER){
                        Server.appendToLogs(
                                "Attempting to transfer item from " + from.toString() + " to " + to.toString());
                        if (from.inventory.transferInventoryItemTo(to.inventory, item)) {
                            Server.appendToLogs("Transfer successful!");
                        } else {
                            Server.appendToLogs("Transfer failed!");
                        }
                    }else {Server.appendToLogs("Transfer failed! "+from.toString()+" is too far away from"+to.toString());}
                        break;
                    case 6:
                        Entity victim = Server.getEntityCopy(din.readLong()); 
                        if(victim instanceof KillableEntity){
                        KillableEntity victim2 = (KillableEntity) victim;
                        victim2.dealDamage(20);
                        Server.appendToLogs(userEntity.toString() + " Has shot at " + victim.toString() +"!");
                        Server.SpawnARFSDefenseForce(userEntity);
                    }
                    break;
                }

            } catch (SocketException e) {
                Server.appendToLogs("user forced to disconnect from port: " + user.getPort());
                Server.removeEntity(userEntity);
                try {
                    user.close();
                    isrunning = false;
                    this.interrupt();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Server.appendToLogs("user forced to disconnect from port: " + user.getPort());
                Server.removeEntity(userEntity);
                try {
                    user.close();
                    isrunning = false;
                    this.interrupt();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            } // ends outer catch
        } // ends while

    }// ends run

    public void sendEntity(Entity e) throws Exception {
        dout.writeObject(e);
        dout.flush();
        dout.reset();
    }

    public boolean isRunning() {
        return isrunning;
    }

    public int getPort() {
        return user.getPort();
    }
}// ends class