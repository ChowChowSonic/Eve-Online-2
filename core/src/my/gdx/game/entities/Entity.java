package my.gdx.game.entities;


import java.io.Serializable;

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

public abstract class Entity implements Serializable{
	public static AssetManager manager = new AssetManager();

	protected static final long serialVersionUID = 1L;
	protected transient Vector3 pos,vel,accel;
	protected transient Model model = EveOnline2.DEFAULTMODEL;
	protected transient ModelInstance instance; 
	protected transient float mass, size;
	protected final long ID; 
	protected EntityType type; 
	protected String modelname; 
	
	
	/**Internal, non-transient variables meant to update the position of the entity after serialization */
	protected float x, dx, ddx, y, dy, ddy, z, dz, ddz; 
	/**
	* One meter in length, as defined by me
	*/
	public static final float METER = 0.00005f;
	public static enum EntityType{PLAYER,ASTEROID,FRIEND,FOE,CELESTIALOBJ, STATION}
	public Inventory inventory;
	
	
	public Entity(String modelname, EntityType type, long id){
		this.type = type;
		this.modelname = modelname; 
		pos = new Vector3();
		vel = new Vector3();
		accel = new Vector3();
		ID = id;
	}
	
	public Entity(Vector3 position, String modelname, EntityType type, long id) {
		// TODO Auto-generated constructor stub
		this.type = type;
		this.modelname = modelname; 
		pos = new Vector3(position);
		vel = new Vector3();
		accel = new Vector3();
		ID = id; 
	}
	public Entity(long ID){
		this.ID = ID;
	}
	
	public void update(float deltaTime) {
		//System.out.println("Entity.update called" + this.toString());
		this.vel = this.vel.add(accel);
		this.pos = this.pos.add(vel);
		accel = accel.setZero();
		if(this.pos != null){
			x = this.pos.x; y= this.pos.y; z = this.pos.z; 
			dx = this.vel.x; dy = this.vel.y; dz = this.vel.z; 
			ddx = this.accel.x; ddy = this.accel.y; ddz = this.accel.z; 
		}
		if(this.instance !=null){
			this.instance.transform.scl(this.size);
			Quaternion quaternion = new Quaternion();
			if(this.vel.len2()>0) {
				Matrix4 instanceRotation = this.instance.transform.cpy().mul(this.instance.transform);
				instanceRotation.setToLookAt(
				new Vector3(-(this.vel.x),-(this.vel.y),-(this.vel.z)), 
				new Vector3(0,-1,0));
				instanceRotation.rotate(0, 0, 1, 180);
				instanceRotation.getRotation(quaternion);
			}else {
				this.instance.transform.getRotation(quaternion);
			}
			this.instance.transform.set(this.pos, quaternion);
		}
	}
	
	public void serverSideUpdate(float deltaTime) {
		//System.out.println("Entity.update called" + this.toString());
		this.vel = this.vel.add(accel);
		this.pos = this.pos.add(vel);
		accel = accel.setZero();
		x = this.pos.x; y= this.pos.y; z = this.pos.z; 
		dx = this.vel.x; dy = this.vel.y; dz = this.vel.z; 
		ddx = this.accel.x; ddy = this.accel.y; ddz = this.accel.z; 
	}
	
	public boolean touches(Entity e) {
		float distance  = this.pos.dst2(e.pos);
		if(distance < (this.size*this.size)+(e.size*e.size)) {
			Vector3 forcetoapply1 = new Vector3(//standard two-body collision equation. 
			(this.vel.x*((this.getMass()-e.getMass())/(e.getMass()+this.getMass())) + e.vel.x*((2*e.getMass())/(e.getMass()+this.getMass()))),
			(this.vel.y*((this.getMass()-e.getMass())/(e.getMass()+this.getMass())) + e.vel.y*((2*e.getMass())/(e.getMass()+this.getMass()))),
			(this.vel.z*((this.getMass()-e.getMass())/(e.getMass()+this.getMass())) + e.vel.z*((2*e.getMass())/(e.getMass()+this.getMass()))));
			Vector3 forcetoapply2 = new Vector3(
			-(((2*this.getMass())/(e.getMass()+this.getMass()))*this.vel.x + e.vel.x*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))),
			-(((2*this.getMass())/(e.getMass()+this.getMass()))*this.vel.y + e.vel.y*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))),
			-(((2*this.getMass())/(e.getMass()+this.getMass()))*this.vel.z + e.vel.z*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))));
			e.setVel(forcetoapply2);
			this.setVel(forcetoapply1);
			return true;
		}
		return false;
		
	}
	
	public void buildSerializedEntity(){
		if(this.pos == null) this.pos = new Vector3(); 
		if(this.vel == null) this.vel = new Vector3();
		if(this.accel == null) this.accel = new Vector3();
		this.pos = new Vector3(x, y, z);
		this.setVel(new Vector3(dx,dy,dz));
		this.setAccel(ddx, ddy, ddz);
		if(!manager.contains(this.modelname)){
			manager.load(this.modelname, Model.class);
			manager.finishLoadingAsset(this.modelname);
		}
		this.model = manager.get(this.getModelName(), Model.class); 
		this.instance = new ModelInstance(this.model, pos); 
	}
	
	@Override
	public String toString(){
		String str = "Pos: ";//Do something here later. 
		str+= this.getPos() + " ";
		str+= "Type: " +this.getEntityType() + " ID: "+this.getID();  
		return str; 
	}
	@Override
	public boolean equals(Object e){
		Entity e2 = (Entity) e; 
		return this.ID == e2.ID; 
	}
	
	//getters, setters and adders
	
	//Model/modelinstance
	public Model getModel() {
		return this.model;
	}
	public ModelInstance getInstance() {
		return this.instance;
	}
	
	//Position
	public Vector3 getPos() { 
		return pos; 
	}
	
	public void setPos(Vector3 newpos) {
		this.pos = newpos;
	}
	
	public void setPos(float x, float y, float z) {
		this.pos = new Vector3(x,y,z);
	}
	
	//Velocity
	public Vector3 getVel() {return vel;}
	
	public void setVel(Vector3 newvel) {
		vel = newvel;
	}
	
	public void setVel(float x, float y, float z) {
		vel = new Vector3(x,y,z);
	}
	public void addVel(float x, float y, float z) {
		vel.add(x,y,z);
	}
	public void addVel(Vector3 accel) {
		vel.add(accel);
	}
	
	//Accel
	public Vector3 getAccel() {return accel;}
	
	public void setAccel(Vector3 newaccel) {
		accel = newaccel;	
	}
	
	public void setAccel(float x, float y, float z) {
		accel = new Vector3(x,y,z);
	}
	
	public void addAccel(Vector3 newaccel) {
		accel = accel.add(newaccel);	
	}
	
	public void addAccel(float x, float y, float z) {
		accel = accel.add(x,y,z);
	}
	
	//Mass
	public float getMass() {
		return mass;
	}
	
	public void setMass(float mass) {
		this.mass = mass;
	}
	
	//Entity Type
	public EntityType getEntityType() {
		return this.type; 
	}
	
	public long getID(){
		return ID;
	}
	
	//size
	public float getSize(){
		return this.size;
	}
	
	public void setSize(float f){
		this.size = f;
	}
	
	public String getModelName() {
		return modelname;
	}
	
}
