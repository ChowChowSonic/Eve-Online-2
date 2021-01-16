package my.gdx.game.inventory;

import java.util.ArrayList;

public enum InventoryItems {
	Jimbabwe_Shipping_Crates("Jimbabwe Shipping Crates", 0, 10);
	
	private String name;
	private float weight;
	private float size;
	private ArrayList<InventoryItems> components;
	private int stacksize;
	
	InventoryItems(String name, float kg, float cubicm3) {
		this.name = name;
		this.weight = kg; 
		this.size = cubicm3;
		stacksize = 1; 
		components = new ArrayList<InventoryItems>();
	}
	
	InventoryItems(String name, float kg, float cubicm3, int stacksize) {
		this.name = name;
		this.weight = kg; 
		this.size = cubicm3;
		this.stacksize = stacksize; 
		components = new ArrayList<InventoryItems>();
	}
	
	InventoryItems(String name, float kg, float cubicm3, ArrayList<InventoryItems> components) {
		this.name = name;
		this.weight = kg; 
		this.size = cubicm3;
		this.components = components;
		stacksize = 1;
	}
	
	InventoryItems(String name, float kg, float cubicm3, int stacksize, ArrayList<InventoryItems> components) {
		this.name = name;
		this.weight = kg; 
		this.size = cubicm3;
		this.components = components;
		this.stacksize = stacksize;
	}
	
	public String getName() {
		return name;
	}
	public float getWeight() {
		return weight;
	}
	public float getSize() {
		return size; 
	}
	public ArrayList<InventoryItems> getComponents() {
		return components;
	}
	public float getStacksize() {
		return stacksize;
	}
}
