package my.gdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json.Serializable;

import my.gdx.game.inventory.Inventory;
//import my.gdx.server.Server;
import my.gdx.game.inventory.InventoryItems;

public class Player extends KillableEntity {
	float invmass, basemass = 10;
	private static final long serialVersionUID = 1L;
	private boolean justpressedboost = false, isBoosting = false;
	private boolean isAccelerating = false;

	protected long tetheringstationID = 0;

	public Player(String modelname, long ID) {
		super(modelname, EntityType.PLAYER, 1, ID);
		this.mass = basemass;
		inventory = new Inventory(100);
		inventory.additem(InventoryItems.Platinum, 100);
		invmass = inventory.getWeight();
		this.mass = basemass + invmass;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void update(float deltaTime) {
		// Inventory management
		if (this.instance == null) {
			invmass = inventory.getWeight();
			this.mass = basemass + invmass;
		}

		// Movement controls
		/*
		 * if(Gdx.input.isKeyJustPressed(Keys.W) && !justpressedboost) {
		 * this.isAccelerating = true; camRot = new
		 * Vector3(basemass*METER*camrotation.x/this.mass,
		 * basemass*METER*camrotation.y/this.mass,
		 * basemass*METER*camrotation.z/this.mass); }else
		 * if(Gdx.input.isKeyPressed(Keys.S)) { this.isAccelerating = false; camRot =
		 * new Vector3(basemass*METER*camrotation.x/this.mass,
		 * basemass*METER*camrotation.y/this.mass,
		 * basemass*METER*camrotation.z/this.mass); this.accel.add(-camRot.x, -camRot.y,
		 * -camRot.z); }
		 */

		if (this.isAccelerating) {
			if (direction.len2() != 1.0)
				direction.nor();
			this.accel.set(this.basemass * METER * direction.x / this.getMass(),
					this.basemass * METER * direction.y / this.getMass(),
					this.basemass * METER * direction.z / this.getMass());
			// Server.appendToLogs("player at "+this.pos+" is now accelerating");
		}

		// Stop the player
		/*
		 * if(Gdx.input.isKeyPressed(Keys.SPACE) && !justpressedboost) {
		 * this.isAccelerating = false; this.vel.x -= (Math.abs(this.vel.x) > 0.06) ?
		 * this.vel.x/100: this.vel.x/10; this.vel.y -= (Math.abs(this.vel.y) > 0.06) ?
		 * this.vel.y/100: this.vel.y/10; this.vel.z -= (Math.abs(this.vel.z) > 0.06) ?
		 * this.vel.z/100: this.vel.z/10; if(this.vel.len() < METER/(100*this.mass)) {
		 * this.vel.setZero(); } }
		 */

		// Starts up & shuts down the warp drive
		if (isBoosting) {
			this.tetheringstationID = 0;
			justpressedboost = true;
			Vector3 accelnorm = this.direction.cpy().nor();
			this.addVel(
					(float) (accelnorm.x * (deltaTime / Math.sqrt(this.mass + 1))
							* ((1000 - (METER * this.mass)) - this.vel.len2())),
					(float) (accelnorm.y * (deltaTime / Math.sqrt(this.mass + 1))
							* ((1000 - (METER * this.mass)) - this.vel.len2())),
					(float) (accelnorm.z * (deltaTime / Math.sqrt(this.mass + 1))
							* ((1000 - (METER * this.mass)) - this.vel.len2())));
		} else if (justpressedboost) {
			this.vel.x /= 1.05;
			this.vel.y /= 1.05;
			this.vel.z /= 1.05;
			this.accel.setZero();
			if (this.vel.len() <= 100 * METER) {
				justpressedboost = false;
				this.vel.setZero();
			}
		}

		
		// System.out.println("Player.update called "+this.toString());
		super.update(deltaTime);
	}

	public void updateEntityFromSerialized(Entity serializedEntity) {
		super.updateEntityFromSerialized(serializedEntity);
		if (serializedEntity.getEntityType() == this.type) {
			Player p = (Player) serializedEntity;
			this.isAccelerating = p.isAccelerating;
			this.justpressedboost = p.justpressedboost;
			this.tetheringstationID = p.tetheringstationID;
		}
	}

	public boolean isAccelerating() {
		return isAccelerating;
	}

	public void setAccelerating(boolean ac) {
		this.isAccelerating = ac;
	}

	public void setAccelerating(boolean ac, float x, float y, float z) {
		this.direction.set(x, y, z).nor();
		this.isAccelerating = ac;
	}

	public boolean isBoosting() {
		return justpressedboost || isBoosting;
	}

	public void setBoosting(boolean b) {
		this.isBoosting = b;
	}

	public void setTetheringStation(Station s) {
		this.tetheringstationID = s.ID;
	}

	public void setTetheringStationID(long l) {
		this.tetheringstationID = l;
	}

	public long getTetheringStationID() {
		return this.tetheringstationID;
	}

	/**
	 * @return this.tetheringstationID != 0 && !this.isBoosting();
	 */
	public boolean isTethered() {
		return this.tetheringstationID != 0 && !this.isBoosting();
	}

}
