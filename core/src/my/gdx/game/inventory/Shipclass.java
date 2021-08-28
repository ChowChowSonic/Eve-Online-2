package my.gdx.game.inventory;

public enum Shipclass {
    Apollyon(8, 5, 8, 500, 600, 500, 1, 100,"ship.obj");

    private int guns, defenses, suppliments, shields, armor, hull, size, invsize; 
    private String modelname; 
    Shipclass(int gunSlots, int defenseSlots, int supplimentSlots, int maxshields, int maxarmor, int maxhull, int ShipSize, int inventorySize, String modelName){
        guns = gunSlots;
        defenses = defenseSlots;
        suppliments = supplimentSlots;
        shields = maxshields; armor = maxarmor;
        hull = maxhull; 
        size = ShipSize; 
        invsize = inventorySize; 
        modelname = modelName; 
    }

    public String getModelName(){
        return modelname;
    }
    public int getGunSlots(){
        return guns; 
    }
    public int getDefenseSlots(){
        return defenses; 
    }
    public int getSupplimentSlots(){
        return suppliments; 
    }
    public int getMaxShields(){
        return shields; 
    }
    public int getMaxArmor(){
        return armor; 
    }
    public int getMaxhull(){
        return hull; 
    }
    public int getSize(){
        return size; 
    }
    public int getInventorySize(){
        return invsize; 
    }
    public InventoryItems toItemTemplate(){
        return InventoryItems.valueOf(this.name()); 
    }
    public Item toItemStack(){
        return new Item(this.toItemTemplate()); 
    }
}
