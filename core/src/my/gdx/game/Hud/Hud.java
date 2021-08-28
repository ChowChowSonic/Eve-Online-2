package my.gdx.game.Hud;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.Disposable;

public abstract class Hud implements Disposable{
	protected static ShapeRenderer renderer = new ShapeRenderer();
	protected static SpriteBatch textrenderer = new SpriteBatch();
	protected static BitmapFont font = new BitmapFont(); 
	protected float x, y, height, width;
	protected hudtype type;
		
	public static final int screenheight = Gdx.graphics.getHeight(), screenwidth = Gdx.graphics.getWidth();
	ArrayList<Button> buttons;
	
	public Hud(float x, float y, float width, float height){
		this.x = x;
		this.y = y; 
		this.width = width; this.height = height; 
	}
	
	public static enum hudtype {
		HealthBar, InventoryMenu, DockingButton, Infomenu, dropdown, target
	}
	/**
	* Updates the shape of the image (I.E. the Opaque white/gray/colorful rectangles, a circle acting as a border)
	* DOES NOT UPDATE THE IMAGES SHOWN ON SCREEN. updateText() does that.  <p>
	* If you're looking at Hud.updateShape() (the abstract class's method): <p> 
	* begins the renderer, and any associated batch files if they're not already running. 
	*/
	public void updateShape() {
		if (!renderer.isDrawing())
		renderer.begin(ShapeType.Filled);
	}
	
	/**
	* Updates the letters drawn on screen alongside any images that may be needed. <p>
	* If you're looking at Hud.updateText() (the abstract class's method): <p> 
	* begins the renderer, and any associated batch files if they're not already running. 
	*/
	public void updateText() {
		if (!textrenderer.isDrawing()) {
			textrenderer.begin();
		}
	}
	
	/**
	* Returns true if a specified pixel on the screen is located "on top" of the
	* HUD
	* 
	* @param x The x position of the pixel you want to check
	* @param y The y position of the pixel you want to check
	* @return True if hte specified pixel is, in fact, on top of this HUD
	*/
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
	
	/**
	* Allows the player to interact with the HUD via the mouse
	* 
	* @param x - The X position of the mouse
	* @param y - The Y position of the mouse
	*/
	public abstract void interact(float x, float y);
	
	public static SpriteBatch getTextrenderer() {
		return textrenderer;
	}
	
	public static ShapeRenderer getRenderer() {
		return renderer;
	}

	public static BitmapFont getFont(){
		return font; 
	}
	
	@Override
	public boolean equals(Object h) {
		Hud h2 = (Hud) h;
		if (h2.type == this.type)
		return true;
		return false;
	}
	
	public ArrayList<Button> getButtons(){
		return buttons; 
	}

	protected static String correctAlignment(String oldstring, int MaxLineLength){
        String formattedString = ""; 
        String[] words = oldstring.split(" "); 
        
        for(String s : words){
            int lastindex = formattedString.lastIndexOf("\n"); 
            if(lastindex < 0) lastindex = 0; 
            if(formattedString.substring(lastindex).length() + s.length() >= MaxLineLength){
                formattedString+="\n"+s+" ";
            }else{
                formattedString+=s+" "; 
            }
        }
    return formattedString;
    }

	/**
	 * Sets the HUD's position to{@code (x,y)} & All its buttons get translated to match its current setup
	 * @param x
	 * @param y
	 */
	public void translateTo(float x, float y){
        float dx = this.x-x;
        float dy = this.y-y;
		this.x-=dx;
		this.y-=dy;

		if(buttons !=null) 
		for(Button b : buttons){
			b.translate(dx, dy);
		}
    }

	public void translate(float dx, float dy){
		this.x+=dx;
		this.y+=dy; 

		if(buttons !=null) 
		for(Button b : buttons){
			b.translate(dx, dy);
		}
	}

	/**
	 * Moves the HUD & ALL ASSOCIATED BUTTONS to the point {@code (x,y)} on the screen. DOES NOT PRESERVE THEIR ORIGINAL ORIENTATION
	 * @see translateTo(float x, float y)
	 * @param x
	 * @param y
	 */
	public void moveTo(float x, float y){
        this.x=x;
        this.y=screenheight-y; 
		if(buttons !=null)
		for(Button b : buttons){
			b.x = x;
			b.y = screenheight-y; 
		}
    }

	public void dispose(){
		if(buttons != null){
		for(Button b : buttons){
			b.dispose();
		}
		buttons = null;
	}
		this.type = null; 

	}

	public hudtype getType(){
		return type;
	}

}
