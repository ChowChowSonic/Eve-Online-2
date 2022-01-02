package my.gdx.game.desktop;

import java.io.Console;
import java.io.File;

import my.gdx.server.Server;

public class ServerLauncher {

    public static void main(String[] arg){
        //Create new console and/or Use system console and link server to that
        if(arg.length ==0 || !arg[0].equals("hidden")){
            //Override local input, output and error printing
            //System.setIn(in);
            //System.setOut(out);
            //System.setErr(err);
        }
       
        //Run server; 
        //String ent= Gdx.files.internal("Entities.txt").path(); 
        Server server = new Server(new File("core\\assets\\Entities.txt")); 
        server.start();
    }
    
}
