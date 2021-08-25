package my.gdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;

import my.gdx.game.entities.Entity;

public class DropdownMenu extends Hud{
    private Entity target; 
    public DropdownMenu(float xpos, float ypos, Entity ent) {
        super(xpos, ypos, 200, 250);
        this.y-=height/2*Math.signum(ypos-screenheight/2); 
        this.x-=width/2*Math.signum(xpos-screenwidth/2); 
        this.target = ent; 
        this.type = hudtype.dropdown; 
        this.buttons = new ArrayList<Button>(); 
        //if(y < screenheight/2) y+=height; 
        float nextopenspace = this.y-height/2+15; 
        if(ent.inventory != null){
            this.buttons.add(new DropdownButton(this.x, nextopenspace, "Open Inventory", target){//define custom method for this button
                @Override
                public void interact(float x, float y){
                    EveOnline2.addHUD(new InventoryMenu(target)); 
                    EveOnline2.removeHUD(hudtype.dropdown);
                }
            }); 
            nextopenspace+=15; 
        }
    }
    @Override
    public void updateShape(){
        super.updateShape();
        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(x-width/2, screenheight-(y+height/2), width, height);
        for(int i = 0; i < buttons.size(); i++){
            Button b = buttons.get(i); 
            b.updateShape();
        }
    }
    
    public void updateText(){
        for(int i = 0; i < buttons.size(); i++){
            Button b = buttons.get(i); 
            b.updateText();
        }
    }

    public boolean isInBounds(float x, float y){
        for(Button b : buttons){
            if(b.isInBounds(x, y)) return true; 
        }
        return super.isInBounds(x, y);
    }
    
    @Override
    public void interact(float x, float y) {
        // TODO Auto-generated method stub
        for(int i = 0; i < buttons.size(); i++){
            Button b = buttons.get(i); 
            if(b.isInBounds(x, y))b.interact(x, y);
        }
        
    }
    
}
