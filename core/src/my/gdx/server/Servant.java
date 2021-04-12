package my.gdx.server;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.badlogic.gdx.math.Vector3;

import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;

class Servant extends Thread{
    Socket user;
    DataInputStream din;
    ObjectOutputStream dout;
    private static final long serialVersionUID = 1L;
    Player userEntity;
    
    public Servant(Socket s){
        user = s; 
        try{
            this.dout = new ObjectOutputStream(s.getOutputStream());
            this.din = new DataInputStream(s.getInputStream());
            userEntity = (Player) Server.getEntityCopy(din.readLong()); 
            dout.writeObject((Entity) userEntity);
            dout.flush();
        }catch(Exception e){
            e.printStackTrace();
            Server.appendToLogs("user forced to disconnect from port: "+user.getPort());
            Server.removeEntity(userEntity);
        }
    }
    private boolean isrunning = true;
    @Override
    public void run(){
        while(isrunning)
        try{
            Short cmd = din.readShort();
            switch(cmd){
                case 0:{
                    long ID = din.readLong(); 
                    Entity e = Server.getEntityCopy(ID); 
                    dout.reset();
                    dout.writeObject(e);
                    dout.flush();
                    break;
                }
                case 1:{
                    long ID = din.readLong();
                    float x = din.readFloat(), y=din.readFloat(), z=din.readFloat(); 
                    Player mod = (Player) Server.getModifiableEntity(ID); 
                    if(mod.getRotation().hasOppositeDirection(new Vector3(x,y,z))){
                    mod.rotate(x, y, z);
                    mod.setAccelerating(false);
                    }else{
                    mod.setAccelerating(true);
                    }
                    break;
                }
            }
        }catch(EOFException e){
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
    }//ends run
    
    public void sendEntity(Entity e) throws Exception{
        dout.reset();
        dout.writeObject((Entity) e ); 
        dout.flush();
    }
    public boolean isRunning() {
        return isrunning; 
    }
}//ends class