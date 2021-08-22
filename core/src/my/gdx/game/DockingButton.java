package my.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import my.gdx.game.entities.Player;
import my.gdx.game.inventory.Item;

public class DockingButton extends Hud {
	SpriteBatch spriteBatch;
	BitmapFont font;
	
	public DockingButton(){
		super(screenwidth/2, screenheight/2-350, 200, 50);
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		this.type = Hud.hudtype.DockingButton; 
	}
	
	@Override
	public void updateShape() {
		super.updateShape();
		renderer.setColor(Color.GRAY);
		renderer.rect((screenwidth-width)/2, (screenheight-height)/2-350, width, height);
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
		
		@Override
		public boolean isInBounds(float xpos, float ypos) {
			boolean xisgood = false, yisgood = false;
			if (xpos < (this.x + width/2) && xpos > (this.x - width/2)) {
				xisgood = true;
			}
			if (ypos < (this.y + height/2) && ypos > (this.y - height/2)) {
				yisgood = true;
			}
			return xisgood && yisgood;
		}
		
		public void interact(float x, float y){
			System.exit(0);
		}
	}
	