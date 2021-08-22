package my.gdx.game.entities;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;

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

    public void render(){
        if (this.instance == null) {
			this.model = manager.get(this.modelname, Model.class);
			this.instance = new ModelInstance(this.model, pos);
		}
		if (this.instance != null) {
			
			Quaternion quaternion = new Quaternion();
			if (this.vel.len2() > 0) {
				Matrix4 instanceRotation = this.instance.transform.cpy().mul(this.instance.transform.cpy());
				instanceRotation.setToLookAt(new Vector3(-(this.vel.x + this.direction.x),
						-(this.vel.y + this.direction.y), -(this.vel.z + this.direction.z)), new Vector3(0, -1, 0));
				instanceRotation.rotate(0, 0, 1, 180);
				instanceRotation.getRotation(quaternion);
			} else {
				this.instance.transform.getRotation(quaternion);
			}
			this.instance.transform.set(this.pos, quaternion);
            this.instance.transform.scl(this.size/1000, this.size/1000, this.size/1000);
		}
    }
    
}
