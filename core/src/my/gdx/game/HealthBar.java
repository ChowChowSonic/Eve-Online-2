package my.gdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;

public class HealthBar extends Hud{
	private Player p;
	BitmapFont health;

	HealthBar(Player player2track) {
		this.p = player2track;
		health = new BitmapFont();
		// TODO Auto-generated constructor stub
	}

	@Override
	public void updateShape() {
		super.updateShape();
		int shields =p.getShields(), maxshields = p.getMaxshields();
		int armor = p.getArmor(), maxarmor = p.getMaxarmor();
		int hull = p.getHull(), maxhull = p.getMaxhull();
		renderer.setColor(Color.WHITE);
		renderer.circle(Gdx.graphics.getWidth()/2, 0, 100);
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
		if (velocity < 14959) {
		health.draw(textrenderer, String.format("%.2f", velocity)+" m/s", Gdx.graphics.getWidth()/2-20, 20);
		}else {
			velocity *=1000;
			health.draw(textrenderer, String.format("%.2f", velocity/149597871.0)+" AU/s", Gdx.graphics.getWidth()/2-20, 20);
		}
	}

}
