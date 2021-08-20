package my.gdx.game.entities;

import java.util.ArrayList;

import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.Item;

public class Crate extends Entity{

    public Crate(String modelname, Inventory inv,  long id) {
        super(modelname, EntityType.DEBRIS, 0.25f, id);
        this.inventory = inv; 
        //TODO Auto-generated constructor stub
    }
    public Crate(String modelname, ArrayList<Item> inv,  long id) {
        super(modelname, EntityType.DEBRIS, 0.25f, id);
        this.inventory = new Inventory(inv, 2500); 
        //TODO Auto-generated constructor stub
    }
    
}
