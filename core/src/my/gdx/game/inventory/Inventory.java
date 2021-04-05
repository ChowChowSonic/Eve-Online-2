package my.gdx.game.inventory;

import java.util.ArrayList;

import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializable;

public class Inventory implements Serializable{
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
		this.items = new ArrayList<Item>(items); 
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
	* Copies the selected inventory
	* @param i
	*/
	public Inventory(Inventory i) {
		this.items = new ArrayList<Item>();
		for(Item e : i.getItems()) {
			this.items.add(new Item(e)); 
		}
		weight = i.getWeight(); 
		this.capacity = i.getCapacity(); 
		occupiedspace = i.getOccupiedspace();
		this.itemcount = i.getItemcount(); 
	}
	
	/**
	* Tries to add an item to the inventory. If there isn't enough space it fails and returns false
	* True otherwise
	* @param i
	* @return whether or not the item was added to the inventory. 
	*/
	public boolean additem(Item i) {
		
		if(i.getStacksize() <= 0) {
			return false; 
		} 
		if(this.occupiedspace + (i.getVolume()*i.getStacksize()) <= this.capacity) {
			if(this.contains(i)) {
				for(Item e : this.items) {
					if(i.getName().equals(e.getName())) {
						e.combinestack(i.getStacksize());
						this.weight += i.getWeight()*i.getStacksize();
						this.occupiedspace += i.getVolume()*i.getStacksize();
						this.itemcount+= i.getStacksize(); 
						break;
					}
				}
				verifycontents();
				return true;
			}
			items.add(new Item(i));
			this.weight += i.getWeight()*i.getStacksize();
			this.occupiedspace += i.getVolume()*i.getStacksize();
			this.itemcount+= i.getStacksize(); 
			verifycontents();
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
		verifycontents();
		for(Item i : inv) {
			if(i.getStacksize() > 0) {
				this.additem(i.getTemplate(), i.getStacksize()); 
			} 
		}
	}
	
	/**
	* Tries to add one item of specified type to the inventory
	* @param i
	* @return
	*/
	public boolean additem(InventoryItems i) {
		verifycontents();
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
		verifycontents();
		if(stacksize <= 0) {
			return false;  
		} 
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
		verifycontents();
		for(Item e : this.items) {
			if(e.getName().equals(i.getName())) return true;
		}
		
		return false;
		
	}
	public void removeItem(InventoryItems item, int amount) {
		for(Item e : this.items) {
			if(e.getName().equals(item.getName())) {
				e.dropItem(amount);
				verifycontents();
				break; 
			}
		}
	}
	
	public float getWeight() {
		weight = 0;
		verifycontents();
		for(Item i : this.items) {
			weight+= i.getWeight()*i.getStacksize(); 
		}
		return weight;
	}
	public float getOccupiedspace() {
		occupiedspace = 0;
		verifycontents();
		for(Item i : this.items) {
			occupiedspace+=i.getVolume()*i.getStacksize(); 
		}
		return occupiedspace;
	}
	public float getCapacity() {
		return capacity;
	}
	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}
	public float getItemcount() {
		int itemcount = 0;
		verifycontents();
		for(Item i : this.items) {
			itemcount+=i.getStacksize(); 
		}
		return itemcount;
	}
	public ArrayList<Item> getItems() {
		verifycontents();
		return items;
	}
	
	/**
	* Returns a list of items that are not present in both inventories.
	* Will only return items in the paramater inventory. Example: <p>
	* inv1 = [yeet, yote, yaint]<p>
	* inv2 = [yeet, yaint]<p>
	* inv1.getDifferences(inv2) >> []<p>
	* inv2.getDifferences(inv1) >> [yote]
	* @return
	*/
	public ArrayList<Item> getDifferences(Inventory comparator){
		this.verifycontents();
		comparator.verifycontents();
		Inventory compcopy = new Inventory(comparator);
		for(Item i : this.items) {
			if(compcopy.contains(i))
			compcopy.removeItem(i.getTemplate(), i.getStacksize());
		}
		return compcopy.items;
	}
	@Override
	public String toString() {
		String str = "max size: "+this.capacity+"\n"; 
		for(Item i : this.items) {
			str+=i.toString()+"\n"; 
		}
		return str;
		
	}
	/**
	* Finds a specific stack of items 
	* @param i
	* @return
	*/
	public Item findItemByType(InventoryItems i) {
		for(Item i2 : this.items) {
			if(i2.getName().equals(i.getName())) {
				return new Item(i2); 
			}
		}
		return null; 
	}
	/**
	* Ensures that all items in the inventory are of positive, non-zero quantity. (and removes any that aren't) 
	*/
	private void verifycontents() {
		ArrayList<Item> removals = new ArrayList<Item>(); 
		for(int i = 0; i < items.size(); i++) {
			Item e = items.get(i); 
			if(e.getStacksize() <= 0) {
				removals.add(e);
			}
		}
		for(Item i : removals) {
			items.remove(i); 
		}//*/
	}//ends VerifyContents

	@Override
	public void write(Json json) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void read(Json json, JsonValue jsonData) {
		// TODO Auto-generated method stub
		
	}
}//ends class
