package my.gdx.game.entities;

import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import my.gdx.game.EveOnline2;

public class CelestialObject extends Entity{
	
	protected final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
	private static final long serialVersionUID = 1L;
	
	public CelestialObject(Vector3 position, String modelname, float mass, float radius, long ID) {
		super(modelname, EntityType.CELESTIALOBJ, radius, ID);
		this.pos = position;
		this.setMass(mass);
		x = this.pos.x; y = this.pos.y; z = this.pos.z; 
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
	@Override
	public void render(){
		if (this.instance == null) {
			this.model = manager.get(this.modelname, Model.class);
			this.instance = new ModelInstance(this.model, pos);
			for (int i = 0; i < instance.nodes.size; i++) {
				instance.nodes.get(i).scale.set(new Vector3(size/2.4f, size/2.4f, size/2.4f));
			}
			instance.calculateTransforms();
		}
		if (this.instance != null) {
			Quaternion quaternion = new Quaternion();
			if (this.vel.len2() > 0) {
				Matrix4 instanceRotation = this.instance.transform.cpy().mul(this.instance.transform.cpy());
				instanceRotation.setToLookAt(new Vector3(-(this.direction.x),
						-(this.direction.y), -(this.direction.z)), new Vector3(0, -1, 0));
				instanceRotation.rotate(0, 0, 1, 180);
				instanceRotation.getRotation(quaternion);
			} else {
				this.instance.transform.getRotation(quaternion);
			}
			this.instance.transform.set(this.pos, quaternion);
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
				//Vector3 difference = e.pos.cpy().nor();
				//difference.sub(this.pos.nor());  
				//e.accel.set(METER * -e.direction.x/ e.getMass(), METER * -e.direction.y / e.getMass(),
				//METER * -e.direction.z / e.getMass());
			}
		}
		return false;
	}
	
}
