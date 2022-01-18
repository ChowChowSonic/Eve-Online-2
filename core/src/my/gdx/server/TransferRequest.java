package my.gdx.server;

import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.Item;

public class TransferRequest {
    private Inventory from, to; 
    private Item item; 

    /**
     * Creates a new wrapper class, responsible for transferring items between two inventories. In the Server.java class, these will be but into a queue and frequently fufilled. 
     * @param from
     * @param obj
     * @param to
     */
    public TransferRequest(Inventory from, Item obj, Inventory to){
        this.from = from;
        this.to = to; 
        this.item = obj; 
    }
    /**
     * Actually tries to fufill the order (if possible) and returns whether or not it went through. 
     * @return true, if the items were successfully transferred; false otherwise
     */
    public boolean fufill(){
        if(from.containsWithQuantity(item) && to.hasRoomFor(item)){
            from.transferInventoryItemTo(to, item); 
            return true; 
        }
        return false; 
    }
    /**
     * Returns whether or not this TransferRequest is able to be fufilled. HOWEVER THIS METHOD DOES NOT ACTUALLY FUFILL THE REQUEST
     * @return from.containsWithQuantity(item) && to.hasRoomFor(item)
     */
    public boolean canFufill(){
        return from.containsWithQuantity(item) && to.hasRoomFor(item); 
    }

    public String toString(){
        return "[Size:" + from.getCapacity() + " Occupied:"+from.getOccupiedspace()+"] -> "+String.format("[%s x%s %.1fm3]", item.getName(), item.getStacksize(), item.getVolume())+" -> [Size:" + to.getCapacity() + " Occupied:"+to.getOccupiedspace()+"]"; 
    }
}
