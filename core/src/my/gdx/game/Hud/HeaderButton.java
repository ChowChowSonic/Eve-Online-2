package my.gdx.game.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;

import my.gdx.game.EveOnline2;

public class HeaderButton extends Button{
    
    Hud parent; 
    String text;
    public HeaderButton(float x, float y, float width, String text, Hud parent){
        super(x,y+10, width, 20); 
        this.parent = parent; 
        this.text = text; 
    }
    
    public void updateShape(){
        renderer.setColor(Color.WHITE);
        renderer.rect(x - width / 2, (screenheight - y) - (height / 2), width, height);
        if(isactive){
        parent.translateTo(Gdx.input.getX(), Gdx.input.getY()+parent.height/2-this.height/2);
        //if(Gdx.input.isButtonJustPressed(Buttons.LEFT)) isactive = false;
        }
    }

    public void updateText(){
        font.getData().setScale(1.4f);
        font.setColor(Color.BLACK);
        font.draw(textrenderer, "X", x+width/2-15, screenheight-y+7); 
        font.draw(textrenderer, text, x-width/4, screenheight - y +7); 
        font.getData().setScale(1f);
    }
    
    @Override
    public void interact(float x, float y) {
        // TODO Auto-generated method stub
        if(super.isInBounds(x, y)){
            if(x > this.x+width/2-20){
                EveOnline2.removeHUD(parent);
            }else if(x <= this.x+width/2-20 && x > this.x-width/2){
                isactive = !isactive; 
            }
        }
        
    }

    public void dispose(){
        parent = null; 
        text = null;
    }
    
}
