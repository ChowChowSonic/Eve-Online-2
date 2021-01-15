package my.gdx.game.inventory;

import java.util.ArrayList;

public class InventoryItem<components> {
	private String name;
	private float weight;
	private float size;
	private ArrayList<InventoryItem> components;
	private int stacksize;
	public InventoryItem(String name, float kg, float cubicm3) {
		this.name = name;
		this.weight = kg; 
		this.size = cubicm3;
		stacksize = 1; 
		components = new ArrayList<InventoryItem>();
	}
	
	public InventoryItem(String name, float kg, float cubicm3, int stacksize) {
		this.name = name;
		this.weight = kg; 
		this.size = cubicm3;
		this.stacksize = stacksize; 
		components = new ArrayList<InventoryItem>();
	}
	
	public InventoryItem(String name, float kg, float cubicm3, ArrayList<InventoryItem> components) {
		this.name = name;
		this.weight = kg; 
		this.size = cubicm3;
		this.components = components;
		stacksize = 1;
	}
	
	public InventoryItem(String name, float kg, float cubicm3, int stacksize, ArrayList<InventoryItem> components) {
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
	public ArrayList<InventoryItem> getComponents() {
		return components;
	}
	public float getStacksize() {
		return stacksize;
	}
}
