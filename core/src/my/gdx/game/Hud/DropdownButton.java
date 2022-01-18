package my.gdx.game.Hud;

import com.badlogic.gdx.graphics.Color;

import my.gdx.game.entities.Entity;


public class DropdownButton extends Button{
    protected final Entity entity;
    private String text; 
	public DropdownButton(float xpos, float ypos, String txt, Entity e) {
		super(xpos, ypos, 190, 20);
        this.entity = e; 
        text = txt; 
		//TODO Auto-generated constructor stub
	}

    @Override
    public void updateShape(){
        if(isactive) renderer.setColor(Color.GREEN);
        else renderer.setColor(Color.WHITE);
        renderer.rect(x - width / 2, (screenheight - y) - (height / 2), width, height);
    }

    public void updateText(){
        font.setColor(Color.BLACK);
        font.getData().setScale(1.25f);
        font.draw(textrenderer, text, x - width / 2, screenheight - y+height/2);
    }

	@Override
	public void interact(float x, float y) {
		// TODO Auto-generated method stub
	}

    public void setText(String s){
        text = s; 
    }

    public String getText(){
        return text; 
    }

    public boolean equals(Object o){
        if(o instanceof DropdownButton){
            DropdownButton d = (DropdownButton) o; 
        return entity.equals(d.entity) && text.equals(d.text); 
        }
        return false;
    }
    
}
