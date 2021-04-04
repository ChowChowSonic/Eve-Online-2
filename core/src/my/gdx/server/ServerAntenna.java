package my.gdx.server;

import java.io.*;
import java.net.*;
public class ServerAntenna extends Thread{ 
    ServerSocket server;
    int port;
    public ServerAntenna(int port){
        this.port = port; 
    }
    
    @Override
    public void run(){
        try{
        server = new ServerSocket(port); 
        Server.appendToLogs("Server successfully created on "+server);
        }catch(Exception e){
            e.printStackTrace();
        }
        while(true){
            try {
                Socket user = server.accept();
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
            server.close();
            System.exit(0);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}//ends class

class Servant extends Thread{
    Socket user;
    public Servant(Socket s){
        user = s; 
    }
    
    @Override
    public void run(){
        try{
            ObjectInputStream incoming = new ObjectInputStream(user.getInputStream());
            //incoming.readObject(); 
            System.out.println(incoming.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
    }//ends run
    
}//ends class