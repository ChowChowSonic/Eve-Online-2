package my.gdx.game;

import com.badlogic.gdx.Gdx;

public abstract class Button extends Hud {
    protected float x, y, height, width;
    protected boolean isactive;

    public Button(float xpos, float ypos, float width, float height) {
        this.x = xpos;
        this.y = screenheight - ypos;
        this.height = height;
        this.width = width;
        isactive = false;
    }

    @Override
    public boolean isInBounds(float mousex, float mousey) {
        boolean xisgood = false, yisgood = false;
        if (mousex < x + (width / 2) && mousex > x - (width / 2)) {
            xisgood = true;
        }
        if (mousey < y + (height / 2) && mousey > y - (height / 2)) {
            yisgood = true;
        }
        System.out.println("Is in bounds: " + (xisgood && yisgood));
        return xisgood && yisgood;
    }

    @Override
    public abstract void interact(float x, float y);

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
}
