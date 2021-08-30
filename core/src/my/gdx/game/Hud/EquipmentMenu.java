package my.gdx.game.Hud;

import java.util.ArrayList;

public class EquipmentMenu extends Hud{
    private int gun, def, supp; 
    protected enum EquipmentType{gun, defense, suppliment}; 
    public EquipmentMenu(int guns, int defenses, int suppliments) {
        super(screenwidth/2+200, screenheight-50, 140, 100, hudtype.equipment);
        buttons = new ArrayList<Button>(); 
        float buttonx = x-width/2-70;
        float buttony = y-height/2-5; 

        for(int i = 0; i < guns; i++){
            buttons.add(new EquipmentButton(buttonx,buttony, EquipmentType.gun)); 
            buttonx += 42; 
        } 
        gun = guns; 
        buttony +=42;
        buttonx = x-width/2-25; 

         for(int i = 0; i < defenses; i++){
            buttons.add(new EquipmentButton(buttonx,buttony, EquipmentType.defense)); 
            buttonx += 42; 
        } 
        def = defenses; 
        buttony +=42; 
        buttonx = x-width/2-10; 

        for(int i = 0; i < suppliments; i++){
            buttons.add(new EquipmentButton(buttonx,buttony, EquipmentType.suppliment)); 
            buttonx += 42; 
        } 
        supp = suppliments; 
        //TODO Auto-generated constructor stub
    }

    public boolean isInBounds(float x, float y){
        for(Button b : buttons){
            if(b.isInBounds(x, y)){
                b.interact(x, y);
                return true; 
            }
        }
        return false; 
    }

    public void updateShape(){
        super.updateShape();
        for(Button b : buttons){
            b.updateShape();
        }
    }

    @Override
    public void interact(float x, float y) {
        // TODO Auto-generated method stub
        
    }

    public void dispose(){
        super.dispose();
    }
}
