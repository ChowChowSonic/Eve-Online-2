package my.gdx.server;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;

import my.gdx.game.entities.Entity;
import my.gdx.game.inventory.Inventory;

public class Server extends ApplicationAdapter{
    public static Inventory materialcensus, usedmaterials, vanishedmaterials;	
    private static ArrayList<Entity> entities = new ArrayList<Entity>();
    public void create() {
        materialcensus = new Inventory((float)Math.pow(3, 38));
        usedmaterials = new Inventory((float)Math.pow(3, 38));
        vanishedmaterials = new Inventory((float)Math.pow(3, 38));
        super.create();
    }

    public void render(){
        super.render();
        for(int i =0; i < entities.size(); i++)
		for(int e =0; e < entities.size(); e++) {
			if(i < e) {
				entities.get(i).touches(entities.get(e));
			}
		}
        for(Entity e : entities) {
            if(e.inventory != null) {
				usedmaterials.additem(e.inventory.getItems()); 
				//System.out.println(e.getEntityType()+":\n"+e.inventory.toString());
			}
			e.update(Gdx.graphics.getDeltaTime());
        }
    }

    public boolean connectPlayer(){
        return false; 
    }

}
