package my.gdx.game.entities;

import my.gdx.game.inventory.Inventory;
import my.gdx.game.entities.Vector3; 
//import my.gdx.server.Server;

public class Player extends Entity {
	float invmass, basemass = 10;
	private int shields = 1000, armor = 250, hull = 500; 
	private final int maxshields = 1000, maxarmor = 250, maxhull = 500;
	protected long tetheringstationID = 0; 
	private static final long serialVersionUID = 1L;
	private boolean justpressedboost = false;
	private float totalDeltaTime= 0;
	private boolean isAccelerating = false;
	private Vector3 direction = new Vector3(0,0,0);
	
	public Player(String modelname, EntityType type, long ID) {
		super(modelname, type, ID);
		this.mass = basemass;
		this.size = 1f;
		inventory = new Inventory(100);
		//inventory.additem(InventoryItems.Platinum, 100);
		invmass = inventory.getWeight();
		this.mass = basemass+invmass;
		// TODO Auto-generated constructor stub
	}
	
	
	@Override
	public void update(float deltaTime) {
		//Inventory management
		if(this.inventory !=null){
		invmass = inventory.getWeight();
		this.mass = basemass+invmass;
		}else this.mass = basemass;
		totalDeltaTime += deltaTime;
		
		//Movement controls
		/*if(Gdx.input.isKeyJustPressed(Keys.W) && !justpressedboost) {
			this.isAccelerating = true;
			camRot = new Vector3(basemass*METER*camrotation.x/this.mass, basemass*METER*camrotation.y/this.mass, basemass*METER*camrotation.z/this.mass);
		}else if(Gdx.input.isKeyPressed(Keys.S)) {
			this.isAccelerating = false;
			camRot = new Vector3(basemass*METER*camrotation.x/this.mass, basemass*METER*camrotation.y/this.mass, basemass*METER*camrotation.z/this.mass);
			this.accel.add(-camRot.x, -camRot.y, -camRot.z);
		}*/
		
		if(this.isAccelerating) {
			if(direction.len2() != 1.0) direction.nor();
			this.accel.set(this.basemass*METER*direction.x/this.getMass(), 
			this.basemass*METER*direction.y/this.getMass(),  
			this.basemass*METER*direction.z/this.getMass());
			//Server.appendToLogs("player at "+this.pos+" is now accelerating");
		}
		
		//Stop the player
		/*if(Gdx.input.isKeyPressed(Keys.SPACE) && !justpressedboost) {
			this.isAccelerating = false;
			this.vel.x -= (Math.abs(this.vel.x) > 0.06) ? this.vel.x/100: this.vel.x/10;
			this.vel.y -= (Math.abs(this.vel.y) > 0.06) ? this.vel.y/100: this.vel.y/10;
			this.vel.z -= (Math.abs(this.vel.z) > 0.06) ? this.vel.z/100: this.vel.z/10;
			if(this.vel.len() < METER/(100*this.mass)) {
				this.vel.setZero();
			}
		}*/
		
		//Starts up & shuts down the warp drive
		/*if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			this.tetheringstation = null;
			justpressedboost = true;
			Vector3 accelnorm = this.vel.cpy().nor();
			this.addVel((float)(accelnorm.x*(deltaTime/Math.sqrt(this.mass+1))*((1000-(METER*this.mass))-this.vel.len2())), 
			(float)(accelnorm.y*(deltaTime/Math.sqrt(this.mass+1))*((1000-(METER*this.mass))-this.vel.len2())), 
			(float)(accelnorm.z*(deltaTime/Math.sqrt(this.mass+1))*((1000-(METER*this.mass))-this.vel.len2())) );
		}else//*/ 
		if(justpressedboost) {
			this.vel.x/=1.05;
			this.vel.y/=1.05;
			this.vel.z/=1.05;
			this.accel.setZero();
			if(this.vel.len() <= 100*METER) {
				justpressedboost = false;
				this.vel.setZero();
			}
		}
		
		if(totalDeltaTime > 5.0f) {
			totalDeltaTime -= 5;
			if(this.shields < this.maxshields-20)
			this.shields+=20;
			else this.shields = maxshields;
		}
		//System.out.println("Player.update called "+this.toString());
		super.update(deltaTime);
	}

	public void replace(Entity e){
		if(e instanceof Player){
			Player p = (Player) e;
			super.replace(e);
			 //ADD IN SHIELDS/HULL/ARMOR REPLACEMENT LATER
			 this.tetheringstationID = p.tetheringstationID;
			 this.justpressedboost = p.justpressedboost;
			 this.totalDeltaTime = p.totalDeltaTime; 
			 this.isAccelerating = p.isAccelerating; 
			 this.direction = p.direction.cpy(); 

		}
	}
	
	public void dealDamage(int damage) {
		if(!justpressedboost) {
			if(damage > this.shields) {
				damage-=shields;
				shields=0;
			}else {
				shields -=damage;
				return;
			}
			if(damage > this.armor) {
				damage-=armor;
				armor=0;
			}else {
				armor -=damage;
				return;
			}
		}
		//Most people: Implements a way to die
		//Me, an intellectual:
		if(damage >= this.hull) {
			System.exit(0);
		}else {
			hull -=damage;
			return;
		}
	}
	
	public boolean isAccelerating(){
		return isAccelerating;
	}
	
	public void setAccelerating(boolean ac){
		this.isAccelerating = ac; 
	}
	public void setAccelerating(boolean ac, float x, float y, float z){
		this.direction.set(x, y, z); 
		this.isAccelerating = ac; 
	}
	
	public boolean isBoosting() {
		return justpressedboost;
	}
	
	public void setBoosting(boolean b){
		this.justpressedboost = b;
	}
	
	public int getShields() {
		return shields;
	}
	
	public void setShields(int shields) {
		this.shields = shields;
	}
	
	public int getArmor() {
		return armor;
	}
	
	public void setArmor(int armor) {
		this.armor = armor;
	}
	
	public int getHull() {
		return hull;
	}
	
	public void setHull(int hull) {
		this.hull = hull;
	}
	
	public int getMaxshields() {
		return maxshields;
	}
	
	public int getMaxarmor() {
		return maxarmor;
	}
	
	public int getMaxhull() {
		return maxhull;
	}
	
	public void setTetheringStation(Station s){
		this.tetheringstationID = s.ID; 
	}
	
	public void setTetheringStationID(long l){
		this.tetheringstationID = l; 
	}
	
	public long getTetheringStationID() {
		return this.tetheringstationID; 
	}
	
	public boolean isTethered(){
		return this.tetheringstationID != 0 && !this.isBoosting();
	}
	
	public void rotate(float x, float y, float z){
		this.direction.set(x, y, z);
	}
	
	public void rotate(Vector3 vec){
		this.direction.set(vec);
	}
	
	public Vector3 getRotation(){
		return this.direction; 
	}
	
}
