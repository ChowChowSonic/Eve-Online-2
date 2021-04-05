package my.gdx.server;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

import my.gdx.game.entities.CelestialObject;
import my.gdx.game.entities.Debris;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
import my.gdx.game.entities.Entity.EntityType;
import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.InventoryItems;
import my.gdx.game.inventory.Item;

public class Server extends ApplicationAdapter{
    private static Inventory materialcensus, usedmaterials, vanishedmaterials;	
    private static ArrayList<Entity> entities = new ArrayList<Entity>();
    private static SpriteBatch textrenderer;
    private static BitmapFont font; 
    private static int logposition = 0;
    private static String[] logs = new String[31]; 
    private static final Model VOIDMODEL = new Model();
    private static long nextID = 0L;
    
    public void create() {
        System.out.flush();
        materialcensus = new Inventory((float)Math.pow(3, 38));
        usedmaterials = new Inventory((float)Math.pow(3, 38));
        vanishedmaterials = new Inventory((float)Math.pow(3, 38));
        textrenderer = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        materialcensus.additem(new Item(InventoryItems.Gold, 1000));
        ServerAntenna antenna = new ServerAntenna(26000);
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
        }
        for(Entity e : entities) {
            if(e.inventory != null) {
                usedmaterials.additem(e.inventory.getItems()); 
            }
            e.update(Gdx.graphics.getDeltaTime());
            
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
    
    public static void addEntity(Entity e) {
        if(e instanceof CelestialObject)
        entities.add(0, e);
        else if(e instanceof Player) {
            entities.add(entities.size(), e);
        }else entities.add(entities.size(), e);
        sortEntities();
        appendToLogs("Entity Spawned:" + e.getEntityType());
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
            appendToLogs("entity list successfully sorted");
        }
    }
    
    private void runItemCensus() {
        Inventory underflow = new Inventory(usedmaterials.getDifferences(materialcensus), 999999); 
        Inventory overflow = new Inventory(materialcensus.getDifferences(usedmaterials),999999);
        
        materialcensus.additem(overflow.getItems());
        vanishedmaterials.additem(underflow.getItems());
        
        if(vanishedmaterials.getItemcount() > 100) {
            appendToLogs("Available: \n"+materialcensus.toString() + "\nUsed: \n"+usedmaterials.toString()+ "\nUnaccounted for: "+vanishedmaterials.toString());
            appendToLogs("Excess:\n"+overflow.toString() +"Lacking:\n"+ underflow.toString());
            addEntity(new Debris(new Vector3(700, 5, 0), VOIDMODEL, vanishedmaterials.getItems(),15, this.assignID()));
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
        
    }
    public boolean connectPlayer(){
        return false; 
    }
    
    private long assignID(){
        nextID++;
        return nextID; 
    }
    
}
