package my.gdx.game.entities;

import java.io.Serializable;

import com.badlogic.gdx.math.Vector;

public class Vector3 extends com.badlogic.gdx.math.Vector3 implements Serializable{
    public float x, y, z; 
    
    public Vector3(float x, float y, float z){
        super(x, y, z); 
        this.x = x; this.y = y; this.z = z; 
    }
    
    public Vector3() {
        super(0,0,0); x=0; y=0; z=0; 
    }
    
    public Vector3(Vector3 pos) {
        super(pos.x, pos.y,pos.z); 
        x=pos.x; y=pos.y;z=pos.z;
    }
    public Vector3 nor(){
        return (Vector3) super.nor(); 
    }

    public Vector3 cpy(){
        return new Vector3(x,y,z); 
    }

    public Vector3 add(Vector3 e){
        return (Vector3) super.add(e.x, e.y, e.z); 
    }

    public Vector3 add(float x, float y, float z){
        return (Vector3) super.add(x, y, z); 
    }

    public Vector3 setZero(){
        this.x = 0; this.y = 0; this.z = 0;
        super.setZero(); 
        return this;
    }
    
}
