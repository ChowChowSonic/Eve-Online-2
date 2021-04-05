package my.gdx.server;

import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;

import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Entity.EntityType;

import com.badlogic.gdx.utils.JsonValue;

public class SerializedEntity implements Serializable{
    public static enum modelID{NASA_ISS, SHIP, ORB}; //protected Model model;
    public static enum textureID{SUN, BADLOGIC, SHIP, SKYBOX, SPACE}//protected ModelInstance instance; 
    protected modelID model;
    protected textureID texture;
    protected float x,y,z;
	protected EntityType type; 
	protected long ID; 

    private static final long serialVersionUID=1L;//I need this for some reason and I dont know why. 
    public SerializedEntity(Entity e){
        this.ID = e.getID();
        this.x = e.getPos().x;
        this.y = e.getPos().y;
        this.z = e.getPos().z;
        this.model = getIDbyModel(e.getModel());
        this.texture = getIDbyTexture(e.getModel().materials);
    }
    
    public SerializedEntity(modelID model, textureID texture, Vector3 position,EntityType type,long ID){
        this.ID = ID; 
        this.model = model;
        this.texture = texture;
        this.type = type; 
        x = position.x;
        y = position.y;
        z = position.z;
    }
    public SerializedEntity(Vector3 position,EntityType type,long ID){
        this.ID = ID; 
        this.type = type; 
        x = position.x;
        y = position.y;
        z = position.z;
    }
    public SerializedEntity(long ID, Vector3 position){
        this.ID = ID; 
        x = position.x;
        y = position.y;
        z = position.z;
    }

    /**
     * Return to this later... Returns the ship model no matter what. 
     * @param m
     * @return
     */
    private modelID getIDbyModel(Model m){
        return modelID.SHIP; 
    }
/**
     * Return to this later... Returns the ship texture no matter what. 
     * @param mats
     * @return
     */
    private textureID getIDbyTexture(Array<Material> mats){
        return textureID.SHIP;
    }

    @Override
    public void write(Json json) {
        // TODO Auto-generated method stub
        json.toJson(this);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        // TODO Auto-generated method stub
        json.readFields(SerializedEntity.class, jsonData);
    }

    public long getID(){
        return ID;
    }

    public Vector3 getpos() {
        return new Vector3(x,y,z);
    }

    public EntityType getType(){
        return type; 
    }
    
}
