package my.gdx.game.entities;

import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Quaternion;
import my.gdx.game.entities.Vector3; 

public class CelestialObject extends Entity{
	
	protected final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
	private static final long serialVersionUID = 1L;
	
	public CelestialObject(Vector3 position, String modelname, float mass, float radius, long ID) {
		super(modelname, EntityType.CELESTIALOBJ, ID);
		this.pos = position;
		this.setMass(mass);
		this.size = radius;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void update(float deltaTime) {
		if(this.instance !=null){
			this.instance.transform.set(this.pos, new Quaternion());
			this.instance.transform.scl(0.75f); 
			//float size2 = (float) (Math.pow(Math.E, -Math.pow(EveOnline2.player.pos.dst(this.pos) / (this.size *1), 2)));
			//this.instance.transform.scl(size2);
		}
	}
	
	/**
	* The CelectialObject.touches() method overrides the Enitty.touches() method.
	* Most celestial objects are going to be too big for players to have any real
	* effect on them. Because of this, I just decided to skip the calculations for
	* the celestial object. This bascially saves the PC some calculatory overhead.
	*/
	@Override
	public boolean touches(Entity e) {
		float distance = this.pos.dst2(e.pos);
		if (distance < ((this.size * 1.5) * (this.size * 1.5)) + (e.size * e.size)) {
			if (distance < (this.size * this.size) + (e.size * e.size)) {
				Vector3 forcetoapply1 = new Vector3(
				(e.vel.x * ((e.getMass() - this.getMass()) / (e.getMass() + this.getMass()))),
				(e.vel.y * ((e.getMass() - this.getMass()) / (e.getMass() + this.getMass()))),
				(e.vel.z * ((e.getMass() - this.getMass()) / (e.getMass() + this.getMass()))));
				e.addAccel(forcetoapply1);
				e.setVel(forcetoapply1);
				return true;
			} else {
				Vector3 difference = (Vector3) this.pos.cpy().sub(e.pos).nor();
				e.addAccel(-difference.x * METER / e.mass, -difference.y * METER / e.mass,
				-difference.z * METER / e.mass);
			}
		}
		return false;
	}
	
}
