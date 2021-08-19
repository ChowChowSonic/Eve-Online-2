package my.gdx.game;

public abstract class Button extends Hud{
    protected float x, y, height, width;
    protected boolean isactive;
    
    public Button(float xpos, float ypos, float width, float height){
        this.x = xpos; 
        this.y = ypos; 
        this.height = height; 
        this.width = width; 
        isactive = false; 
    }

    @Override
    public boolean isInBounds(float mousex, float mousey) {
        boolean xisgood = false, yisgood = false; 
		if(x < (x+width)/2 && x > (x-width)/2){
			xisgood = true; 
		}
		if(y < (y+height)/2 && y > (y-height)/2){
			yisgood = true; 
		}
		return xisgood && yisgood; 
    }

    @Override
    public abstract void interact(float x, float y);
    
}
