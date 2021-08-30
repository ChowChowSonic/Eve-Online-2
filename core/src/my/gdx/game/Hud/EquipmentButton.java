package my.gdx.game.Hud;

import com.badlogic.gdx.graphics.Color;

import my.gdx.game.EveOnline2;
import my.gdx.game.Hud.EquipmentMenu.EquipmentType;

public class EquipmentButton extends Button{
    private EquipmentType type; 
    public EquipmentButton(float xpos, float ypos, EquipmentType type) {
        super(xpos, ypos, 20, 20);
        this.type = type; 
        //TODO Auto-generated constructor stub
    }

    public void updateShape(){
        super.updateShape();
        switch(type){
            case gun:
            renderer.setColor(Color.RED);
            renderer.circle(x, screenheight-y, width);
            return;
            case defense:
            renderer.setColor(Color.BLUE);
            renderer.circle(x, screenheight-y, width);
            //Do something here later; 
            return;
            case suppliment:
            renderer.setColor(Color.GREEN);
            renderer.circle(x, screenheight-y, width);
            return;
        }
        
    }

    @Override
    public void interact(float x, float y) {
        // TODO Auto-generated method stub
        switch(type){
            case gun:
            EveOnline2.shootActiveEntity();
            return;
            case defense:
            //Do something here later; 
            return;
            case suppliment:
            return;
        }
        
    }

    @Override
	public boolean isInBounds(float x, float y) {
		float dx = this.x - x; 
		float dy = this.y - y; 
		return Math.sqrt((dx*dx)+(dy*dy)) <= width; 
	}
    
}
