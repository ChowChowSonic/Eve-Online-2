package my.gdx.game.entities;

import java.util.ArrayList;

public class InventoryItem {
	private final float weight, size, stacksize;
	private final String name;
	private final ArrayList<InventoryItem> components; 
	public InventoryItem(String name, float kg, float cubicm3) {
		this.name = name;
		this.weight = kg; 
		this.size = cubicm3;
		stacksize = 1; 
		components = new ArrayList<InventoryItem>();
	}
	public InventoryItem(String name, float kg, float cubicm3, ArrayList<InventoryItem> components) {
		this.name = name;
		this.weight = kg; 
		this.size = cubicm3;
		this.components = components;
		stacksize = 1;
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
	public ArrayList<InventoryItem> getComponents() {
		return components;
	}
	public float getStacksize() {
		return stacksize;
	}
}
