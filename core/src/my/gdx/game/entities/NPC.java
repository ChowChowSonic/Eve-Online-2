package my.gdx.game.entities;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

public class NPC extends Entity{

	public NPC(Model model, EntityType type, long ID) {
		super(model, type, ID);
		this.setMass(2f);
		this.size = 1f;
		// TODO Auto-generated constructor stub
	}
	public NPC(Vector3 position, Model model, EntityType type, long ID) {
		super(position, model, type, ID);
		this.setMass(2f);
		this.size = 1f;
		// TODO Auto-generated constructor stub
	}
	public NPC(Vector3 position, Model model, float mass, EntityType type, long ID) {
		super(position, model, type, ID);
		this.mass = mass;
		this.size = 1f;
		// TODO Auto-generated constructor stub
	}

}
