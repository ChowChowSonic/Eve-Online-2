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
	
	public Item(Item newitem) {
		// TODO Auto-generated constructor stub
		this.blueprint = newitem.getTemplate(); 
		this.stacksize = newitem.getStacksize(); 
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
	
	public void dropItem(int amount) {
		this.stacksize-=amount; 
	}
	public String getName() {
		return blueprint.getName();
	}
	public InventoryItems getTemplate() {
		return blueprint; 
	}
	public float getWeight() {
		return blueprint.getWeight()*this.stacksize;
	}
	public float getVolume() {
		return blueprint.getVolume()*this.stacksize; 
	}
	public ArrayList<InventoryItems> getComponents() {
		return blueprint.getComponents();
	}
	@Override
	public String toString() {
		return this.getName() +" x"+ this.getStacksize() +": " +this.getVolume()+" m3, "+this.getWeight()+" kg";
		
	}
}
