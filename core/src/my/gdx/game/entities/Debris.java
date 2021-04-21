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
		public Debris(Vector3 position, String modelname, ArrayList<Item> contents, long ID) {
			super(modelname, EntityType.ASTEROID, ID);
			this.pos = position; 
			this.inventory = new Inventory(contents, 999999999);  
			this.size = 0; 
			for(Item i : contents){
				size+=i.getVolume()/100; 
				this.mass+=i.getWeight(); 
			}
			if(this.instance !=null){
			this.instance.transform.scale(size/100000000, size/100000000, size/100000000);
			}
			// TODO Auto-generated constructor stub
		}
		
		public Debris(Vector3 position, String modelname, Inventory contents, int radius, long ID) {
			super(modelname, EntityType.ASTEROID, ID);
			this.pos = position; 
			this.inventory = contents;  
			for(Item i : contents.getItems()){
				size+=i.getVolume()/100; 
				this.mass+=i.getWeight();
			}
			this.instance.transform.scale(size/10000000, size/10000000, size/10000000);
			// TODO Auto-generated constructor stub
		}
		
		public Debris(Vector3 position, String modelname, int radius, long ID) {
			super(modelname, EntityType.ASTEROID, ID); 
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
			this.instance.transform.scl(radius);
		}
		public Debris(long ID){
			super(ID);
		}
		
	}