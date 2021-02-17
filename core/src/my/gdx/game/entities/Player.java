package my.gdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.InventoryItems;

public class Player extends Entity{
	float invmass, basemass = 10;
	private int shields = 1000, armor = 250, hull = 500; 
	private final int maxshields = 1000, maxarmor = 250, maxhull = 500;
	protected Entity tetheringstation = null; 
	public Player(Model model, EntityType type) {
		super(model, type);
		this.mass = basemass;
		this.size = 1f;
		this.setPos(6, 0, 0);
		inventory = new Inventory(100);
		//inventory.additem(InventoryItems.Platinum, 100);
		invmass = inventory.getWeight();
		this.mass = basemass+invmass;
		// TODO Auto-generated constructor stub
	}
	private boolean justpressedboost = false;
	private float totalDeltaTime= 0;
	private boolean isAccelerating = false;
	private Vector3 camRot = new Vector3(0,0,0); 
	@Override
	public void update(float deltaTime) {
		//Inventory management
		invmass = inventory.getWeight();
		this.mass = basemass+invmass;
		totalDeltaTime += deltaTime;
		
		
		//Movement controls
		if(Gdx.input.isKeyJustPressed(Keys.W) && !justpressedboost) {
			this.isAccelerating = true;
			camRot = new Vector3(basemass*METER*camrotation.x/this.mass, basemass*METER*camrotation.y/this.mass, basemass*METER*camrotation.z/this.mass);
		}else if(Gdx.input.isKeyPressed(Keys.S)) {
			this.isAccelerating = false;
			camRot = new Vector3(basemass*METER*camrotation.x/this.mass, basemass*METER*camrotation.y/this.mass, basemass*METER*camrotation.z/this.mass);
			this.accel.add(-camRot.x, -camRot.y, -camRot.z);
		}
		
		if(this.isAccelerating) {
			this.accel.add(camRot);
		}

		//Stop the player
		if(Gdx.input.isKeyPressed(Keys.SPACE) && !justpressedboost) {
			this.isAccelerating = false;
			this.vel.x -= (Math.abs(this.vel.x) > 0.06) ? this.vel.x/100: this.vel.x/10;
			this.vel.y -= (Math.abs(this.vel.y) > 0.06) ? this.vel.y/100: this.vel.y/10;
			this.vel.z -= (Math.abs(this.vel.z) > 0.06) ? this.vel.z/100: this.vel.z/10;
			if(this.vel.len() < METER/(100*this.mass)) {
				this.vel.setZero();
			}
		}
		//Starts up & shuts down the warp drive
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			this.tetheringstation = null;
			justpressedboost = true;
			Vector3 accelnorm = this.vel.cpy().nor();
			this.addVel((float)(accelnorm.x*(deltaTime/Math.sqrt(this.mass+1))*((1000-(METER*this.mass))-this.vel.len2())), 
					    (float)(accelnorm.y*(deltaTime/Math.sqrt(this.mass+1))*((1000-(METER*this.mass))-this.vel.len2())), 
					    (float)(accelnorm.z*(deltaTime/Math.sqrt(this.mass+1))*((1000-(METER*this.mass))-this.vel.len2())) );//*/
		}else if(justpressedboost) {
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
		super.update(deltaTime);
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
	public boolean isBoosting() {
		return justpressedboost;
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

	public boolean isTethered(){
		return this.tetheringstation != null && !this.isBoosting();
	}
	
}
