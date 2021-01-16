package my.gdx.game.inventory;

import java.util.ArrayList;

public class Inventory {
	private float weight, occupiedspace, capacity, itemcount;
	ArrayList<Item> items; 
	public Inventory(float size) {
		weight = 0; 
		this.capacity = size; 
		occupiedspace = 0; 
		items = new ArrayList<Item>();
	}
	public Inventory(ArrayList<Item> items, float size) {
		this.items = items; 
		weight = 0; 
		this.capacity = size; 
		occupiedspace = 0; 
		for(Item item : items) {
			occupiedspace += (item.getVolume()*item.getStacksize());
			weight += (item.getWeight()*item.getStacksize());
		}
	}
	
	/**
	 * Tries to add an item to the inventory. If there isn't enough space it fails and returns false
	 * True otherwise
	 * @param i
	 * @return whether or not the item was added to the inventory. 
	 */
	public boolean additem(Item i) {
		if(this.occupiedspace + (i.getVolume()*i.getStacksize()) <= this.capacity) {
			for(Item e : this.items) {
				if(e.getName().equals(i.getName())) {
					e.combinestack(i.getStacksize());
					return true;
				}
			}
		items.add(i);
		this.weight += i.getWeight()*i.getStacksize();
		this.occupiedspace += i.getVolume()*i.getStacksize();
		return true;
		}
		return false;
	}
	public boolean additem(InventoryItems i) {
		Item newitem = new Item(i);
		if(this.occupiedspace + (newitem.getVolume()*newitem.getStacksize()) <= this.capacity) {
			for(Item e : this.items) {
				if(e.getName().equals(newitem.getName())) {
					e.combinestack(newitem.getStacksize());
					return true;
				}
			}
		items.add(newitem);
		this.weight += i.getWeight()*newitem.getStacksize();
		this.occupiedspace += newitem.getVolume()*newitem.getStacksize();
		return true;
		}
		return false;
	}
	public boolean additem(InventoryItems i, int stacksize) {
		Item newitem = new Item(i, stacksize);
		if(this.occupiedspace + (newitem.getVolume()*newitem.getStacksize()) <= this.capacity) {
			for(Item e : this.items) {
				if(e.getName().equals(newitem.getName())) {
					e.combinestack(newitem.getStacksize());
					return true;
				}
			}
		items.add(newitem);
		this.weight += i.getWeight()*newitem.getStacksize();
		this.occupiedspace += newitem.getVolume()*newitem.getStacksize();
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
	public ArrayList<Item> getItems() {
		return items;
	}
}
