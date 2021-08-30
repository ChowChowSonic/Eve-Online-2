package my.gdx.game.Hud;

import com.badlogic.gdx.Gdx;

public abstract class Button extends Hud {
    protected boolean isactive;

    public Button(float xpos, float ypos, float width, float height) {
        super(xpos, ypos, width, height, null); 
        isactive = false;
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
    public abstract void interact(float x, float y);

    @Override
    public void translate(float dx, float dy){
        this.x-=dx;
        this.y-=dy; 
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public boolean isActive(){
        return isactive; 
    }

    @Override
    public void dispose(){

    }
}
