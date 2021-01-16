package my.gdx.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import my.gdx.game.EveOnline2;

public class CelestialObject extends Entity{

	protected final Material material = new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("2k_sun.jpg"))), 
			ColorAttribute.createSpecular(1, 1, 1, 1),
			FloatAttribute.createShininess(100f));
	protected final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;

	public CelestialObject(Vector3 position, Model model, float mass, float radius) {
		super(model, EntityType.CELESTIALOBJ);
		this.pos = position;
		this.setMass(mass); this.size = radius;
		// TODO Auto-generated constructor stub
	}
	@Override
	public void update(float deltaTime) {
		this.instance.transform.set(this.pos, new Quaternion());
		int size2 = (int) Math.max(1000f*Math.pow(Math.E, -Math.pow(EveOnline2.player.pos.dst(this.pos)/(this.size*10), 2)), 1);
		Model newmodel = this.model;
		if(size2 > 0) {
			newmodel = EveOnline2.builder.createSphere(size2, size2, size2, 100, 100, this.material, attributes);
		}else {//Generate an image of what should be a star from really far away...\]
			
		}
		this.instance = new ModelInstance(newmodel);
	}

	/**
	 * The CelectialObject.touches() method overrides the Enitty.touches() method.
	 * Most celestial objects are going to be too big for players to have any real effect on them. 
	 * Because of this, I just decided to skip the calculations for the celestial object. 
	 * This bascially saves the PC some calculatory overhead. 
	 */
	@Override
	public boolean touches(Entity e) {
		float distance  = this.pos.dst2(e.pos);
		if(distance < ((this.size*1.1)*(this.size*1.1))+(e.size*e.size)) {
			if(distance < (this.size*this.size)+(e.size*e.size)) {
				Vector3 forcetoapply1 = new Vector3(
						(e.vel.x*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))),
						(e.vel.y*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))),
						(e.vel.z*((e.getMass()-this.getMass())/(e.getMass()+this.getMass()))));
				e.addAccel(forcetoapply1);
				e.setVel(forcetoapply1);
				return true; 
			}else {
				Vector3 difference = this.pos.cpy().sub(e.pos).nor(); 
				e.addAccel(-difference.x*ACCEL, -difference.y*ACCEL, -difference.z*ACCEL);
			}
		}
		return false;
	}

}
