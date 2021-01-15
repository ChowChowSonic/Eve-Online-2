package my.gdx.game.entities;

import java.util.ArrayList;
import java.util.TimerTask;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.ai.btree.Task.Status;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

import my.gdx.game.EveOnline2;
import my.gdx.game.entities.Entity.EntityType;
import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.InventoryItem;

public class Player extends Entity{
	public Inventory inventory;
	float invmass, basemass = 1;
	private int shields = 1000, armor = 250, hull = 500; 
	private final int maxshields = 1000, maxarmor = 250, maxhull = 500;
	public Player(Model model, EntityType type) {
		super(model, type);
		this.mass = basemass;
		this.size = 1f;
		this.setPos(6, 0, 0);
		inventory = new Inventory(100);
		inventory.additem(new InventoryItem("Shipping Crates", 50, 50));
		inventory.additem(new InventoryItem("Shipping Crate", 20, 20));
		// TODO Auto-generated constructor stub
	}
	private boolean justpressedboost = false;
	private float totalDeltaTime= 0;
	@Override
	public void update(float deltaTime) {
		//Inventory management
		invmass = inventory.getWeight();
		this.mass = basemass+invmass;
		totalDeltaTime += deltaTime;
		Vector3 camRot = new Vector3(ACCEL*deltaTime*camrotation.x/this.mass,ACCEL*deltaTime*camrotation.y/this.mass,ACCEL*deltaTime*camrotation.z/this.mass);
		Vector3 invCamRot = new Vector3(-ACCEL*deltaTime*camrotation.x,-ACCEL*deltaTime*camrotation.y,-ACCEL*deltaTime*camrotation.z);
		
		//Movement controls
		if(Gdx.input.isKeyPressed(Keys.W) && !justpressedboost) {
			this.accel.add(camRot);
		}else if(Gdx.input.isKeyPressed(Keys.S)) {
			this.accel.add(invCamRot);
		}

		//Stop the player
		if(Gdx.input.isKeyPressed(Keys.SPACE) && !justpressedboost) {
			this.vel.x -= (Math.abs(this.vel.x) > 0.06) ? this.vel.x/100: this.vel.x/10;
			this.vel.y -= (Math.abs(this.vel.y) > 0.06) ? this.vel.y/100: this.vel.y/10;
			this.vel.z -= (Math.abs(this.vel.z) > 0.06) ? this.vel.z/100: this.vel.z/10;
			//System.out.println(this.vel.toString());
			if(this.vel.len() < ACCEL/(100*this.mass)) {
				this.vel.setZero();
			}
		}
		//Starts up the warp drive
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			justpressedboost = true;
			Vector3 accelnorm = this.vel.cpy().nor();
			this.addAccel(accelnorm.x*(deltaTime/5)*(50-this.vel.len2()), 
					accelnorm.y*(deltaTime/5)*(50-this.vel.len2()), 
					accelnorm.z*(deltaTime/5)*(50-this.vel.len2()));
		}else if(justpressedboost) {
			this.vel.x/=1.05;
			this.vel.y/=1.05;
			this.vel.z/=1.05;
			this.accel.setZero();
			if(this.vel.len() <= 10*METER) {
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
}
