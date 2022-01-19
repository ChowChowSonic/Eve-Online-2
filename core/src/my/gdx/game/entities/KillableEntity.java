package my.gdx.game.entities;

import com.badlogic.gdx.math.Vector3;

import my.gdx.game.inventory.DefensiveGear;
import my.gdx.game.inventory.OffensiveGear;
import my.gdx.game.inventory.Shipclass;

public class KillableEntity extends Entity {
    protected int shields, armor, hull;
    protected final int maxshields, maxarmor, maxhull;
    protected float totalDeltaTime = 0;
    protected OffensiveGear[] guns; 
    protected DefensiveGear[] defBoosts; 
    
    /**
     * depreciated constructor. Please dont use this. 
     * @param position
     * @param modelname
     * @param type
     * @param size
     * @param id
     */
    public KillableEntity(Vector3 position, String modelname, EntityType type, float size, long id) {
        super(position, modelname, type, size, id);
        maxshields = 1000;
        shields = maxshields;
        maxarmor = 750;
        armor = maxarmor;
        maxhull = 500;
        hull = maxhull;
        // TODO Auto-generated constructor stub
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
    
    public KillableEntity(Shipclass shiptype, EntityType type, long ID, DefensiveGear[] defensiveGears, OffensiveGear[] gunGear) {
        super(shiptype.getModelName(), type, shiptype.getSize(), ID);
        int maxshields = shiptype.getMaxShields();
        int maxarmor = shiptype.getMaxArmor();
        int maxhull = shiptype.getMaxhull();
        shields = shiptype.getMaxShields();
        armor = shiptype.getMaxArmor();
        hull = shiptype.getMaxhull();
        if(gunGear.length <= shiptype.getGunSlots()){
            guns = gunGear;
        }else{
            guns = new OffensiveGear[shiptype.getGunSlots()]; 
            for(int i = 0; i < guns.length; i++){
                guns[i] = gunGear[i]; 
            }
        }
        int x = Math.min(shiptype.getDefenseSlots(), defensiveGears.length); 
        defBoosts = new DefensiveGear[x];  
        for(int i = 0; i < x; i++){
            defBoosts[i] = defensiveGears[i]; 
            maxshields+= defensiveGears[i].getBoostAmount(); 
        }
        this.maxshields = maxshields;
        this.maxarmor = maxarmor;
        this.maxhull = maxhull; 
    }
    
    public void update(float deltaTime) {
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
    
    public void updateEntityFromSerialized(Entity serialEntity) {
        super.updateEntityFromSerialized(serialEntity);
        if (serialEntity instanceof KillableEntity) {
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
