package my.gdx.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import my.gdx.game.EveOnline2;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
public class ServerAntenna extends Thread{ 
    ServerSocket socket;
    int port;
    public FileOutputStream writer;
    public static ObjectOutputStream objectwriter; 
    private static final long serialVersionUID = 1L; 
    ArrayList<Servant> connections;
    public ServerAntenna(int port){
        this.port = port; 
        connections = new ArrayList<Servant>();
    }
    
    @Override
    public void run(){
        try{
            socket = new ServerSocket(port); 
            Server.appendToLogs("Server successfully created on "+socket);
        }catch(Exception e){
            e.printStackTrace();
        }
        while(true){
            try {
                Socket user = socket.accept();
                Server.appendToLogs("User successfully connected on port "+user.getPort());
                Servant usersocket = new Servant(user);
                connections.add(usersocket);
                usersocket.run();
                
            }catch(UnknownHostException ex) {
                ex.printStackTrace();
            }
            catch(IOException e){
                e.printStackTrace();
            }//ends try/catch
        }//ends while
        
    }//ends run
    
    public void close(){
        try {
            socket.close();
            System.exit(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void sendEntity(Entity e){
        if(connections.size() == 0) return;
        for(int i =0; i < connections.size(); i++){
            try{
                connections.get(i).sendEntity(e);
            }catch(Exception e2){
                connections.remove(i);
            }
            }
        }
}//ends class

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
            userEntity = (Player) Server.getConnectedUser(din.readLong()); 
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
            din.readLong(); 
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