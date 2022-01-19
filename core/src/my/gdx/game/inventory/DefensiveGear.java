package my.gdx.game.inventory;

public enum DefensiveGear {
    shieldBooster(0, 0, 50); 
    private int cycle, repair, boost;

    DefensiveGear(int cycleTime, int RepairAmount, int BoostAmount){
        cycle = cycleTime;
        repair=RepairAmount;
        boost=BoostAmount;
    }
    public int getCycleTime(){
        return cycle;
    }
    public int getRepairAmount(){
        return repair;
    }
    public int getBoostAmount(){
        return boost;
    }
    
}
