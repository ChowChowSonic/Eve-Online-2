package my.gdx.game.entities;

import com.badlogic.gdx.math.Vector3;

import my.gdx.game.inventory.Shipclass;

public class KillableEntity extends Entity{
    protected int shields, armor, hull;
    protected final int maxshields, maxarmor, maxhull;
    protected float totalDeltaTime = 0;
    
    public KillableEntity(Vector3 position, String modelname, EntityType type, float size, long id) {
        super(position, modelname, type, size, id);
        maxshields = 1000;
        shields = maxshields; 
        maxarmor = 750;
        armor = maxarmor; 
        maxhull = 500; 
        hull = maxhull; 
        //TODO Auto-generated constructor stub
    }
    
    public KillableEntity(String modelname, EntityType type, float size, long ID) {
        super(modelname, type, size, ID); 
        maxshields = 1000;
        shields = maxshields; 
        maxarmor = 750;
        armor = maxarmor; 
        maxhull = 500; 
        hull = maxhull; 
    }

    public KillableEntity(Shipclass shiptype, EntityType type, long ID) {
        super(shiptype.getModelName(), type, shiptype.getSize(), ID); 
        maxshields = shiptype.getMaxShields();
        maxarmor = shiptype.getMaxArmor();
        maxhull = shiptype.getMaxhull();
        shields = shiptype.getMaxShields();
        armor = shiptype.getMaxArmor();
        hull = shiptype.getMaxhull();
    }
    
    public void update(float deltaTime){
        totalDeltaTime += deltaTime;
        if (totalDeltaTime > 5.0f) {
            totalDeltaTime -= 5;
            if (this.shields < this.maxshields - 20)
            this.shields += 20;
            else
            this.shields = maxshields;
        }
        super.update(deltaTime);
    }
    
    public void dealDamage(int damage) {
        if (damage > this.shields) {
            damage -= shields;
            shields = 0;
        } else {
            shields -= damage;
            return;
        }
        if (damage > this.armor) {
            damage -= armor;
            armor = 0;
        } else {
            armor -= damage;
            return;
        }
        // Most people: Implements a way to die
        // Me, an intellectual:
        if (damage >= this.hull) {
            System.exit(0);
        } else {
            hull -= damage;
            return;
        }
    }
    
    public void updateEntityFromSerialized(Entity serialEntity){
        super.updateEntityFromSerialized(serialEntity);
        if(serialEntity instanceof KillableEntity){
            KillableEntity p = (KillableEntity) serialEntity; 
            this.shields = p.shields;
            this.armor = p.armor;
            this.hull = p.hull;
        }
        
    }
    
    public int getShields() {
        return shields;
    }
    
    public void setShields(int shields) {
        this.shields = shields;
    }
    
    public int getArmor() {
        return armor;
    }
    
    public void setArmor(int armor) {
        this.armor = armor;
    }
    
    public int getHull() {
        return hull;
    }
    
    public void setHull(int hull) {
        this.hull = hull;
    }
    
    public int getMaxshields() {
        return maxshields;
    }
    
    public int getMaxarmor() {
        return maxarmor;
    }
    
    public int getMaxhull() {
        return maxhull;
    }
    
}
