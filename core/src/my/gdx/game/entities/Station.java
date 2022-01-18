package my.gdx.game.entities;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class Station extends CelestialObject {
	float tetherradius;
	Entity bobbers[];
	/*
	* Model bobbermodel = EveOnline2.builder.createSphere(1f, 1f, 1f, 5, 5, new
	* Material(ColorAttribute.createSpecular(1, 1, 1, 1),
	* FloatAttribute.createShininess(8f)), (long)(Usage.Position | Usage.Normal |
	* Usage.TextureCoordinates));
	*/
	
	public Station(Vector3 pos, String modelname, float mass, float innerradius, float outerraidus, long ID) {
		super(pos, modelname, mass, innerradius, ID);
		this.tetherradius = outerraidus;
		this.type = EntityType.STATION;
		// bobbers = new Entity[8];
		
		/*
		* bobbers[0]= new NPC(new Vector3(this.pos.x+tetherradius, this.pos.y,
		* this.pos.z),bobbermodel, EntityType.FRIEND); bobbers[1]= new NPC(new
		* Vector3(this.pos.x-tetherradius, this.pos.y, this.pos.z),bobbermodel,
		* EntityType.FRIEND); bobbers[2]= new NPC(new Vector3(this.pos.x, this.pos.y,
		* this.pos.z+tetherradius),bobbermodel, EntityType.FRIEND); bobbers[3]= new
		* NPC(new Vector3(this.pos.x, this.pos.y, this.pos.z-tetherradius),bobbermodel,
		* EntityType.FRIEND); bobbers[4]= new NPC(new
		* Vector3(this.pos.x+(tetherradius*0.707f), this.pos.y,
		* this.pos.z+(tetherradius*0.707f)),bobbermodel, EntityType.FRIEND);
		* bobbers[5]= new NPC(new Vector3(this.pos.x+(tetherradius*0.707f), this.pos.y,
		* this.pos.z-(tetherradius*0.707f)),bobbermodel, EntityType.FRIEND);
		* bobbers[6]= new NPC(new Vector3(this.pos.x-(tetherradius*0.707f), this.pos.y,
		* this.pos.z+(tetherradius*0.707f)),bobbermodel, EntityType.FRIEND);
		* bobbers[7]= new NPC(new Vector3(this.pos.x-(tetherradius*0.707f), this.pos.y,
		* this.pos.z-(tetherradius*0.707f)),bobbermodel, EntityType.FRIEND); for(Entity
		* bobber : bobbers) { EveOnline2.addEntity(bobber); }
		*/
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void update(float DeltaTime) {
		if (this.instance != null) {
			this.instance.transform.set(this.pos, new Quaternion());
			this.instance.transform.scl(0.5f);
			// float size2 = (float) (Math.pow(Math.E,
			// -Math.pow(EveOnline2.player.pos.dst(this.pos)/(this.size*this.size), 2)));
			// this.instance.transform.scl(size2);
		}
		// for(Entity bobber : bobbers) {
			// bobber.instance.transform.scl(size2,size2,size2);
			// }
		}
		
		@Override
		public boolean touches(Entity e) {
			float distance = this.pos.dst(e.pos);
			if (e.getEntityType() == EntityType.PLAYER) {
			Player p = (Player) e; 
			if (distance < this.tetherradius+this.size && !p.isBoosting() && p.getTetheringStationID() == 0) {
				p.tetheringstationID = this.ID;
				System.out.println("Player is now tethered");
			} else if(distance > this.tetherradius+this.size){
				if (p.tetheringstationID == this.ID)
				p.tetheringstationID = 0;
			}
			/*if (p.vel.len() > 500 * METER) {
				if (!p.isBoosting()) {
					Vector3 tmp = p.vel.cpy().nor();
					p.addVel(-tmp.x * METER, -tmp.y * METER, -tmp.z * METER);
				}
			}*/
			}
					
			return super.touches(e);
		}
		
		public float getouterRadius() {
			return this.tetherradius;
		}
		
	}
	