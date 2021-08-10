package my.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import my.gdx.game.entities.Player;
import my.gdx.game.inventory.Item;

public class DockingButton extends Hud {
	private int width = 200, height = 50; 
	SpriteBatch spriteBatch;
	BitmapFont font;
	private int screenwidth = Gdx.graphics.getWidth(), screenheight = Gdx.graphics.getHeight();
	
	public DockingButton(){
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		this.type = Hud.hudtype.DockingButton; 
	}
	
	@Override
	public void updateShape() {
		super.updateShape();
		//if(isvisible) {
			renderer.setColor(Color.GRAY);
			renderer.rect((screenwidth-width)/2, (screenheight-height)/2-350, width, height);
		//}
	}
	
	@Override
	public void updateText() {
		//if(isvisible) {
			super.updateText();
			spriteBatch.begin();
			font.setColor(Color.WHITE);
			font.getData().setScale((float) 3.8);
			font.draw(spriteBatch, "Dock?", (screenwidth-width)/2+25, (screenheight-height)/2-352.5f+height);
			spriteBatch.end();
			//}
		}
	}
	