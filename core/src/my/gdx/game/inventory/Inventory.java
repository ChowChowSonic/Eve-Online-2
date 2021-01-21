package my.gdx.game.inventory;

import java.util.ArrayList;

public class Inventory {
	private float weight, occupiedspace, capacity, itemcount;
	ArrayList<Item> items; 	
	/**
	 * create a new, empty inventory of specified size
	 * @param size
	 */
	public Inventory(float size) {
		weight = 0; 
		this.capacity = size; 
		occupiedspace = 0; 
		items = new ArrayList<Item>();
		this.itemcount = 0;
	}
	
	/**
	 * Create a new inventory of specified size with specified items in it. 
	 * @param items
	 * @param size
	 */
	public Inventory(ArrayList<Item> items, float size) {
		this.items = items; 
		weight = 0; 
		this.capacity = size; 
		occupiedspace = 0; 
		for(Item item : items) {
			occupiedspace += (item.getVolume()*item.getStacksize());
			weight += (item.getWeight()*item.getStacksize());
			this.itemcount+= item.getStacksize(); 
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
			this.itemcount+= i.getStacksize(); 
			return true;
		}
		return false;
	}
	
	/**
	 * Tries to add multiple items to the inventory
	 * @param inv
	 * @return
	 */
	public void additem(ArrayList<Item> inv) {
		for(Item i : inv) {
			if(this.occupiedspace + (i.getVolume()*i.getStacksize()) <= this.capacity) {
				for(Item e : this.items) {
					if(e.getName().equals(i.getName())) {
						e.combinestack(i.getStacksize());
					}else {
						items.add(i);
					}
				}
				this.weight += i.getWeight()*i.getStacksize();
				this.occupiedspace += i.getVolume()*i.getStacksize();
				this.itemcount+= i.getStacksize(); 
			}
		}
	}
	
	/**
	 * Tries to add one item of specified type to the inventory
	 * @param i
	 * @return
	 */
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
			this.itemcount+= 1; 
			return true;
		}
		return false;
	}
	
	/**
	 * Adds a stack of a specified item to the inventory. 
	 * @param i
	 * @param stacksize
	 * @return
	 */
	public boolean additem(InventoryItems i, int stacksize) {
		Item newitem = new Item(i, stacksize);
		if(this.occupiedspace + (newitem.getVolume()) <= this.capacity) {
			for(Item e : this.items) {
				if(e.getName().equals(newitem.getName())) {
					e.combinestack(newitem.getStacksize());
					return true;
				}
			}
			items.add(newitem);
			this.weight += i.getWeight()*newitem.getStacksize();
			this.occupiedspace += newitem.getVolume()*newitem.getStacksize();
			this.itemcount+= stacksize; 
			return true;
		}
		return false;
	}
	
	/**
	 * Emptys the inventory of all items, sets the volume to 0 and weight to 0
	 */
	public void empty() {
		this.items = new ArrayList<Item>();
		this.weight = 0;
		this.itemcount = 0; 
		this.occupiedspace = 0; 
	}
	
	public boolean contains(Item i) {
		for(Item e : this.items) {
			if(e.getName().equals(i.getName())) return true;
		}
		
		return false;
		
	}
	public void removeItem(InventoryItems item, int amount) {
		for(Item e : this.items) {
			if(e.getTemplate() == item) {
				e.dropItem(amount);
			}
		}
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
		int tmpct = 0;
		for(Item i : this.items) {
			tmpct+=i.getStacksize(); 
		}
		itemcount = tmpct;
		return itemcount;
	}
	public ArrayList<Item> getItems() {
		return items;
	}
}
