package my.gdx.server;

import java.io.File;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import com.badlogic.gdx.math.Vector3;

import my.gdx.game.entities.CelestialObject;
import my.gdx.game.entities.Crate;
import my.gdx.game.entities.ARFSDefender;
import my.gdx.game.entities.Asteroid;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.KillableEntity;
import my.gdx.game.entities.Player;
import my.gdx.game.entities.Station;
import my.gdx.game.entities.removedEntity;
import my.gdx.game.entities.Entity.EntityType;
import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.InventoryItems;
import my.gdx.game.inventory.Item;
import my.gdx.game.inventory.Shipclass;

public class Server extends Thread{
    public File ENTITYFILE; 
    public ArrayList<Entity> entities;
    public final ArrayList<Account> connectedPlayers; 
    
    private static ServerAntenna antenna = ServerAntenna.getActiveAntenna();
    private long cumDeltaTime = 0, last;
    private Entity[] activeDefenders = new Entity[10];
    private static final long serialVersionUID = 1L;
    private static final Inventory  materialcensus = new Inventory((float) Math.pow(3, 38)),
    usedmaterials = new Inventory((float) Math.pow(3, 38)),
    vanishedmaterials = new Inventory((float) Math.pow(3, 38));
    private static Random r;
    private static ArrayList<Long> openIDs = new ArrayList<Long>();
    private static long nextID = 0L;
    private static boolean isCensus = false; 
    
    
    public Server(File entity) {
        System.out.println("------SERVER STARTUP INITIATED------");
        last = System.currentTimeMillis();
        r = new Random();
        entities = new ArrayList<Entity>();
        connectedPlayers = new ArrayList<Account>();
        
        materialcensus.additem(new Item(InventoryItems.Gold, 1000));
        try {
            ENTITYFILE = entity; 
            System.out.println(InetAddress.getLocalHost().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        spawnEntity(new CelestialObject(new Vector3(0, 0, 0), "Sun.obj", 500000000, 1000, assignID()), new Vector3());
        spawnEntity(new Station(new Vector3(2000, 0, 0), "SpaceStation.obj", 1000000, 20, 40, assignID()),
        new Vector3(2000, 0, 0));
        
        //super.create();
    }
    
    @Override
    /**
    * Public Void run() in this case serves as both an updater for the ingame
    * world. DO NOT CALL THIS TOO MANY TIMES PER SECOND OR YOU WILL GET BUGGY GAMEPLAY! 
    */
    public void run() {
        while(true){
            
            // Cumulative Delta Time - used for sending the entities to the clients
            last = System.currentTimeMillis(); 
            
            // In-game Physics
            if(cumDeltaTime >= 150){
                for (int i = 0; i < entities.size(); i++) {
                    for (int e = i; e < entities.size(); e++) {
                        if (i < e) {
                            entities.get(i).touches(entities.get(e));
                        }
                        /*
                        * if(entities.get(i) instanceof Player &&
                        * entities.get(e).getPos().dst2(entities.get(i).getPos()) < 9000){ FileHandle
                            * playerdist = Gdx.files.local(String.valueOf(entities.get(i).getID())); }
                            */
                        }
                        entities.get(i).update(0);
                    }
                }
                // Logs all items in existance and tries to regulate them with the item census
                if(!isCensus){ 
                    isCensus = true; 
                    for (int i = 0; i < entities.size(); i++) {
                        Entity e = entities.get(i);
                        if (e.inventory != null) {
                            usedmaterials.additem(e.inventory.getItems());
                        }
                    }
                    runItemCensus();
                    isCensus = false; 
                }
                cumDeltaTime+= (System.currentTimeMillis()-last); 
                // sends all entities to all clients if CumDeltaTime > 0.2 seconds
                if (cumDeltaTime >= 200) {
                    for (int i = 0; i < entities.size(); i++) {
                        Entity e = entities.get(i);
                        for(Account player : connectedPlayers){
                            try {
                                player.sendEntity(e);
                            } catch (Exception e1) {
                                // TODO Auto-generated catch block
                                e1.printStackTrace();
                            }
                        }
                    }
                    //System.out.println("Entities sent!");
                    cumDeltaTime = 0;
                }
                
            }
        }
        
        public void close() {
            System.out.println("------END OF SERVER LOGS UNTIL NEXT RESTART------");
            antenna.close();
            
        }
        
        /**
        * adds an entity at a random point around the perimiter of a radius
        * 
        * @param e
        */
        public void spawnEntity(Entity e, int radius) {
            entities.add(e);
            float angle = (float) (r.nextFloat() * 2 * Math.PI);
            e.setPos(radius * (float) Math.cos(angle), 0, radius * (float) Math.sin(angle));
            sortEntities();
            System.out.println("Entity Spawned:" + e.toString());
        }
        
        public void spawnEntity(Entity e, int radius, int maxOffset) {
            entities.add(e);
            radius += r.nextInt(maxOffset);
            radius -= r.nextInt(maxOffset);
            float angle = (float) (r.nextFloat() * 2 * Math.PI);
            e.setPos(radius * (float) Math.cos(angle), 0, radius * (float) Math.sin(angle));
            sortEntities();
            System.out.println("Entity Spawned:" + e.toString());
        }
        
        /**
        * Adds an entity at a fixed point in the world
        * 
        * @param e
        * @param pos
        */
        public void spawnEntity(Entity e, Vector3 pos) {
            e.setPos(pos);
            entities.add(e);
            sortEntities();
            System.out.println("Entity Spawned:" + e.toString());
        }
        
        public void removeEntity(Entity e) {
            for (Iterator<Entity> ents = entities.iterator(); ents.hasNext();) {
                Entity e2 = ents.next();
                if (e.equals(e2)) {
                    ents.remove();
                }
            }
            
            //if the target is dead, remove it from the ARFS defender's hit list
            for (Entity def : activeDefenders) {
                if (def == null)
                continue;
                ARFSDefender d = (ARFSDefender) def;
                if (d.getTarget().equals(e))
                d.setTarget(null);
            }
            
            if (!entities.contains(e)) {
                openIDs.add(e.getID());
                for(Account player : connectedPlayers){
                    try {
                        player.sendEntity(new removedEntity(e.getID()));
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
                System.out.println("Entity removed:" + e.getEntityType() + ", ID: " + e.getID());
            } else {
                System.out.println("Entity unable to be removed!");
            }
        }
        
        private void sortEntities() {
            ArrayList<Entity> newlist = new ArrayList<Entity>();
            for (Entity e : entities) {
                if (e.getEntityType() == EntityType.CELESTIALOBJ || e.getEntityType() == EntityType.STATION)
                newlist.add(e);
            }
            
            for (Entity e : entities) {
                if (!newlist.contains(e) && e.getEntityType() != EntityType.PLAYER) {
                    newlist.add(e);
                }
            }
            
            for (Entity e : entities) {
                if (e.getEntityType() == EntityType.PLAYER)
                newlist.add(e);
            }
            
            entities = newlist;
            System.out.println("entity list successfully sorted");
        }// ends sortEntities()
        
        private void runItemCensus() {
            Inventory underflow = new Inventory(usedmaterials.getDifferences(materialcensus), 999999);
            Inventory overflow = new Inventory(materialcensus.getDifferences(usedmaterials), 999999);
            
            materialcensus.additem(overflow.getItems());
            vanishedmaterials.additem(underflow.getItems());
            
            if (vanishedmaterials.getItemcount() > 100) {
                System.out.println("WARNING: Item census has uncovered an abundance of items not present in the public economy."+
                "\nServer will automatically regenerate these items by spawning in a new asteroid. \nThe item census is as follows:\n"+
                "All available materials (used or raw): \n" + materialcensus.toString() + "\nUsed materials: \n" + usedmaterials.toString()
                + "\nUnaccounted for: \n" + vanishedmaterials.toString()+"\nExcess materials (Possibly duplicated in some way):\n" + overflow.toString() + "Lacking:\n" + underflow.toString());
                spawnEntity(new Asteroid("Asteroid.obj", vanishedmaterials.getItems(), assignID()), 2000);
                vanishedmaterials.empty();
                System.out.println("Asteroid Spawned!");
            }
        }// ends runItemCensus()
        
        protected void DropItem(Entity e, Item i) {
            Vector3 chestpos = e.getPos().cpy();
            Vector3 rotation = e.getRotation();
            if (rotation.isZero())
            rotation = e.getPos().cpy().nor();
            float sz = e.getSize() * 2;
            chestpos.sub(rotation.x * sz, rotation.y * sz, rotation.z * sz);
            ArrayList<Item> wrapper = new ArrayList<Item>();
            if (e.inventory.containsWithQuantity(i)) {
                wrapper.add(i);
                e.inventory.removeItem(i);
                spawnEntity(new Crate("Crate.obj", wrapper, assignID()), chestpos);
            } else {
                System.out.println("Illegal item drop attempted! " + i.toString() + " not contained within the inventory!");
                System.out.println(e.inventory.toString());
            }
        }
        
        /**
        * Returns a copy of an entity, likely a player, to send to a client.
        * 
        * @param ID - the ID of the entity in question
        * @return a copy of a player
        */
        public Entity getEntityCopy(long ID) {
            if (ID > 0) {
                for (Entity e : entities) {
                    if (e != null && e.getID() == ID)
                    return e;
                }
            }
            Player p = new Player(Shipclass.Apollyon, assignID());
            p.setPos(r.nextFloat(), r.nextFloat(), r.nextFloat());
            spawnEntity(p, 1500);
            return p;
        }
        
        public void SpawnARFSDefenseForce(KillableEntity criminal) {
            // Get a station to spawn them at
            Station spawnpoint = null;
            for (int i = 0; i < entities.size(); i++) {
                if (entities.get(i).getEntityType() == EntityType.STATION) {
                    spawnpoint = (Station) entities.get(i);
                    break;
                }
            }
            
            if (spawnpoint == null)
            return;
            for (int i = 0; i < activeDefenders.length; i++) {
                Entity e = activeDefenders[i];
                if (e == null) {
                    ARFSDefender police = new ARFSDefender(Shipclass.ARFSBattleship, criminal, assignID());
                    activeDefenders[i] = police;
                    Vector3 position = spawnpoint.getPos();
                    double angle = r.nextFloat() * Math.PI * 2f;
                    Vector3 position2 = new Vector3(position.x - (float) (spawnpoint.getouterRadius() * Math.sin(angle)),
                    position.y, position.z - (float) (spawnpoint.getouterRadius() * Math.cos(angle)));
                    spawnEntity(police, position2);
                } else {
                    ARFSDefender e2 = (ARFSDefender) e;
                    e2.setTarget(criminal);
                }
            }
        }
        
        //Start of Static methods
        private static long assignID() {
            if (openIDs.size() > 0) {
                long l = openIDs.get(0);
                openIDs.remove(0);
                return l;
            }
            nextID++;
            return nextID;
        }
    }