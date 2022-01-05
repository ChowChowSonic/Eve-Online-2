package my.gdx.game.desktop;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.util.Enumeration;

import my.gdx.server.Server;
import my.gdx.server.ServerWindow;

public class ServerLauncher {
    
    public static void main(String[] arg){
        //Create new console and/or Use system console and link server to that
        //if(arg.length == 0 || !arg[0].equals("hidden")){
            try {
                ServerWindow logs = new ServerWindow(new File("core\\assets\\Logs.txt"));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                System.exit(1); 
            }
        //}
        String systemipaddress = "";
        try{
            // reads system IPAddress
            systemipaddress = getPublicIpAddress(); 
            File publicAccessFile = new File("C:/wamp64/www/MMO/yeet.php") ; 
            if(!publicAccessFile.exists()) throw new Exception(); 
            FileWriter writer = new FileWriter(publicAccessFile);
            writer.append(systemipaddress);
            writer.close();
        }
        catch (IOException e)
        {
            System.out.println("Error: Cannot access public IP address bot. Server will be forced to run on local network only.");
            systemipaddress = "Cannot Execute Properly";
        }catch(Exception E){
            System.out.println("Error: Cannot access WAMPServer directory. Server will be forced to run on local network only.\nLocal IP address will be printed on server creation; manual entry will be required in order to access it.");
        }
        
        //Run server; 
        //String ent= Gdx.files.internal("Entities.txt").path(); 
        Server server = new Server(new File("core\\assets\\Entities.txt")); 
        server.start();
    }


    /**
    * Stolen straight from StackOverflow, I have no fucking clue what's going on here
    * @return the system's Public IP Address
    */
    private static String getPublicIpAddress() {
        String res = null;
        try {
            String localhost = InetAddress.getLocalHost().getHostAddress();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) e.nextElement();
                if(ni.isLoopback())
                continue;
                if(ni.isPointToPoint())
                continue;
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress address = (InetAddress) addresses.nextElement();
                    if(address instanceof Inet4Address) {
                        String ip = address.getHostAddress();
                        if(!ip.equals(localhost))
                        System.out.println((res = ip));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
}
