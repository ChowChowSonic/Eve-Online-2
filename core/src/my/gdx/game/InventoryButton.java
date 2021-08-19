package my.gdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import my.gdx.game.inventory.Item;

public class InventoryButton extends Button{
    private String text;
    private SpriteBatch spritebatch; 
    private BitmapFont font; 
    
    public InventoryButton(float xpos, float ypos, Item i) {
        super(xpos, ypos, 95, 142);
        char[] txt = i.toString().toCharArray(); 
        text = new String();
        for(int i2 = 0; i2 < txt.length; i2++){
            if(i2 > 5 && i2 < 10 && txt[i2]==' ') text+="\n"; 
            else {
                int n = (text.lastIndexOf("\n") > 0) ? text.lastIndexOf("\n") : 0; 
             if(text.substring(n, i2).length() > 13){
                 text+="\n";
             }
                text+=txt[i2];
            }
            spritebatch = new SpriteBatch(); 
            font = new BitmapFont();
        }
    }
    
    public void updateShape(){
        super.updateShape();
        if(isactive){
            x = Gdx.input.getX(); 
            y = Gdx.input.getY(); 
        }
        renderer.setColor(Color.WHITE);
        renderer.rect(x-width/2, y-height/2, width, height);
    }
    
    public void updateText(){
        super.updateText();
        spritebatch.begin();
        font.getData().setScale((float) 1);
        font.setColor(Color.BLACK);
        font.draw(spritebatch, text, x-width/2, y); 
        spritebatch.end();
    }
    
    @Override
    public void interact(float x, float y) {
        if(this.isInBounds(x, y)){
            isactive = !isactive; 
        }
    }
    
}
