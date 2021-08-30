package my.gdx.game.Hud;

import java.nio.ByteBuffer;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;

import my.gdx.game.entities.Entity;

public class TargetButton extends Button{
    private Entity target; 
    private Texture img; 

    public TargetButton(float xpos, float ypos, Entity e) {
        super(xpos, ypos, 110, 110);
        this.target = e;
        
        //Build a new circular image from the origin file
        Pixmap p = new Pixmap(Gdx.files.internal(target.getModelName().replace(".obj", ".png"))); //load origin file into pixmap (Does not have alpha)
        int size = 400;
        Pixmap newmap = new Pixmap(size, size, Format.RGBA8888); //Load new pixmap of a better size (DOES have alpha)
        newmap.drawPixmap(p, 0, 0, (p.getWidth()-size)/2, (p.getHeight()-size)/2, size, size); //Cut the center out of the old pixmap, and load onto the new one.
        p.dispose();//dispose of the old one to save memory
        int totalpixels = size*size;
        int ypixel = size; 
        int xpixel = 0;
        newmap.setBlending(Pixmap.Blending.None);
        newmap.setColor(Color.CLEAR);
        for(int i = 0; i < totalpixels; i++){
            int dx = (xpixel) - (size/2);
            int dy = (ypixel) - (size/2); 
            if(Math.sqrt((dx*dx)+(dy*dy)) > size/2){
                newmap.drawPixel(xpixel, ypixel); //Make pixels that are too far from the center invisible, hence why we needed the alpha
                //This will result in a circular shape for the image
            }
            if(ypixel > 0) {
                ypixel--;
            }else{
                ypixel = size;
                xpixel++;
            }
        }
        img = new Texture(newmap); // load the pixmap into a new texture
        newmap.dispose();
     }
    
    public boolean equals(Object o){
        if(o instanceof TargetButton){
            TargetButton e = (TargetButton) o;
            return e.target.equals(this.target); 
        }
        return false; 
    }
    
    public boolean isInBounds(float x, float y){
        float dx = this.x - x;
        float dy = this.y - y;
        return Math.sqrt((dx*dx)+(dy*dy)) <= this.height/2;
        
    }

    public Entity getEntity(){
        return this.target; 
    }
    
    public void updateText(){
        textrenderer.draw(img, this.x-(this.width/2), screenheight-(y+height/2), this.width, this.height);
    }
    
    @Override
    public void interact(float x, float y) {
        // TODO Auto-generated method stub
        
    }
    @Override
    public void dispose(){
        img.dispose();
        img = null;
        target = null;
    }
    
}
