package my.gdx.server;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

import my.gdx.game.EveOnline2;
import my.gdx.game.entities.Entity;
public class ServerAntenna extends Thread{ 
    ServerSocket socket;
    int port;
    public FileOutputStream writer;
    public static ObjectOutputStream objectwriter; 
    private static final long serialVersionUID = 1L; 

    public ServerAntenna(int port){
        this.port = port; 
        try {
            writer = new FileOutputStream(Server.ENTITYFILE);
            objectwriter = new ObjectOutputStream(writer);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            System.exit(1);
        }
        
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
}//ends class

class Servant extends Thread{
    Socket user;
    DataInputStream din;
    ObjectOutputStream dout;
    private static final long serialVersionUID = 1L;
    
    public Servant(Socket s){
        user = s; 
        try{
        this.din = new DataInputStream(s.getInputStream());
        this.dout = new ObjectOutputStream(s.getOutputStream());
        dout.writeObject(Server.getConnectedUser(din.readLong()));
        dout.flush();
        }catch(Exception e){
            e.printStackTrace();
            Server.appendToLogs("user forced to disconnect from port: "+user.getPort());
        }
    }
    
    @Override
    public void run(){
        try{
            dout = ServerAntenna.objectwriter; 

        }catch(Exception e){
            e.printStackTrace();
            try {
                user.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }//ends outer catch
    }//ends run
    
}//ends class