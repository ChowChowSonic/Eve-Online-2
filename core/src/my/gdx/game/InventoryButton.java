package my.gdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import my.gdx.game.inventory.Item;

public class InventoryButton extends Button {
    private String text;
    private Item item;
    private InventoryMenu parent; 
    private Texture texture; 
    
    
    public InventoryButton(float xpos, float ypos, Item baseItem, InventoryMenu parent) {
        super(xpos, ypos, 90, 160);
        this.parent = parent; 
        this.item = baseItem;
        text = correctAlignment(baseItem.toString(), 15);
    }
    
    @Override
    public void updateShape() {
        super.updateShape();
        if (isactive) {
            x = Gdx.input.getX();
            y = Gdx.input.getY();
            if (!parent.isInBounds(x,y) && Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
                for(int i = 0; i < EveOnline2.windows.size(); i++){
                    Hud h = EveOnline2.windows.get(i);
                    if(h.type == hudtype.InventoryMenu && h.isInBounds(x, y)) {
                        InventoryMenu menu = (InventoryMenu) h;
                        EveOnline2.connection.transferInventoryItem(this.parent.user, menu.user, this.item);
                        parent.removeButton(this);
                        return;
                    }
                }
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
        textrenderer.draw(new Texture("E.png"), x-width/2+8, screenheight-(this.y)+4, width-16, width-16);
        font.getData().setScale((float) 0.95);
        font.setColor(Color.BLACK);
        font.draw(textrenderer, text, x - width / 2, screenheight - y);
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
