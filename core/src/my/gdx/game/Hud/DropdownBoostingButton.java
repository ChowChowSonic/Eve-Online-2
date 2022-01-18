package my.gdx.game.Hud;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.math.Vector3;

import my.gdx.game.EveOnline2;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;

public class DropdownBoostingButton extends DropdownButton{
    private boolean isactive = false, once = true; 
    public DropdownBoostingButton(float xpos, float ypos, String txt, Entity e) {
        super(xpos, ypos, txt, e);
        //TODO Auto-generated constructor stub
    }
    
    @Override
    public void updateShape(){
        super.updateShape();
        Player p = EveOnline2.player; 
        Vector3 direction = p.getPos().cpy().sub(entity.getPos()); 
        if(isactive && p.getRotation().cpy().crs(direction).len2() <= 0.99){
            
            if(once && p.getPos().dst(entity.getPos()) > (entity.getSize()+p.getSize())*10){
                EveOnline2.connection.boostPlayer(-direction.x, -direction.y, -direction.z, true);
                once = false; 
            }
            
        }else if(isactive && p.getPos().dst(entity.getPos()) >= (entity.getSize()+p.getSize())*10){
            EveOnline2.connection.accelPlayer(-direction.x, -direction.y, -direction.z);
        }
        if(p.getPos().cpy().add(p.getVel()).dst2(entity.getPos()) <= (entity.getSize()+p.getSize())*(entity.getSize()+p.getSize())*100 || Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            EveOnline2.removeHUD(this);
            //System.out.println("hg");
        }
    }//updateshape
    
    public void updateText() {
        // TODO Auto-generated method stub
        if(isactive)this.setText("Warping to...");
        super.updateText(); 
    }
    @Override
    public boolean isInBounds(float xpos, float ypos) {
        // TODO Auto-generated method stub
        return super.isInBounds(xpos, ypos) && !isactive;
    }
    
    @Override
    public void dispose() {
        // TODO Auto-generated method stub
        if(isactive)
        EveOnline2.connection.boostPlayer(0,0,0, false);
        //Thread.dumpStack();
    }
    
    @Override
    public void interact(float x, float y){
        if(!isactive){
            isactive = true; 
            EveOnline2.addHUD(this);
            this.moveTo(screenwidth/2, 160);
            Vector3 direction = entity.getPos().cpy().sub(EveOnline2.player.getPos()).nor(); 
            EveOnline2.connection.accelPlayer(direction.x, direction.y, direction.z);
        }
    }
}
