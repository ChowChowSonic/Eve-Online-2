package my.gdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import my.gdx.game.inventory.Item;

public class InventoryButton extends Button {
    private String text;
    private SpriteBatch spritebatch;
    private BitmapFont font;
    private Item item;
    private InventoryMenu parent; 
    
    public InventoryButton(float xpos, float ypos, Item baseItem, InventoryMenu parent) {
        super(xpos, ypos, 95, 142);
        this.parent = parent; 
        this.item = baseItem;
        char[] txt = item.toString().toCharArray();
        text = new String();
        for (int i2 = 0; i2 < txt.length; i2++) {
            if (i2 > 5 && i2 < 10 && txt[i2] == ' ')
            text += "\n";
            else {
                int n = (text.lastIndexOf("\n") > 0) ? text.lastIndexOf("\n") : 0;
                if (text.substring(n, i2).length() > 13) {
                    text += "\n";
                }
                text += txt[i2];
            }
            spritebatch = new SpriteBatch();
            font = new BitmapFont();
        }
    }

    @Override
    public void updateShape() {
        super.updateShape();
        if (isactive) {
            x = Gdx.input.getX();
            y = Gdx.input.getY();
            if (!parent.isInBounds(x,y) && Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
                EveOnline2.connection.dropInventoryItem(item); 
                parent.removeButton(this);
                return; 
            }
        }
        renderer.setColor(Color.WHITE);
        renderer.rect(x - width / 2, (screenheight - y) - (height / 2), width, height);
    }

    @Override
    public void updateText() {
        super.updateText();
        spritebatch.begin();
        font.getData().setScale((float) 1);
        font.setColor(Color.BLACK);
        font.draw(spritebatch, text, x - width / 2, screenheight - y);
        spritebatch.end();
    }
    
    @Override
    public void interact(float x, float y) {
        isactive = !isactive;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof InventoryButton){
            InventoryButton b = (InventoryButton) o; 
             return (b.item.toString().equals(this.item.toString())); 
        }else if(o instanceof Item){
            Item i2 = (Item) o;
            return i2.toString().equals(this.item.toString()); 
        }
        return false; 
    }
}
