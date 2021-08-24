package my.gdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.graphics.Color;

public class PagerButton extends Button{
    private String text;
    
    public PagerButton(float x, float y, String text, float length){
        super(x,y, length, 50f); 
        this.text = text; 
    }
    
    public void updateShape(){
        super.updateShape();
        renderer.setColor(Color.NAVY);
        renderer.rect(x-width/2, screenheight-y-height/2, width, height);
    }
    
    public void updateText(){
        super.updateText();
        font.getData().setScale((float) 2);
        font.draw(textrenderer, text, x-font.getScaleX(), screenheight - y+font.getScaleY()); 
        
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
    
    @Override
    public void interact(float x, float y) {
        // TODO Auto-generated method stub
        if(Gdx.input.isButtonJustPressed(Buttons.LEFT))
        this.isactive = !isactive; 
    }
    
    public void setActive(boolean b){
        this.isactive = b; 
    }
    
    public void setText(String s){
        this.text = s; 
    }

    /**
    * Returns true if the button is active at the time of calling, then sets button activity to false. 
    * @return
    */
    public boolean getInput(){
        if(isactive){
            isactive = false; 
            return true; 
        }
        return false; 
    }
    
    
    
}
