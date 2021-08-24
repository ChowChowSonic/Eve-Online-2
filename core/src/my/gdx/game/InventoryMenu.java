package my.gdx.game;

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
	private Entity user;
	private int width = 400, height = 400;
	SpriteBatch spriteBatch;
	BitmapFont font;
	ArrayList<Button> buttons;

	public InventoryMenu(Entity user) {
		super(screenwidth/2, screenheight/2, 400, 400); 
		this.user = user;
		spriteBatch = new SpriteBatch();
		font = new BitmapFont();
		this.type = Hud.hudtype.InventoryMenu;
		buttons = new ArrayList<Button>();

		int slotx = (screenwidth) / 2 - width / 3;
		int sloty = (screenheight) / 2 + height / 5;
		// y++ = up; x-- = left
		int counter = 0;
		for (Item i : user.inventory.getItems()) {
			buttons.add(new InventoryButton(slotx, screenheight - sloty, i, this));
			slotx += 100;
			counter += 1;
			if (counter == 4) {
				slotx -= 400;
				sloty -= 145;
				counter = 0;
			}
		}
	}

	@Override
	public void updateShape() {
		super.updateShape();
		renderer.setColor(Color.GRAY);
		renderer.rect(x-width/2, y-height/2, width, height);
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
        boolean xisgood = false, yisgood = false;
		if (xpos < (this.x + width/2) && xpos > (this.x - width/2)) {
			xisgood = true;
		}
		if (ypos < (this.y + height/2) && ypos > (this.y - height/2)) {
			yisgood = true;
		}
		return xisgood && yisgood;
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
	}
}
