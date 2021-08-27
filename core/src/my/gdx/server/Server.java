package my.gdx.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Iterator;
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
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;

import my.gdx.game.entities.CelestialObject;
import my.gdx.game.entities.Crate;
import my.gdx.game.entities.Asteroid;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
import my.gdx.game.entities.Station;
import my.gdx.game.entities.removedEntity;
import my.gdx.game.entities.Entity.EntityType;
import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.InventoryItems;
import my.gdx.game.inventory.Item;

public class Server extends ApplicationAdapter {
    public static ArrayList<Entity> entities;
    public static File ENTITYFILE, LOGFILE;

    private static final long serialVersionUID = 1L;
    private static Inventory materialcensus, usedmaterials, vanishedmaterials;
    private static SpriteBatch textrenderer;
    private static BitmapFont font;
    private static String[] logs = new String[31];
    private static ServerAntenna antenna;
    private static Random r;
    private static ArrayList<Long> openIDs = new ArrayList<Long>();
    private static long nextID = 0L;
    private static float cumDeltaTime = 0f;
    private static int logposition = 0;

    public void create() {
        r = new Random();
        materialcensus = new Inventory((float) Math.pow(3, 38));
        usedmaterials = new Inventory((float) Math.pow(3, 38));
        vanishedmaterials = new Inventory((float) Math.pow(3, 38));
        entities = new ArrayList<Entity>();
        textrenderer = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        materialcensus.additem(new Item(InventoryItems.Gold, 1000));
        try {
            LOGFILE = new File("C:\\Users\\dmcdc\\Documents\\GitHub\\Eve-Online-2\\core\\assets\\Logs.txt");
            appendToLogs(InetAddress.getLocalHost().toString());
            ENTITYFILE = new File(Gdx.files.internal("Entities.txt").path());

        } catch (Exception e) {
            e.printStackTrace();
        }

        spawnEntity(new CelestialObject(new Vector3(0, 0, 0), "Sun.obj", 500000000, 1000, assignID()), new Vector3());
        spawnEntity(new Station(new Vector3(2000, 0, 0), "SpaceStation.obj", 1000000, 20, 40, assignID()),
                new Vector3(2000, 0, 0));

        antenna = new ServerAntenna(26000);
        antenna.start();

        super.create();
    }

    /**
     * Public Void Render() in this case serves as both an updater for the ingame
     * world AND the makeshift terminal that shows its running.
     */
    public void render() {
        super.render();
        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);// clears the screen of text
        
        // Cumulative Delta Time - used for sending the entities to the clients
        cumDeltaTime += Gdx.graphics.getDeltaTime();

        // In-game Physics
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
            entities.get(i).update(Gdx.graphics.getDeltaTime());
        }

        // Logs all items in existance and tries to regulate them with the item census
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (e.inventory != null) {
                usedmaterials.additem(e.inventory.getItems());
            }
        }
        runItemCensus();

        // sends all entities to all clients if CumDeltaTime > 0.2 seconds
        if (cumDeltaTime >= 0.1) {
            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);
                antenna.sendEntity(e);
            }
            cumDeltaTime = 0;
        }

        // Logging
        textrenderer.begin();
        for (int i = 0; i < logs.length; i++) {
            if (logs[i] != null) {
                font.draw(textrenderer, logs[i], 0, Gdx.graphics.getHeight() - 15 * (i));
            }
        }
        textrenderer.end();
    }

    @Override
    public void dispose() {
        appendToLogs("------END OF SERVER LOGS UNTIL NEXT RESTART------");
        antenna.close();

    }

    /**
     * adds an entity at a random point around the perimiter of a radius
     * 
     * @param e
     */
    public static void spawnEntity(Entity e, int radius) {
        entities.add(e);
        float angle = (float) (r.nextFloat() * 2 * Math.PI);
        e.setPos(radius * (float) Math.cos(angle), 0, radius * (float) Math.sin(angle));
        sortEntities();
        appendToLogs("Entity Spawned:" + e.toString());
    }

    public static void spawnEntity(Entity e, int radius, int maxOffset) {
        entities.add(e);
        radius += r.nextInt(maxOffset);
        radius -= r.nextInt(maxOffset);
        float angle = (float) (r.nextFloat() * 2 * Math.PI);
        e.setPos(radius * (float) Math.cos(angle), 0, radius * (float) Math.sin(angle));
        sortEntities();
        appendToLogs("Entity Spawned:" + e.toString());
    }

    /**
     * Adds an entity at a fixed point in the world
     * 
     * @param e
     * @param pos
     */
    public static void spawnEntity(Entity e, Vector3 pos) {
        e.setPos(pos);
        entities.add(e);
        sortEntities();
        appendToLogs("Entity Spawned:" + e.toString());
    }

    public static void removeEntity(Entity e) {
        for (Iterator<Entity> ents = entities.iterator(); ents.hasNext();) {
            Entity e2 = ents.next();
            if (e.equals(e2)) {
                ents.remove();
            }
        }

        if (!entities.contains(e)) {
            openIDs.add(e.getID());
            antenna.sendEntity(new removedEntity(e.getID()));
            appendToLogs("Entity removed:" + e.getEntityType() + ", ID: " + e.getID());
        } else {
            appendToLogs("Entity unable to be removed!");
        }
    }

    private static void sortEntities() {
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
        appendToLogs("entity list successfully sorted");
    }// ends sortEntities()

    private void runItemCensus() {
        Inventory underflow = new Inventory(usedmaterials.getDifferences(materialcensus), 999999);
        Inventory overflow = new Inventory(materialcensus.getDifferences(usedmaterials), 999999);

        materialcensus.additem(overflow.getItems());
        vanishedmaterials.additem(underflow.getItems());

        if (vanishedmaterials.getItemcount() > 100) {
            appendToLogs("Available: \n" + materialcensus.toString() + "\nUsed: \n" + usedmaterials.toString()
                    + "\nUnaccounted for: " + vanishedmaterials.toString());
            appendToLogs("Excess:\n" + overflow.toString() + "Lacking:\n" + underflow.toString());
            spawnEntity(new Asteroid("Asteroid.obj", vanishedmaterials.getItems(), assignID()), 2000);
            vanishedmaterials.empty();
            appendToLogs("Asteroid Spawned!");
        }
    }// ends runItemCensus()

    protected static void appendToLogs(String s) {
        if (logposition < logs.length) {
            logs[logposition] = s.replaceAll("\n", " / ");
            logposition++;
        } else {
            logposition = 0;
            logs[0] = s.replaceAll("\n", " / ");
        }
        try (FileWriter logger = new FileWriter(LOGFILE, true);) {
            logger.append("[ " + LocalDateTime.now() + " ] " + s + "\n");
            logger.flush();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private static long assignID() {
        if (openIDs.size() > 0) {
            long l = openIDs.get(0);
            openIDs.remove(0);
            return l;
        }
        nextID++;
        return nextID;
    }

    protected static void DropItem(Entity e, Item i) {
        Vector3 chestpos = e.getPos().cpy();
        Vector3 rotation = e.getRotation();
        if (rotation.isZero())
            rotation = e.getPos().cpy().nor();
        float sz = e.getSize() * 2;
        chestpos.sub(rotation.x * sz, rotation.y * sz, rotation.z * sz);
        ArrayList<Item> wrapper = new ArrayList<Item>();
        if (e.inventory.containsItemType(i)) {
            wrapper.add(i);
            e.inventory.removeItem(i);
            spawnEntity(new Crate("Crate.obj", wrapper, assignID()), chestpos);
        } else {
            appendToLogs("Illegal item drop attempted!");
        }
    }

    /**
     * Returns a copy of an entity, likely a player, to send to a client.
     * 
     * @param ID - the ID of the entity in question
     * @return a copy of a player
     */
    public static Entity getEntityCopy(long ID) {
        if (ID > 0) {
            for (Entity e : entities) {
                if (e != null && e.getID() == ID)
                    return e;
            }
        }
        Player p = new Player("ship.obj", assignID());
        p.setPos(r.nextFloat(), r.nextFloat(), r.nextFloat());
        spawnEntity(p, 1500);
        return p;
    }

}