package my.gdx.game.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import my.gdx.game.entities.Vector3; 
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;

public class NPC extends Entity{
	

	public NPC(String modelname, EntityType type, long ID) {
		super(modelname, type, ID);
		this.setMass(2f);
		this.size = 1f;
		// TODO Auto-generated constructor stub
	}
	public NPC(Vector3 position, String modelname, EntityType type, long ID) {
		super(position, modelname, type, ID);
		this.setMass(2f);
		this.size = 1f;
		// TODO Auto-generated constructor stub
	}
	public NPC(Vector3 position, String modelname, float mass, EntityType type, long ID) {
		super(position, modelname, type, ID);
		this.mass = mass;
		this.size = 1f;
		// TODO Auto-generated constructor stub
	}

}
