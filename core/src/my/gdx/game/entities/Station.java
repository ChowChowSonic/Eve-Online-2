package my.gdx.game.entities;

import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json.Serializable;

import my.gdx.game.DockingButton;
import my.gdx.game.EveOnline2;

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
		this.instance.transform.set(this.pos, new Quaternion());
		float size2 = (float) (Math.pow(Math.E, -Math.pow(EveOnline2.player.pos.dst(this.pos)/(this.size*this.size), 2)));
		this.instance.transform.scl(size2);
		//for(Entity bobber : bobbers) {
		//	bobber.instance.transform.scl(size2,size2,size2);
		//}
	}
	
	@Override
	public boolean touches(Entity e) {
		float distance  = this.pos.dst2(e.pos);
		if(distance < (this.tetherradius*this.tetherradius)+(e.size*e.size)) {
			if(distance < (this.size*this.size)+(e.size*e.size)) {
				Vector3 forcetoapply1 = new Vector3(
				(e.vel.x*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))),
				(e.vel.y*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))),
				(e.vel.z*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))));
				e.addAccel(forcetoapply1);
				e.setVel(forcetoapply1);
				return true; 
			}else {
				if(e instanceof Player) {
					Player ent = (Player) e;
					if(e.vel.len() > 500*METER) {
						if(!ent.isBoosting()) {
							Vector3 tmp = e.vel.cpy().nor();
							e.addVel(-tmp.x*METER, -tmp.y*METER, -tmp.z*METER);
							
						}
					}
					ent.tetheringstation = this; 
				}//instanceof player
				return false; 
			}
		}else if(e instanceof Player){
			Player ent = (Player) e;
			if(ent.tetheringstation !=null && ent.tetheringstation.equals(this)) ent.tetheringstation = null; 
		}
		return false;
	}

    public float getouterRadius() {
        return this.tetherradius;
    }
	
}
