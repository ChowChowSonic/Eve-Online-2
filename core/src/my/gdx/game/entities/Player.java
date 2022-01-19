package my.gdx.game.entities;

import com.badlogic.gdx.math.Vector3;

import my.gdx.game.inventory.DefensiveGear;
import my.gdx.game.inventory.Inventory;
//import my.gdx.server.Server;
import my.gdx.game.inventory.InventoryItems;
import my.gdx.game.inventory.OffensiveGear;
import my.gdx.game.inventory.Shipclass;

public class Player extends KillableEntity {
	float invmass, basemass;
	private static final long serialVersionUID = 1L;
	private boolean justpressedboost = false, isBoosting = false;
	private boolean isAccelerating = false;
	private Shipclass ship; 
	protected long tetheringstationID = 0;

	public Player(Shipclass type, long ID, OffensiveGear[] guns, DefensiveGear[] shields) {
		super(type, EntityType.PLAYER, ID, shields, guns);
		this.ship = type; 
		this.basemass = type.toItemStack().getWeight();
		inventory = new Inventory(type.getInventorySize());
		inventory.additem(InventoryItems.Platinum, type.getInventorySize());
		System.out.println(inventory.toString());
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

		if (this.isAccelerating) {
			int str = ship.getThrusterStrength(); 
			if (direction.len2() != 1.0)
				direction.nor();
			this.accel.set( str * METER * direction.x / this.getMass(),
							str * METER * direction.y / this.getMass(),
							str * METER * direction.z / this.getMass());
			// Server.appendToLogs("player at "+this.pos+" is now accelerating");
		}

		// Starts up & shuts down the warp drive
		if (isBoosting) {
			this.tetheringstationID = 0;
			justpressedboost = true;
			if(this.vel.len2() <= 40000*KILOMETER){
			Vector3 accelnorm = this.direction.cpy();
			this.addAccel(accelnorm.x*131*METER, accelnorm.y*131*METER, accelnorm.z*131*METER);
		}
		}else if (!isBoosting && justpressedboost) {
			this.vel.x /= 1.05;
			this.vel.y /= 1.05;
			this.vel.z /= 1.05;
			this.accel.setZero();
			if (this.vel.len() <= 100 * METER) {
				justpressedboost = false;
				this.vel.setZero();
				super.update(deltaTime);
				return; 
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

	public Shipclass getShipclass(){
		return ship;
	}

	/**
	 * @return this.tetheringstationID != 0 && !this.isBoosting();
	 */
	public boolean isTethered() {
		return this.tetheringstationID != 0 && !this.isBoosting();
	}

}
