package my.gdx.game.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;

public class NPC extends Entity{
	

	public NPC(String modelname, EntityType type, long ID) {
		super(modelname, type, 1, ID);
		this.setMass(2f);
		// TODO Auto-generated constructor stub
	}
	public NPC(Vector3 position, String modelname, EntityType type, long ID) {
		super(position, modelname, type, 1, ID);
		this.setMass(2f);
		this.size = 1f;
		// TODO Auto-generated constructor stub
	}
	public NPC(Vector3 position, String modelname, float mass, EntityType type, long ID) {
		super(position, modelname, type, 1, ID);
		this.mass = mass;
		this.size = 1f;
		// TODO Auto-generated constructor stub
	}

}
