package my.gdx.game.Hud;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import my.gdx.game.EveOnline2;
import my.gdx.game.entities.Entity;

public class DropdownMenu extends Hud{
    private Entity target; 
    private float nextopenspace; 
    public DropdownMenu(float xpos, float ypos, Entity ent) {
        super(xpos, ypos, 200, 250, hudtype.dropdown);
        this.y-=height/2*Math.signum(ypos-screenheight/2); 
        this.x-=width/2*Math.signum(xpos-screenwidth/2); 
        this.target = ent; 
        this.buttons = new ArrayList<Button>(); 
        //if(y < screenheight/2) y+=height; 
        nextopenspace = this.y-height/2+15; 
        if(ent.inventory != null && ent.getPos().dst(EveOnline2.player.getPos()) <= 100000*(EveOnline2.player.getSize()+ent.getSize())*Entity.METER){
            addbutton(new DropdownButton(this.x, nextopenspace, "Open Inventory", target){//define custom method for this button
                @Override
                public void interact(float x, float y){
                    EveOnline2.addHUD(new InventoryMenu(target)); 
                }
            }); 
        }
        if(!ent.equals(EveOnline2.player)){
            addbutton(new DropdownButton(this.x, nextopenspace, "Approach", target){//define custom method for this button
                @Override
                public void interact(float x, float y){
                    Vector3 direction = target.getPos().cpy().sub(EveOnline2.player.getPos()).nor(); 
                    //while(!EveOnline2.player.isAccelerating())
                    EveOnline2.connection.accelPlayer(direction.x, direction.y, direction.z);
                }

                public void dispose(){
                    Thread.dumpStack();
                }
            }); 
            
            addbutton(new DropdownBoostingButton(this.x, nextopenspace, "Warp to", ent));
        }
    }
    @Override
    public void updateShape(){
        super.updateShape();
        renderer.setColor(Color.DARK_GRAY);
        renderer.rect(x-width/2, screenheight-(y+height/2), width, height);
        if(buttons != null)
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
        if(buttons != null){
            for(int i = 0; i < buttons.size(); i++){
                Button b = buttons.get(i); 
                if(b.isInBounds(x, y)){
                    b.interact(x, y);
                    EveOnline2.removeHUD(this);
                    break;
                }
            }
        }   
    }
    private void addbutton(Button b){
        buttons.add(b); 
        nextopenspace+=25;
        
    }
    @Override
    public void dispose(){
        if(buttons != null){
            for(Button b : buttons){
                b.dispose();
            }
            buttons.clear();
            buttons = null;
        }
        this.type = null; 
        target = null;
    }
}
