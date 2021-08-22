package my.gdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;

public class HealthBar extends Hud{
	private Player p;
	BitmapFont health;
	private final float AU = (float) (Math.pow(10, 11)*1.4960);

	HealthBar(Player player2track) {
		super(screenwidth/2, 0, 100, 100);
		this.p = player2track;
		health = new BitmapFont();
		this.type = Hud.hudtype.HealthBar; 
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
		health.setColor(Color.BLACK);
		float velocity = p.getVel().len()/Entity.METER;
		if (velocity < 1000) {
		health.draw(textrenderer, String.format("%.2f", velocity)+" m/s", Gdx.graphics.getWidth()/2-20, 20);
		}else if(velocity < 100000){
			health.draw(textrenderer, String.format("%.2f", velocity/1000)+" km/s", Gdx.graphics.getWidth()/2-20, 20);
		}else {
			health.draw(textrenderer, String.format("%.2f", velocity/100000)+" AU/s", Gdx.graphics.getWidth()/2-20, 20);
		}
	}

	@Override
	public boolean isInBounds(float x, float y) {
		float dx = this.x - x; 
		float dy = this.y - y; 
		return Math.sqrt((dx*dx)+(dy*dy)) <= 100; 
	}

	public void interact(float x, float y){
		
	}
}
