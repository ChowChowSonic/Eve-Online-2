package my.gdx.game.entities;

import java.io.Serializable;
import java.util.ArrayList;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import my.gdx.game.EveOnline2;
import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.Item;

public abstract class Entity implements Serializable {
	public static AssetManager manager = new AssetManager();

	protected static final long serialVersionUID = 1L;
	protected transient Vector3 pos, vel, accel;
	protected transient Model model = EveOnline2.DEFAULTMODEL;
	protected transient ModelInstance instance;
	protected final long ID;
	protected EntityType type;
	protected String modelname;
	protected float mass, size;
	/**
	 * NOT THE PLAYER'S ACTUAL DIRECTION. THIS IS FOR BOOSTING/ACCELERATING PURPOSES
	 * ONLY.
	 */
	protected Vector3 direction = new Vector3(0, 0, 0);

	/**
	 * Internal, non-transient variables meant to update the position of the entity
	 * after serialization
	 */
	protected float x, dx, ddx, y, dy, ddy, z, dz, ddz;
	/**
	 * One meter in length, as defined by me
	 */
	public static final float METER = 0.005f;

	public static enum EntityType {
		PLAYER, ASTEROID, DEBRIS, FRIEND, FOE, CELESTIALOBJ, STATION, ENFORCER
	}

	public Inventory inventory;

	public Entity(String modelname, EntityType type, float size, long id) {
		this.type = type;
		this.size = size;
		this.modelname = modelname;
		pos = new Vector3();
		vel = new Vector3();
		accel = new Vector3();
		ID = id;
	}

	public Entity(Vector3 position, String modelname, EntityType type, float size, long id) {
		// TODO Auto-generated constructor stub
		this.type = type;
		this.size = size;
		this.modelname = modelname;
		pos = new Vector3(position);
		vel = new Vector3();
		accel = new Vector3();
		ID = id;
	}

	public Entity(long ID) {
		this.ID = ID;
	}

	public void update(float deltaTime) {
		this.vel = this.vel.add(accel);
		this.pos = this.pos.add(vel);
		if (!this.vel.isZero()) {
			this.direction.add(this.vel);
			this.direction.nor();
		}
		if (this.instance == null)
			accel = accel.setZero();
		if (this.pos != null) {
			x = this.pos.x;
			y = this.pos.y;
			z = this.pos.z;
			dx = this.vel.x;
			dy = this.vel.y;
			dz = this.vel.z;
			ddx = this.accel.x;
			ddy = this.accel.y;
			ddz = this.accel.z;
		}

	}

	public void render() {
		if (this.instance == null) {
			this.model = manager.get(this.modelname, Model.class);
			this.instance = new ModelInstance(this.model, pos);
			for (int i = 0; i < instance.nodes.size; i++) {
				instance.nodes.get(i).scale.set(new Vector3(size, size, size));
			}
			instance.calculateTransforms();
		}
		if (this.instance != null) {
			Quaternion quaternion = new Quaternion();
			if (this.vel.len2() > 0) {
				Matrix4 instanceRotation = this.instance.transform.cpy().mul(this.instance.transform.cpy());
				instanceRotation.setToLookAt(new Vector3(-(this.direction.x),
						-(this.direction.y), -(this.direction.z)), new Vector3(0, -1, 0));
				instanceRotation.rotate(0, 0, 1, 180);
				instanceRotation.getRotation(quaternion);
			} else {
				this.instance.transform.getRotation(quaternion);
			}
			this.instance.transform.set(this.pos, quaternion);
		}
	}

	public boolean touches(Entity e) {
		float distance = this.pos.dst2(e.pos);
		if (distance < (this.size * this.size) + (e.size * e.size)) {
			Vector3 forcetoapply1 = new Vector3(// standard two-body collision equation.
					(this.vel.x * ((this.getMass() - e.getMass()) / (e.getMass() + this.getMass()))
							+ e.vel.x * ((2 * e.getMass()) / (e.getMass() + this.getMass()))),
					(this.vel.y * ((this.getMass() - e.getMass()) / (e.getMass() + this.getMass()))
							+ e.vel.y * ((2 * e.getMass()) / (e.getMass() + this.getMass()))),
					(this.vel.z * ((this.getMass() - e.getMass()) / (e.getMass() + this.getMass()))
							+ e.vel.z * ((2 * e.getMass()) / (e.getMass() + this.getMass()))));
			Vector3 forcetoapply2 = new Vector3(
					-(((2 * this.getMass()) / (e.getMass() + this.getMass())) * this.vel.x
							+ e.vel.x * ((e.getMass() - this.getMass()) / (e.getMass() + this.getMass()))),
					-(((2 * this.getMass()) / (e.getMass() + this.getMass())) * this.vel.y
							+ e.vel.y * ((e.getMass() - this.getMass()) / (e.getMass() + this.getMass()))),
					-(((2 * this.getMass()) / (e.getMass() + this.getMass())) * this.vel.z
							+ e.vel.z * ((e.getMass() - this.getMass()) / (e.getMass() + this.getMass()))));
			e.setVel(forcetoapply2);
			this.setVel(forcetoapply1);
			return true;
		}
		return false;

	}

	/**
	 * Takes two equivalent entities: one outdated entity, and an "updated" yet
	 * unbuilt one from the server. It builds the updated one, and sets all outdated
	 * info on the old one to the more recent info. Namely, the position, velocity
	 * and accel. All entities MUST have their own copy of
	 * UpdateEntityFromSerialIzed in order to update properly; if not, some
	 * variables WILL NOT BE UPDATED CLIENTSIDE
	 * 
	 * @param serializedEntity The entity to update from
	 */
	public void updateEntityFromSerialized(Entity serializedEntity) {
		this.setPos(serializedEntity.x, serializedEntity.y, serializedEntity.z);
		this.setVel(serializedEntity.dx, serializedEntity.dy, serializedEntity.dz);
		this.setAccel(serializedEntity.ddx, serializedEntity.ddy, serializedEntity.ddz);
		this.inventory = serializedEntity.inventory; 
		// this.modelname = serializedEntity.modelname;
		// if(this.mass != serializedEntity.mass) System.out.println(this.mass + "
		// (Mass) "+ serializedEntity.mass);
		 this.mass = serializedEntity.mass;
		// if(this.size != serializedEntity.size) System.out.println(this.size + "
		// (Size) "+ serializedEntity.size);
		 this.size = serializedEntity.size;
	}

	@Override
	public String toString() {
		String str = "Pos: ";// Do something here later.
		str += this.getPos() + " ";
		str += "Type: " + this.getEntityType() + " ID: " + this.getID();
		return str;
	}

	@Override
	public boolean equals(Object e) {
		Entity e2 = (Entity) e;
		return this.ID == e2.ID;
	}

	// getters, setters and adders

	// Model/modelinstance
	public Model getModel() {
		return this.model;
	}

	public ModelInstance getInstance() {
		return this.instance;
	}

	// Position
	public Vector3 getPos() {
		return pos;
	}

	public void setPos(Vector3 newpos) {
		this.pos = newpos;
	}

	public void setPos(float x, float y, float z) {
		this.pos = new Vector3(x, y, z);
	}

	// Velocity
	public Vector3 getVel() {
		return vel;
	}

	public void setVel(Vector3 newvel) {
		vel = newvel;
	}

	public void setVel(float x, float y, float z) {
		vel = new Vector3(x, y, z);
	}

	public void addVel(float x, float y, float z) {
		vel.add(x, y, z);
	}

	public void addVel(Vector3 accel) {
		vel.add(accel);
	}

	// Accel
	public Vector3 getAccel() {
		return accel;
	}

	public void setAccel(Vector3 newaccel) {
		accel = newaccel;
	}

	public void setAccel(float x, float y, float z) {
		accel = new Vector3(x, y, z);
	}

	public void addAccel(Vector3 newaccel) {
		accel = accel.add(newaccel);
	}

	public void addAccel(float x, float y, float z) {
		accel = accel.add(x, y, z);
	}

	// Mass
	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	// Entity Type
	public EntityType getEntityType() {
		return this.type;
	}

	public long getID() {
		return ID;
	}

	// size
	public float getSize() {
		return this.size;
	}

	public void setSize(float f) {
		this.size = f;
	}

	public String getModelName() {
		return modelname;
	}

	// rotation
	public void rotate(float x, float y, float z) {
		this.direction.set(x, y, z);
	}

	/**
	 * Rotates this model and/or entity to look at a certain direction, usually
	 * determined by the velocity
	 * 
	 * @param vec
	 */
	public void rotate(Vector3 vec) {
		this.direction.set(vec);
	}

	public Vector3 getRotation() {
		return this.direction;
	}

}
