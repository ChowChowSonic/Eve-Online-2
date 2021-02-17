package my.gdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

public abstract class Hud {
	
	private BitmapFont Texts[]; 
	protected static ShapeRenderer renderer = new ShapeRenderer(); 
	protected static SpriteBatch textrenderer = new SpriteBatch();
	public enum hudtype{HealthBar, InventoryMenu, DockingButton}
	protected hudtype type; 
	
	public void updateShape() {
		if(!renderer.isDrawing())renderer.begin(ShapeType.Filled);
	}
	public void updateText() {
		if(!textrenderer.isDrawing()) {
			textrenderer.begin();
		}
	}

	public static SpriteBatch getTextrenderer() {
		return textrenderer;
	}

	public static ShapeRenderer getRenderer() {
		return renderer;
	}

	@Override
	public boolean equals(Object h){
		Hud h2 = (Hud) h; 
		if (h2.type == this.type) return true;
		return false; 
	}
}
