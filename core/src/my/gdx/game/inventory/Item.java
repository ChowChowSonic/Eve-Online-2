package my.gdx.game.inventory;

import java.util.ArrayList;
/**
 * A wrapper/utility class for the InventoryItems enum.
 * @author dmcdc
 *
 */
public class Item {
	private InventoryItems blueprint;
	private int stacksize = 1; 
	
	public Item(InventoryItems itemtype) {
		this.blueprint = itemtype; 
	}
	public Item(InventoryItems itemtype, int stacksize) {
		this.blueprint = itemtype; 
		this.stacksize = stacksize; 
	}
	
	public int getStacksize() {
		return stacksize;
	}
	public void combinestack(int number) {
		stacksize+=number; 
	}
	public Item splitstack(int secondstack) {
		secondstack = Math.abs(secondstack);
		if(secondstack >= stacksize) return this; 
		else {
			this.stacksize-=secondstack; 
			return new Item(this.blueprint, secondstack);
		}
	}
	public String getName() {
		return blueprint.getName();
	}
	public float getWeight() {
		return blueprint.getWeight();
	}
	public float getVolume() {
		return blueprint.getVolume(); 
	}
	public ArrayList<InventoryItems> getComponents() {
		return blueprint.getComponents();
	}
}
