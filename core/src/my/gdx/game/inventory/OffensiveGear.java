package my.gdx.game.inventory;

public enum OffensiveGear {
    gun(5, 10, 2);
    private int range, damage, cycle; 

    OffensiveGear(int rangeinKM, int damage, int cycleInSEC){
        range=rangeinKM;
        this.damage=damage;
        cycle=cycleInSEC; 
    }

    public int getRange(){
        return range;
    }

    
    public int damagePerShot(){
        return damage;
    }
    
    public int getCycleTime(){
        return cycle;
    }
}
