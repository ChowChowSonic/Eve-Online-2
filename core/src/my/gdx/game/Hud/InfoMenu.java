package my.gdx.game.Hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import my.gdx.game.EveOnline2;

public class InfoMenu extends Hud{
    private String[] text;
    private Texture[] images; 
    private int activepanel = 0, panels; 
    private PagerButton Lbutton, Rbutton; 

    /**
     * Creates a menu screen that presents the player with a slideshow of helpful images and detailing "readers notes". 
     * Each image in the array is presented with the correlated text in the order it is in.  
     * Each array must be of the same size (imageNAME.length) in order to work properly. <p>
     * So imageNAME[0] is presented alongside TEXT[0]; and both are presented on panel number 1; <p>
     * ...imageNAME[1] is presented alongside TEXT[1]; and both are presented on panel number 2; <p>
     * ... and so on and so forth up until you have passed through exactly imageNAME.length amount of slides.  
     * @param imageNAME - the name of the images to go on the infomenu - all put into an array in order. 
     * @param TEXT - The text that goes with it
     */
    public InfoMenu(String[] imageNAME, String[] TEXT){
        super(screenwidth/2, screenheight/2+60, 0.35f*screenwidth, 0.85f*screenheight, hudtype.Infomenu);
        text = new String[imageNAME.length]; 
        images = new Texture[imageNAME.length];
        for(int i = 0; i < imageNAME.length; i++){
            images[i] = new Texture(EveOnline2.assetFolder.child(imageNAME[i])); 
        }
        text = TEXT; 
        panels = imageNAME.length; 
        Lbutton = new PagerButton(x-width/4, screenheight-(y-height/2+30), "< < <", width/2-10); 
        Rbutton = new PagerButton(x+width/4, screenheight-(y-height/2+30), "> > >", width/2-10); 
        for(int i = 0; i < panels; i++){
            text[i] = correctAlignment(text[i], (int) (width/(font.getSpaceXadvance()+2))-10); 
            System.out.println((int)0.35f*screenwidth);
        }
    }

    /**
     * Creates a menu screen that presents the player with a slideshow of helpful images and detailing "readers notes". 
     * */
    public InfoMenu(String Imagename, String TEXT){
        super(screenwidth/2, screenheight/2, 700, 900, hudtype.Infomenu); 
        this.type = hudtype.Infomenu; 
        this.text = new String[1]; 
        text[0] = TEXT; 
        images = new Texture[1];
        images[0] = new Texture(Imagename); 
        panels = 1; 
        Lbutton = new PagerButton(x-width/4, screenheight-(y-height/2+30), "< < <", width/2-10); 
        Rbutton = new PagerButton(x+width/4, screenheight-(y-height/2+30), "> > >", width/2-10); 
        for(int i = 0; i < panels; i++){
            text[i] = correctAlignment(text[i], 110); 
        }
    }

    public void updateShape(){
        super.updateShape();
        renderer.setColor(Color.ROYAL);
        renderer.rect(x-width/2, y-height/2, width, height);
    
        Lbutton.updateShape();
        Rbutton.updateShape();
        if(Lbutton.getInput()){
            activepanel--; 
        }else if(Rbutton.getInput()){
            activepanel++;
        }
        if(activepanel >= panels || activepanel < 0) EveOnline2.removeHUD(this);
    }

    public void updateText(){
        super.updateText();
        float imgscaleFactor = 2; 
        textrenderer.draw(images[activepanel], x-width/2+10, y-10, width-20, height/imgscaleFactor);
        font.getData().setScale(0.975f);
        font.setColor(Color.WHITE);
        font.draw(textrenderer, this.text[activepanel], x-width/2+10, y-20);
        if(activepanel == panels-1)Rbutton.setText("Close");
        else {Rbutton.setText("> > >");}
        if(activepanel == 0)Lbutton.setText("Back");
        else{Lbutton.setText("< < <");}
        Lbutton.updateText();
        Rbutton.updateText();
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
    public void interact(float x, float y) {
        if(Lbutton.isInBounds(x, y))Lbutton.interact(x, y);
        else if(Rbutton.isInBounds(x, y))Rbutton.interact(x, y); 
        
    } 

    public void dispose(){
        super.dispose();
        for(Texture t : images){
            t.dispose();
        }
        Lbutton.dispose();
        Rbutton.dispose();
        images = null;
        text = null;
    }
    
}
