package my.gdx.game.Hud;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
import my.gdx.game.inventory.Item;

public class InventoryMenu extends Hud {
	protected Entity user;
	
	public InventoryMenu(Entity user) {
		super(screenwidth/2, screenheight/2, 400, 400); 
		this.user = user;
		this.type = Hud.hudtype.InventoryMenu;
		buttons = new ArrayList<Button>();
		
		float slotx = (screenwidth) / 2 - width / 3;
		float sloty = (screenheight) / 2 + height / 5;
		// y++ = up; x-- = left
		int counter = 0;
		for (Item i : user.inventory.getItems()) {
			buttons.add(new InventoryButton(slotx, screenheight - sloty, i, "E.png",this));
			slotx += 100;
			counter += 1;
			if (counter == 4) {
				slotx -= 400;
				sloty -= 145;
				counter = 0;
			}
		}
		
		buttons.add(new HeaderButton(x, screenheight-(y+height/2), width, this.user.getEntityType().name()+"'s Inventory", this)); 
	}
	
	@Override
	public void updateShape() {
		super.updateShape();
		renderer.setColor(Color.GRAY);
		renderer.rect(x-width/2, screenheight-(y+height/2), width, height);

		for (int i = 0; i < buttons.size(); i++) {
			Button b = buttons.get(i); 
			b.updateShape();
		}
	}
	
	@Override
	public void updateText() {
		super.updateText();
		for (Button b : buttons) {
			b.updateText();
		}
	}
	
	@Override
	public boolean isInBounds(float xpos, float ypos) {
		
		for (Button b : buttons) {
			if(b.isInBounds(x,y)){
				return true;
			}
		}
		return super.isInBounds(xpos, ypos); 
	}
	
	public void interact(float x, float y) {
		// Do something
		for (Button b : buttons) {
			if (b.isInBounds(x, y)) {
				b.interact(x, y);
				break;
			}
		}
	}
	
	public void removeButton(Button b){
		buttons.remove(b); 
		b.dispose();
	}

	public boolean equals(Object o){
		if(o instanceof InventoryMenu){
		InventoryMenu m = (InventoryMenu) o;
			return this.user.equals(m.user);
		}
		return false;
	}

	public void dispose(){
		super.dispose();
		user = null;
	}
}
