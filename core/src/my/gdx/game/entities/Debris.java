package my.gdx.game.entities;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;

import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.InventoryItems;
import my.gdx.game.inventory.Item;

public class Debris extends Entity{
	Random RNG = new Random(); 
	protected static final InventoryItems[] defaultpossiblecontents =
			   {InventoryItems.Iron, InventoryItems.Cobalt, InventoryItems.Nickel, InventoryItems.Silver, InventoryItems.Gold, InventoryItems.Platinum,
				InventoryItems.Palladium, InventoryItems.Copper, InventoryItems.Tantalum, InventoryItems.Aluminum, InventoryItems.Tin, InventoryItems.Zinc, InventoryItems.Neodymium}; 

	/**
	 * Creates a mineable entity
	 * @param model
	 * @param type
	 */
	public Debris(Vector3 position, Model model, ArrayList<Item> contents, int radius) {
		super(model, EntityType.ASTEROID);
		this.pos = position; 
		this.inventory = new Inventory(contents, 999999999);  
		this.size = radius; 
		for(Item i : contents) {
			this.mass+=i.getWeight(); 
		}
		// TODO Auto-generated constructor stub
	}
	
	public Debris(Vector3 position, Model model, int radius) {
		super(model, EntityType.ASTEROID); 
		this.size = radius; 
		this.pos = position;
		int numberofcontents = RNG.nextInt(defaultpossiblecontents.length-1)+1;
		this.inventory = new Inventory(999999999);
		for(int i =0; i < numberofcontents; i++) {
			int itemtoadd = RNG.nextInt(defaultpossiblecontents.length);
			this.inventory.additem(defaultpossiblecontents[itemtoadd], RNG.nextInt(1000-1)+1);
		}
		
		for(Item i : this.inventory.getItems()) {
			this.mass+=i.getWeight(); 
		}
	}
	
}
