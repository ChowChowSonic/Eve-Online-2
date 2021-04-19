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
    private boolean isrunning = false;
    
    public Servant(Socket s){
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
                    case 1:
                    ID = din.readLong();
                    //System.out.println(ID+" Entity Movement requested");
                    float x = din.readFloat(), y=din.readFloat(), z=din.readFloat(); 
                    //Server.appendToLogs(x+" "+y+" "+z);
                    Server.AccelerateEntity(ID,x,y,z); 
                    break;
                    case 2:
                    Server.stopEntity(din.readLong()); 
                    break;
                    case 3:
                    ID = din.readLong();
                    x = din.readFloat(); y = din.readFloat(); z = din.readFloat(); 
                    Server.boostPlayer(ID,x,y,z); 
                    break; 
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
}//ends class