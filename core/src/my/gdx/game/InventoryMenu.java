package my.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import my.gdx.game.entities.Player;
import my.gdx.game.inventory.InventoryItem;

public class InventoryMenu extends Hud{
	private Player user; 
	private boolean isvisible = false;
	private int width = 400, height = 400; 
	private int screenwidth = Gdx.graphics.getWidth(), screenheight = Gdx.graphics.getHeight();
	public InventoryMenu(Player user) {
		this.user = user; 
	}
	@Override
	public void updateShape() {
		super.updateShape();
		if(Gdx.input.isKeyJustPressed(Keys.I)) this.toggle();
		if(isvisible) {
		renderer.setColor(Color.GRAY);
		renderer.rect((screenwidth-width)/2, (screenheight-height)/2, width, height);
		int slotx =(screenwidth-width)/2; 
		int sloty = (screenheight-height)/2;
		for(InventoryItem i : user.inventory.getItems()) {
			generateinvslot(slotx, sloty, i);
			slotx+= 51;
			
		}
		}
		}
	
	private void generateinvslot(int x, int y, InventoryItem i) {
		renderer.setColor(Color.WHITE);
		renderer.rect(x, y, 50, 75);
	}
	
	public void toggle() {
		isvisible = !isvisible; 
	}
}
