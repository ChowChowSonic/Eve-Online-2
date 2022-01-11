package my.gdx.game.Hud;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import my.gdx.game.EveOnline2;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;

public class HealthBar extends Hud{
	private Player p;
	private final static float AU = (float) (Math.pow(10, 11)*1.4960);

	public HealthBar(Player player2track) {
		super(screenwidth/2, 0, 100, 100, hudtype.HealthBar);
		this.p = player2track;
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateShape() {
		super.updateShape();
		int shields =p.getShields(), maxshields = p.getMaxshields();
		int armor = p.getArmor(), maxarmor = p.getMaxarmor();
		int hull = p.getHull(), maxhull = p.getMaxhull();
		renderer.setColor(Color.WHITE);
		renderer.circle(x, y, width);
		renderer.setColor(Color.BLUE);
		renderer.rect(Gdx.graphics.getWidth()/2-50, 75, 100*shields/maxshields, 10);
		renderer.setColor(Color.DARK_GRAY);
		renderer.rect(Gdx.graphics.getWidth()/2-50, 60, 100*armor/maxarmor, 10);
		float red = 2*((float)maxhull-hull)/maxhull;
		float green = 2*((float)hull/maxhull);
		renderer.setColor(red, green, 0, 1);
		renderer.rect(Gdx.graphics.getWidth()/2-50, 45, 100*hull/maxhull, 10);
	}

	public void updateText() {
		super.updateText();
		font.setColor(Color.BLACK);
		String unit = "m/s"; 
		float velocity = p.getVel().len()/Entity.METER; 
		if(p.isBoosting()){
			unit = "AU/s"; 
			velocity /= 100000; 
		}else if (velocity > 1000) {
			unit = "Km/s"; 
			velocity/=1000; 
		}
		font.draw(textrenderer, String.format("%.2f", velocity)+" "+unit, Gdx.graphics.getWidth()/2-20, 20);
	}

	@Override
	public boolean isInBounds(float x, float y) {
		float dx = this.x - x; 
		float dy = this.y - y; 
		return Math.sqrt((dx*dx)+(dy*dy)) <= 100; 
	}

	public void interact(float x, float y){
		
	}

	public void dispose(){
		super.dispose();
		p = null; 
	}
}
