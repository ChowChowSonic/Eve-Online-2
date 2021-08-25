package my.gdx.game.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.Item;

public class Crate extends Entity{

    public Crate(String modelname, Inventory inv,  long id) {
        super(modelname, EntityType.DEBRIS, 0.25f, id);
        this.inventory = inv; 
        this.mass = inventory.getWeight()+1;
        //TODO Auto-generated constructor stub
    }
    public Crate(String modelname, ArrayList<Item> inv,  long id) {
        super(modelname, EntityType.DEBRIS, 0.25f, id);
        this.inventory = new Inventory(inv, 2500); 
        this.mass = inventory.getWeight()+1;
        //TODO Auto-generated constructor stub
    }

}
