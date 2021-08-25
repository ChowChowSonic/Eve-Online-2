package my.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;

public class HeaderButton extends Button{
    
    Hud parent; 
    public HeaderButton(float x, float y, float width, Hud parent){
        super(x,y+25, width, 50); 
        this.parent = parent; 
    }
    
    public void updateShape(){
        renderer.setColor(Color.WHITE);
        renderer.rect(x - width / 2, (screenheight - y) - (height / 2), width, height);
        if(isactive){
        parent.translateTo(Gdx.input.getX(), Gdx.input.getY()+parent.height/2-25);
        //if(Gdx.input.isButtonJustPressed(Buttons.LEFT)) isactive = false;
        }
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
    
}
