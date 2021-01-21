package my.gdx.game.inventory;

import java.util.ArrayList;

public enum InventoryItems {
	Jimbabwe_Shipping_Crates("Jimbabwe Shipping Crates", 10, 1),
	Iron("Chunk of Iron", 7.874f, 1), 
	Cobalt("Chunk of Cobalt", 8.900f, 1),
	Nickel("Chunk of Cobalt", 8.908f, 1),
	Silver("Chunk of Silver", 10.490f, 1), 
	Gold("Chunk of Gold", 19.300f, 1),
	Platinum("Chunk of Platinum", 21.090f, 1),
	Palladium("Chunk of Palladium", 12.023f, 1),
	Copper("Chunk of Copper", 8.920f, 1),
	Tantalum("Chunk of Tantalum", 16.650f, 1), 
	Aluminum("Chunk of Aluminum", 2.700f, 1), 
	Tin("Chunk of Tin", 7.310f, 1),
	Zinc("Chunk of Zinc", 7.140f, 1),
	Neodymium("Chunk of Neodymium", 7.010f, 1),
	Odor_Blocker_Bodywash("Odor Blocker Bodywash", 1, 5);
	
	private String name;
	private float weight;
	private float size;
	private ArrayList<InventoryItems> components;
	
	InventoryItems(String name, float MetricTons, float cubicm3) {
		this.name = name;
		this.weight = MetricTons; 
		this.size = cubicm3;
	}
	
	InventoryItems(String name, float MetricTons, float cubicm3, ArrayList<InventoryItems> components) {
		this.name = name;
		this.weight = MetricTons; 
		this.size = cubicm3;
		this.components = components;
	}
	
	public String getName() {
		return name;
	}
	public float getWeight() {
		return weight;
	}
	public float getVolume() {
		return size; 
	}
	public ArrayList<InventoryItems> getComponents() {
		return components;
	}
}
