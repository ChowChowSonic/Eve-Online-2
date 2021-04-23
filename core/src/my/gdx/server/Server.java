package my.gdx.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;
import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;  

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;

import my.gdx.game.entities.CelestialObject;
import my.gdx.game.entities.Debris;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
import my.gdx.game.entities.Station;
import my.gdx.game.entities.removedEntity;
import my.gdx.game.entities.Entity.EntityType;
import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.InventoryItems;
import my.gdx.game.inventory.Item;

public class Server extends ApplicationAdapter{
    public static ArrayList<Entity> entities = new ArrayList<Entity>();
    public static File ENTITYFILE, LOGFILE; 
    
    private static Inventory materialcensus, usedmaterials, vanishedmaterials;	
    private static SpriteBatch textrenderer;
    private static BitmapFont font; 
    private static int logposition = 0;
    private static String[] logs = new String[31]; 
    private static long nextID = 0L;
    private static ServerAntenna antenna;
    private static final long serialVersionUID = 1L; 
    private static Random r;
    private static ArrayList<Long> openIDs = new ArrayList<Long>();
    
    
    public void create() {
        System.out.flush();
        r = new Random();
        materialcensus = new Inventory((float)Math.pow(3, 38));
        usedmaterials = new Inventory((float)Math.pow(3, 38));
        vanishedmaterials = new Inventory((float)Math.pow(3, 38));
        textrenderer = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        materialcensus.additem(new Item(InventoryItems.Gold, 1000));
        try{
            LOGFILE = new File(Gdx.files.internal("Logs.txt").path());
            appendToLogs(InetAddress.getLocalHost().toString());
            ENTITYFILE = new File(Gdx.files.internal("Entities.txt").path());
            
        }catch(Exception e){
            e.printStackTrace();
        }

        entities.add(new CelestialObject(new Vector3(0,0,0), "Sun.obj", 500000000, 300, assignID()));
        addEntity(new Station(new Vector3(), "SpaceStation.obj", 1000000, 20, 40, assignID())); 
        
        antenna = new ServerAntenna(26000);
        antenna.start();
        
        super.create();
    }
    public void render(){
        super.render();
        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);//clears the screen of text 
        //in-game physics & logging
        for(int i =0; i < entities.size(); i++){
            for(int e =0; e < entities.size(); e++) {
                if(i < e) {
                    entities.get(i).touches(entities.get(e));
                }
                /*if(entities.get(i) instanceof Player && entities.get(e).getPos().dst2(entities.get(i).getPos()) < 9000){
                    FileHandle playerdist = Gdx.files.local(String.valueOf(entities.get(i).getID()));
                }*/
            }
            entities.get(i).update(Gdx.graphics.getDeltaTime());
        }
        for(int i =0; i < entities.size(); i++) {
            Entity e = entities.get(i); 
            if(e.inventory != null) {
                usedmaterials.additem(e.inventory.getItems()); 
            }
            antenna.sendEntity(e); 
        }
        runItemCensus();
        //Logging
        textrenderer.begin();
        for(int i = 0; i < logs.length; i++){
            if(logs[i] != null){
                font.draw(textrenderer, logs[i], 0, Gdx.graphics.getHeight() - 15*(i));
            }
        }
        textrenderer.end();
    }
    @Override
    public void dispose(){
        antenna.close();
        
    }
    public static void addEntity(Entity e) {
        entities.add(e);
        int radius = 1000;
        float angle = (float) (r.nextFloat()*2*Math.PI); 
        e.setPos(radius*(float)Math.cos(angle), radius*(float)Math.sin(angle), 0f);
        sortEntities();
        appendToLogs("Entity Spawned:" + e.toString());
    }

    public static void removeEntity(Entity e){
        openIDs.add(e.getID());
        antenna.sendEntity(new removedEntity(e.getID()));
        entities.remove(e); 
        appendToLogs("Entity removed:" + e.getEntityType());
    }
    
    public static void sortEntities(){
        ArrayList<Entity> newlist = new ArrayList<Entity>(); 
        int playersinlist = 0;
        for(Entity e : entities){
            if(e.getEntityType() == EntityType.CELESTIALOBJ){
                newlist.add(0,e); 
            }else if(e.getEntityType() == EntityType.PLAYER){
                newlist.add(newlist.size(), e);
                playersinlist++;
            }else{
                if(newlist.size() > 0){
                    newlist.add(newlist.size()-(playersinlist+1), e);
                }else newlist.add(e);
            }
            entities = newlist; 
        }
        appendToLogs("entity list successfully sorted");
    }
    
    private void runItemCensus() {
        Inventory underflow = new Inventory(usedmaterials.getDifferences(materialcensus), 999999); 
        Inventory overflow = new Inventory(materialcensus.getDifferences(usedmaterials),999999);
        
        materialcensus.additem(overflow.getItems());
        vanishedmaterials.additem(underflow.getItems());
        
        if(vanishedmaterials.getItemcount() > 100) {
            appendToLogs("Available: \n"+materialcensus.toString() + "\nUsed: \n"+usedmaterials.toString()+ "\nUnaccounted for: "+vanishedmaterials.toString());
            appendToLogs("Excess:\n"+overflow.toString() +"Lacking:\n"+ underflow.toString());
            addEntity(new Debris(new Vector3(20, 20, 0), "Asteroid.obj", vanishedmaterials.getItems(),assignID()));
            vanishedmaterials.empty();
            appendToLogs("Asteroid Spawned!");
        }
    }//ends runItemCensus()
    public static void appendToLogs(String s){
        if(logposition < logs.length) {
            logs[logposition]= s.replaceAll("\n", " / ");
            logposition++; 
        }else {
            logposition = 0;
            logs[0]= s.replaceAll("\n", " / ");
        }
        try(FileWriter logger = new FileWriter(LOGFILE)){
        logger.append("["+LocalDateTime.now()+"] "+s);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    private static long assignID(){
        if(openIDs.size() > 0) {
            long l = openIDs.get(0);
            openIDs.remove(0); 
            return l; 
        }
        nextID++;
        return nextID; 
    }
    
    public static void AccelerateEntity(long id, float x, float y, float z){
        Entity e2 = null;
        for(int i = 0; i < entities.size(); i++){
            if(entities.get(i).getID() == id) e2 = entities.get(i); 
        }if(e2==null){
            appendToLogs("Failed to accelerate entity!");
            return;
        } 
        if(e2.getID() == id && e2.getEntityType().equals(EntityType.PLAYER)) {
            Player e = (Player) e2;
            e.setAccelerating(!e.getRotation().hasOppositeDirection(new Vector3(x,y,z)), x,y,z);
        }else if(e2.getEntityType() != EntityType.CELESTIALOBJ){
            e2.addAccel(x, y, z);
        }
        
    }
    
    public static void stopEntity(long ID) {
        Entity e = null;
        for(Entity ent : entities){
            if(ent.getID() == ID) e = ent; 
        } 
        if(e !=null && e.getEntityType() == EntityType.PLAYER){
            Player p = (Player) e;
            p.setAccelerating(false);
            Vector3 vel = p.getVel(); 
            
            p.setVel((vel.len2()  > 0.06*Entity.METER)? new Vector3(vel.x/1.15f, vel.y/1.15f, vel.z/1.15f): new Vector3());  
            if(vel.len() < Entity.METER/(100*p.getMass())) {
                p.setVel(0, 0, 0);
            }
        }else if(e !=null){
            Vector3 vel = e.getVel(); 
            
            e.setVel((vel.len2()  > 0.06*Entity.METER)? new Vector3(vel.x/1.15f, vel.y/1.15f, vel.z/1.15f): new Vector3());  
            if(vel.len() < Entity.METER/(100*e.getMass())) {
                e.setVel(0, 0, 0);
            }
        }        
    }
    
    public static void boostPlayer(long ID, float x, float y, float z){
        Player e = null;
        for(Entity ent : entities){
            if(ent.getID() == ID) try{
                e = (Player) ent; 
            }catch(Exception yeet) {return;} 
        } 
        e.rotate(x, y, z);
        e.setTetheringStation(null);
        e.setAccelerating(false);
        e.setBoosting(true);
        Vector3 accelnorm = e.getVel().cpy().nor();
        float deltaTime = Gdx.graphics.getDeltaTime(); 
        e.addVel((float)(accelnorm.x*(deltaTime/Math.sqrt(e.getMass()+1))*((1000-(Entity.METER*e.getMass()))-e.getVel().len2())), 
        (float)(accelnorm.y*(deltaTime/Math.sqrt(e.getMass()+1))*((1000-(Entity.METER*e.getMass()))-e.getVel().len2())), 
        (float)(accelnorm.z*(deltaTime/Math.sqrt(e.getMass()+1))*((1000-(Entity.METER*e.getMass()))-e.getVel().len2())) );
        
    }
    
    /**
    * Returns a copy of an entity, likely a player, to send to a client.
    * @param ID - the ID of the entity in question 
    * @return a copy of a player
    */
    public static Entity getEntityCopy(long ID){
        if(ID > 0){
            for(Entity e : entities){
                if(e!= null && e.getID() == ID) return e;
            }
        }
        Player p = new Player("ship.obj", EntityType.PLAYER, assignID());
        p.setPos(r.nextFloat(),r.nextFloat(),r.nextFloat());
        addEntity(p);
        return p; 
    }
    
    
}