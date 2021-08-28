package my.gdx.game.Hud;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import my.gdx.game.entities.Entity;

public class TargetHud extends Hud{
    public int limit;
    private TargetButton activeTarget; 
    private float slotx, sloty;
    private int errorFrames = 0;

    public TargetHud(int lim) {
        super(screenwidth-(110*lim/2), 0, 110*lim, 150);
        if(this.width > screenwidth*0.4) {
            this.width = screenwidth*0.4f;
            this.x = screenwidth-(width/2); 
        }
        this.limit = lim; 
        slotx = screenwidth-85; 
        sloty = this.y+85; 
        this.type = hudtype.target;
        buttons = new ArrayList<Button>(); 
        //TODO Auto-generated constructor stub
    }
    
    public boolean addTarget(Entity e){
        TargetButton b = new TargetButton(slotx, sloty, e); 
        if(buttons.size() < limit && !buttons.contains(b)) {
            buttons.add(b); 
            slotx -= b.width+5; 
            if(slotx <= screenwidth*0.6){
                slotx = screenwidth - 85; 
                sloty += 80;
            }
            reorder();
            return true;
        }else if(!buttons.contains(b)){
            errorFrames = 120; 
        }
        return false;
    }

    private void reorder(){
        float OGslotx = screenwidth-85; 
        float OGsloty = this.y+85; 
        for(Button b : buttons){
            if(!b.isInBounds(OGslotx, OGsloty)){
                b.x = OGslotx;
                b.y = OGsloty;
            }
            OGslotx -= b.width+5;
            if(OGslotx <= screenwidth*0.6){
                OGslotx = screenwidth - 85; 
                OGsloty += 80;
            }
        }
    }
    
    public void updateShape(){
        super.updateShape();
        renderer.setColor(Color.BLUE);
        renderer.rect(x-width/2-25, screenheight-(y+height/10), width, height/10);
        renderer.rect(x+width/2-25, screenheight-height, 25, height);
        if(activeTarget != null)
        renderer.rect(activeTarget.x-(activeTarget.width/2), screenheight-activeTarget.y-activeTarget.width/2, activeTarget.width, activeTarget.height);
        if(errorFrames > 0){
            renderer.rect(screenwidth/2-175, screenheight/2+300, 350, 50);
            errorFrames--; 
        }
        for (int i = 0; i < buttons.size(); i++) {
            Button b = buttons.get(i); 
            b.updateShape();
        }
        
    }
    
    public void updateText(){
        super.updateText();
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
        font.draw(textrenderer, "Targets: "+ buttons.size() + "/"+this.limit, x-width/4, screenheight); 
        if(errorFrames > 0) font.draw(textrenderer, "Bandwidth cap reached - Unable to target object.", screenwidth/2-150, screenheight/2+325); 
        for (int i = 0; i < buttons.size(); i++) {
            Button b = buttons.get(i); 
            b.updateText();
        }
    }
    
    public boolean isInBounds(float x, float y){
        for(int i = 0; i < buttons.size(); i++){
            TargetButton b = (TargetButton) buttons.get(i); 
            if(b.isInBounds(x, y)) {
                if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT)){
                    if(activeTarget != null && activeTarget.equals(b)) activeTarget = null;
                    buttons.remove(b);
                    reorder();
                }else{
                    activeTarget = (TargetButton) b;
                }
                return true;
            }
        }
        return false;
    }
    
    public Entity getActiveTarget(){
        return activeTarget.getEntity();
    }
    
    @Override
    public void interact(float x, float y) {
        
    }
    
    public void dispose(){
        super.dispose();
        activeTarget = null; 
    }
    
}
