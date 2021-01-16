package my.gdx.game.inventory;

import java.util.ArrayList;

public enum InventoryItems {
	Jimbabwe_Shipping_Crates("Jimbabwe Shipping Crates", 50, 10),
	Odor_Blocker_Bodywash("Odor Blocker Bodywash", 1, 5);
	
	private String name;
	private float weight;
	private float size;
	private ArrayList<InventoryItems> components;
	
	InventoryItems(String name, float kg, float cubicm3) {
		this.name = name;
		this.weight = kg; 
		this.size = cubicm3;
	}
	InventoryItems(String name, float kg, float cubicm3, ArrayList<InventoryItems> components) {
		this.name = name;
		this.weight = kg; 
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
