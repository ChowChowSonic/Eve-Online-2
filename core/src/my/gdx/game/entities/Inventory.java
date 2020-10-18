package my.gdx.game.entities;

import java.util.ArrayList;

public class Inventory {
	private float weight, occupiedspace, capacity, itemcount;
	ArrayList<InventoryItem> items; 
	public Inventory(float size) {
		weight = 0; 
		this.capacity = size; 
		occupiedspace = 0; 
		items = new ArrayList<InventoryItem>();
	}
	public Inventory(ArrayList<InventoryItem> items, float size) {
		this.items = items; 
		weight = 0; 
		this.capacity = size; 
		occupiedspace = 0; 
		for(InventoryItem item : items) {
			occupiedspace += (item.getSize()*item.getStacksize());
			weight += (item.getWeight()*item.getStacksize());
		}
	}
	
	/**
	 * Tries to add an item to the inventory. If there isn't enough space it fails and returns false
	 * True otherwise
	 * @param i
	 * @return whether or not the item was added to the inventory. 
	 */
	public boolean additem(InventoryItem i) {
		if(this.occupiedspace + (i.getSize()*i.getStacksize()) < this.capacity) {
		items.add(i);
		this.weight += i.getWeight()*i.getStacksize();
		this.occupiedspace += i.getSize()*i.getStacksize();
		return true;
		}
		return false;
	}
	public float getWeight() {
		return weight;
	}
	public float getOccupiedspace() {
		return occupiedspace;
	}
	public float getCapacity() {
		return capacity;
	}
	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}
	public float getItemcount() {
		return itemcount;
	}
	public ArrayList<InventoryItem> getItems() {
		return items;
	}
}
