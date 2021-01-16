package my.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import my.gdx.game.entities.Player;
import my.gdx.game.inventory.InventoryItems;

public class InventoryMenu extends Hud{
	private Player user; 
	private boolean isvisible = false;
	private int width = 400, height = 400; 
	SpriteBatch spriteBatch;
	BitmapFont font;
	private int screenwidth = Gdx.graphics.getWidth(), screenheight = Gdx.graphics.getHeight();
	public InventoryMenu(Player user) {
		this.user = user; 
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
	}
	@Override
	public void updateShape() {
		super.updateShape();
		if(Gdx.input.isKeyJustPressed(Keys.I)) this.toggle();
		if(isvisible) {
			renderer.setColor(Color.GRAY);
			renderer.rect((screenwidth-width)/2, (screenheight-height)/2, width, height);
			int slotx =(screenwidth-width)/2 + 3; 
			int sloty = (screenheight)/2;
			int counter = 0;
			for(InventoryItems i : user.inventory.getItems()) {
				generateinvslot(slotx, sloty, i);
				slotx+= 100;
				counter +=1;
				if(counter == 4) {
					slotx-=400;
					sloty-=145;
					counter = 0;
				}

			}
		}
	}

	private void generateinvslot(int x, int y, InventoryItems i) {
		renderer.setColor(Color.WHITE);
		renderer.rect(x, y, 95, 142);

	}
	@Override
	public void updateText() {
		if(isvisible) {
			super.updateText();
			font.setColor(Color.BLACK);
			int slotx = (screenwidth-width)/2+3; 
			int sloty = (screenheight)/2;
			int counter =0;
			for(InventoryItems i : user.inventory.getItems()) {
			CharSequence str = i.getName().replaceAll(" ", "\n") +"\n"+ (int)i.getSize() +"m3, "+(int)i.getWeight()+"kg";			

			spriteBatch.begin();
			font.getData().setScale((float) 1);
			font.draw(spriteBatch, str, slotx, sloty+70, 0, -1, false);
			slotx+=100;
			spriteBatch.end();
			counter +=1;
			if(counter == 4) {
				slotx-=400;
				sloty-=145;
				counter = 0;
			}
			}
		}
	}

	public void toggle() {
		isvisible = !isvisible; 
	}
}
