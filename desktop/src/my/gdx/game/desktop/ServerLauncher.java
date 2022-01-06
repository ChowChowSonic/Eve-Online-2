package my.gdx.game.desktop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import my.gdx.server.ServerAntenna;
import my.gdx.server.ServerWindow;

public class ServerLauncher {
    
    public static void main(String[] arg){
        ServerAntenna antenna = ServerAntenna.getActiveAntenna(); 
        //Create new console and/or Use system console and link server to that
        if(arg.length == 0 || !arg[0].equals("hidden")){
            try {
                File logfile = new File("core\\assets\\Logs.txt"); 
                if(!logfile.exists()) throw new FileNotFoundException(); 
                ServerWindow logs = new ServerWindow(logfile);
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

        //Get system IP Address
        String systemipaddress = "";
        try{
            // reads system IPAddress
            systemipaddress = getPublicIpAddress(); 
            File publicAccessFile = new File("C:/wamp64/www/MMO/yeet.php") ; 
            if(!publicAccessFile.exists()) throw new Exception(); 
            System.out.println("Pre-Server public IP Retrieval: "+systemipaddress);
        }catch(Exception E){
            System.out.println("Error: Cannot access WAMPServer directory. Server will be forced to run on local network only.\n\tLocal IP address will be printed upon server creation; manual entry will be required in order to access it.");
        }

        //Run server; 
        antenna.createWorlds(1);
        
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
                        if(!ip.equals(localhost)) res = ip;
                        //System.out.println((res = ip));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
}
