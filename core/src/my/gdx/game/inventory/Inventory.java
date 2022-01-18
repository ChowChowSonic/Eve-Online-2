package my.gdx.game.inventory;

import java.io.Serializable;
import java.util.ArrayList;

public class Inventory implements Serializable {
	private float weight, occupiedspace, capacity, itemcount;
	private static final long serialVersionUID = 1L;
	ArrayList<Item> items;

	/**
	 * create a new, empty inventory of specified size
	 * 
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
	 * 
	 * @param items
	 * @param size
	 */
	public Inventory(ArrayList<Item> items, float size) {
		this.items = new ArrayList<Item>(items);
		weight = 0;
		this.capacity = size;
		occupiedspace = 0;
		for (Item item : items) {
			occupiedspace += (item.getVolume() * item.getStacksize());
			weight += (item.getWeight() * item.getStacksize());
			this.itemcount += item.getStacksize();
		}
	}

	/**
	 * Copies the selected inventory
	 * 
	 * @param i
	 */
	public Inventory(Inventory i) {
		this.items = new ArrayList<Item>();
		for (Item e : i.getItems()) {
			this.items.add(new Item(e));
		}
		weight = i.getWeight();
		this.capacity = i.getCapacity();
		occupiedspace = i.getOccupiedspace();
		this.itemcount = i.getItemcount();
	}

	/**
	 * Tries to add an item to the inventory. If there isn't enough space it fails
	 * and returns false. It will return True otherwise
	 * 
	 * @param i
	 * @return whether or not the item was added to the inventory.
	 */
	public boolean additem(Item i) {

		if (i.getStacksize() <= 0) {
			return false;
		}
		if (this.getOccupiedspace() + i.getVolume() <= this.capacity) {
			if (this.containsItemType(i)) {
				for (Item e : this.items) {
					if (i.getName().equals(e.getName())) {
						e.combinestack(i.getStacksize());
						this.weight += i.getWeight();
						this.occupiedspace += i.getVolume();
						this.itemcount += i.getStacksize();
						break;
					}
				}
				verifycontents();
				return true;
			}
			items.add(new Item(i));
			this.weight += i.getWeight() * i.getStacksize();
			this.occupiedspace += i.getVolume() * i.getStacksize();
			this.itemcount += i.getStacksize();
			verifycontents();
			return true;
		}
		return false;
	}

	/**
	 * Tries to add multiple items to the inventory
	 * 
	 * @param inv
	 * @return
	 */
	public void additem(ArrayList<Item> inv) {
		verifycontents();
		for (int i =0; i < inv.size(); i++) {
			Item item = inv.get(i); 
			if (item.getStacksize() > 0) {
				this.additem(item.getTemplate(), item.getStacksize());
			}
		}
	}

	/**
	 * Tries to add one item of specified type to the inventory
	 * 
	 * @param i
	 * @return
	 */
	public boolean additem(InventoryItems i) {
		verifycontents();
		Item newitem = new Item(i);
		if (this.occupiedspace + (newitem.getVolume() * newitem.getStacksize()) <= this.capacity) {
			for (Item e : this.items) {
				if (e.getName().equals(newitem.getName())) {
					e.combinestack(newitem.getStacksize());
					return true;
				}
			}
			items.add(newitem);
			this.weight += i.getWeight() * newitem.getStacksize();
			this.occupiedspace += newitem.getVolume() * newitem.getStacksize();
			this.itemcount += 1;
			return true;
		}
		return false;
	}

	/**
	 * Adds a stack of a specified item to the inventory.
	 * 
	 * @param i
	 * @param stacksize
	 * @return
	 */
	public boolean additem(InventoryItems i, int stacksize) {
		verifycontents();
		if (stacksize <= 0) {
			return false;
		}
		Item newitem = new Item(i, stacksize);
		if (this.occupiedspace + (newitem.getVolume()) <= this.capacity) {
			for (Item e : this.items) {
				if (e.getName().equals(newitem.getName())) {
					e.combinestack(newitem.getStacksize());

					return true;
				}
			}
			items.add(newitem);
			this.weight += i.getWeight() * newitem.getStacksize();
			this.occupiedspace += newitem.getVolume() * newitem.getStacksize();
			this.itemcount += stacksize;

			return true;
		}
		return false;
	}

	public boolean transferInventoryItemTo(Inventory i, Item item) {
		//System.out.println(this.toString() +"\n"+ i.toString());
		if (this.containsWithQuantity(item) && i.hasRoomFor(item)) {
			if (i.additem(item)) {
				this.removeItem(item);
				return true;
			}
		} else if (!i.hasRoomFor(item)) {
			for (int i2 = item.getStacksize(); i2 != 1; i2--) {
				if (i.hasRoomFor(new Item(item.getTemplate(), i2)) && i.additem(item.splitstack(i2))) {
					this.removeItem(item.getTemplate(), i2);
					return true;
				}

			}
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

	public boolean containsItemType(Item i) {
		verifycontents();
		for (Item e : this.items) {
			if (e.getName().equals(i.getName()))
				return true;
		}
		return false;
	}

	public boolean containsWithQuantity(Item i) {
		verifycontents();
		for (int I =0; I < items.size(); I++) {
			Item e = items.get(I); 
			if (e.getName().equals(i.getName()) && e.getStacksize() >= i.getStacksize())
				return true;
		}

		return false;

	}

	public void removeItem(InventoryItems item, int amount) {
		for (Item e : this.items) {
			if (e.getName().equals(item.getName())) {
				e.dropItem(amount);
				verifycontents();
				break;
			}
		}
	}

	public void removeItem(Item i) {
		for (int g = 0; g < items.size(); g++) {
			Item e = items.get(g);
			if (e.equals(i)) {
				e.dropItem(i.getStacksize());
				verifycontents();
				break;
			}
		}
	}

	public float getWeight() {
		weight = 0;
		verifycontents();
		for (Item i : this.items) {
			weight += i.getWeight();
		}
		return weight;
	}

	public float getOccupiedspace() {
		occupiedspace = 0;
		verifycontents();
		for (Item i : this.items) {
			occupiedspace += i.getVolume();
		}
		return occupiedspace;
	}

	public boolean hasRoomFor(Item i) {
		return this.getOccupiedspace() + i.getVolume() <= this.capacity;
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
		for (Item i : this.items) {
			itemcount += i.getStacksize();
		}
		return itemcount;
	}

	public ArrayList<Item> getItems() {
		verifycontents();
		return items;
	}

	/**
	 * Returns a list of items that are not present in both inventories. Will only
	 * return items in the paramater inventory. Example:
	 * <p>
	 * inv1 = [yeet, yote, yaint]
	 * <p>
	 * inv2 = [yeet, yaint]
	 * <p>
	 * inv1.getDifferences(inv2) >> []
	 * <p>
	 * inv2.getDifferences(inv1) >> [yote]
	 * 
	 * @return
	 */
	public ArrayList<Item> getDifferences(Inventory comparator) {
		this.verifycontents();
		comparator.verifycontents();
		Inventory compcopy = new Inventory(comparator);
		for (Item i : this.items) {
			if (compcopy.containsItemType(i))
				compcopy.removeItem(i.getTemplate(), i.getStacksize());
		}
		return compcopy.items;
	}

	@Override
	public String toString() {
		String str = "max size: " + this.capacity + "\n";
		for (Item i : this.items) {
			str += i.toString() + "\n";
		}
		return str;

	}

	/**
	 * Finds a specific stack of items
	 * 
	 * @param i
	 * @return
	 */
	public Item findItemByType(InventoryItems i) {
		for (Item i2 : this.items) {
			if (i2.getName().equals(i.getName())) {
				return new Item(i2);
			}
		}
		return null;
	}

	/**
	 * Ensures that all items in the inventory are of positive, non-zero quantity.
	 * (and removes any that aren't)
	 */
	private void verifycontents() {
		ArrayList<Item> removals = new ArrayList<Item>();
		for (int i = 0; i < items.size(); i++) {
			Item e = items.get(i);
			if (e.getStacksize() <= 0) {
				removals.add(e);
			}
		}
		for (Item i : removals) {
			items.remove(i);
		} // */
	}// ends VerifyContents

}// ends class
