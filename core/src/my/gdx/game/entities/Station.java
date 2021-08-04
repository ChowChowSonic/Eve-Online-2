package my.gdx.game.entities;

import com.badlogic.gdx.math.Quaternion;

import my.gdx.game.EveOnline2;
import my.gdx.game.entities.Vector3; 
public class Station extends CelestialObject{
	float tetherradius;
	Entity bobbers[];
	/*Model bobbermodel = EveOnline2.builder.createSphere(1f, 1f, 1f, 5, 5,
	new Material(ColorAttribute.createSpecular(1, 1, 1, 1),
	FloatAttribute.createShininess(8f)), (long)(Usage.Position | Usage.Normal | Usage.TextureCoordinates));*/
	
	public Station(Vector3 pos, String modelname, float mass, float innerradius, float outerraidus, long ID) {
		super(pos, modelname, mass, innerradius, ID);
		this.tetherradius = outerraidus;
		this.type = EntityType.STATION; 
		System.out.println(tetherradius + " "+ mass + " "+innerradius+ " "+outerraidus+" "+ID);
		//bobbers = new Entity[8];
		
		/*bobbers[0]= new NPC(new Vector3(this.pos.x+tetherradius, this.pos.y, this.pos.z),bobbermodel, EntityType.FRIEND);
		bobbers[1]= new NPC(new Vector3(this.pos.x-tetherradius, this.pos.y, this.pos.z),bobbermodel, EntityType.FRIEND);
		bobbers[2]= new NPC(new Vector3(this.pos.x, this.pos.y, this.pos.z+tetherradius),bobbermodel, EntityType.FRIEND);
		bobbers[3]= new NPC(new Vector3(this.pos.x, this.pos.y, this.pos.z-tetherradius),bobbermodel, EntityType.FRIEND);
		bobbers[4]= new NPC(new Vector3(this.pos.x+(tetherradius*0.707f), this.pos.y, this.pos.z+(tetherradius*0.707f)),bobbermodel, EntityType.FRIEND);
		bobbers[5]= new NPC(new Vector3(this.pos.x+(tetherradius*0.707f), this.pos.y, this.pos.z-(tetherradius*0.707f)),bobbermodel, EntityType.FRIEND);
		bobbers[6]= new NPC(new Vector3(this.pos.x-(tetherradius*0.707f), this.pos.y, this.pos.z+(tetherradius*0.707f)),bobbermodel, EntityType.FRIEND);
		bobbers[7]= new NPC(new Vector3(this.pos.x-(tetherradius*0.707f), this.pos.y, this.pos.z-(tetherradius*0.707f)),bobbermodel, EntityType.FRIEND);
		for(Entity bobber : bobbers) {
			EveOnline2.addEntity(bobber);
		}*/
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void update(float DeltaTime) {
		if(this.instance !=null){
			this.instance.transform.set(this.pos, new Quaternion());
			this.instance.transform.scl(0.5f);
			//float size2 = (float) (Math.pow(Math.E, -Math.pow(EveOnline2.player.pos.dst(this.pos)/(this.size*this.size), 2)));
			//this.instance.transform.scl(size2);
		}
		//for(Entity bobber : bobbers) {
			//	bobber.instance.transform.scl(size2,size2,size2);
			//}
		}
		
		@Override
		public boolean touches(Entity e) {
			float distance  = this.pos.dst(e.pos);
			System.out.println("Station.touches(player) called");
			//If the object is within Tethering radius...
			if(distance < this.tetherradius+e.size) {
				//If the object is physically touching the station:
				if(distance < this.size+(e.size)) {
					Vector3 forcetoapply1 = new Vector3(
					(e.vel.x*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))),
					(e.vel.y*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))),
					(e.vel.z*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))));
					e.addAccel(forcetoapply1);
					e.setVel(forcetoapply1);
					return true; 
				}
				if(e.getEntityType() == EntityType.PLAYER) {
					Player ent = (Player) e;
					if(!ent.isBoosting()) {//if the player is not boosting currently
						if(e.vel.len() > 500*METER) {//If they're going too fast, limit their speed

							Vector3 tmp = e.vel.cpy().nor();
							e.addVel(-tmp.x*METER, -tmp.y*METER, -tmp.z*METER);
						}
						System.out.println("Tethering SHOULD BE WORKING");
						//put a tether on the player
						ent.tetheringstationID = this.ID; 
					}
					
				}//instanceof player
				return false; 
				//...If not break the tether
			}else if(e.getEntityType() == EntityType.PLAYER){
				Player ent = (Player) e;
				if(ent.tetheringstationID == this.ID) ent.tetheringstationID = 0; 
				System.out.println("Tether Removed");
			}
			return false;
		}
		
		public float getouterRadius() {
			return this.tetherradius;
		}
		
	}
	