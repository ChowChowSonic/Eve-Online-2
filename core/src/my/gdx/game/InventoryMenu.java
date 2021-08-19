package my.gdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import my.gdx.game.entities.Player;
import my.gdx.game.inventory.Item;

public class InventoryMenu extends Hud {
	private Player user;
	private int width = 400, height = 400;
	SpriteBatch spriteBatch;
	BitmapFont font;
	ArrayList<Button> buttons;

	public InventoryMenu(Player user) {
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
			buttons.add(new InventoryButton(slotx, sloty, i));
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
		renderer.rect((screenwidth - width) / 2, (screenheight - height) / 2, width, height);
		for (Button b : buttons) {
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

	public boolean isInBounds(float x, float y) {
		boolean xisgood = false, yisgood = false;
		if (x < (screenwidth + width) / 2 && x > (screenwidth - width) / 2) {
			xisgood = true;
		}
		if (y < (screenheight + height) / 2 && y > (screenheight - height) / 2) {
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
}
