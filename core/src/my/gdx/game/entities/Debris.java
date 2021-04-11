package my.gdx.game.entities;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Json.Serializable;

import my.gdx.game.inventory.Inventory;
import my.gdx.game.inventory.InventoryItems;
import my.gdx.game.inventory.Item;

public class Debris extends Entity{
	static transient Random RNG = new Random(); 
	protected static final transient InventoryItems[] defaultpossiblecontents =
	{InventoryItems.Iron, InventoryItems.Cobalt, InventoryItems.Nickel, InventoryItems.Silver, InventoryItems.Gold, InventoryItems.Platinum,
		InventoryItems.Palladium, InventoryItems.Copper, InventoryItems.Tantalum, InventoryItems.Aluminum, InventoryItems.Tin, InventoryItems.Zinc, InventoryItems.Neodymium}; 
		private static final long serialVersionUID = 6529685098267757690L;
		/**
		* Creates a mineable entity
		* @param model
		* @param type
		*/
		public Debris(Vector3 position, Model model, ArrayList<Item> contents, int radius, long ID) {
			super(model, EntityType.ASTEROID, ID);
			this.pos = position; 
			this.inventory = new Inventory(contents, 999999999);  
			this.size = radius; 
			for(Item i : contents) {
				this.mass+=i.getWeight(); 
			}
			// TODO Auto-generated constructor stub
		}
		
		public Debris(Vector3 position, Model model, Inventory contents, int radius, long ID) {
			super(model, EntityType.ASTEROID, ID);
			this.pos = position; 
			this.inventory = contents;  
			this.size = radius; 
			for(Item i : contents.getItems()) {
				this.mass+=i.getWeight(); 
			}
			// TODO Auto-generated constructor stub
		}
		
		public Debris(Vector3 position, Model model, int radius, long ID) {
			super(model, EntityType.ASTEROID, ID); 
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