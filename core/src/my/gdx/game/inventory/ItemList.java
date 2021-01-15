package my.gdx.game.inventory;

public enum ItemList {
	Jimbabwe_Shipping_Crates("Jimbabwe Shipping Crates", 50, 10);
	
	private String name;
	private float size;
	private float weight;

	ItemList(String name, float size, float weight) {
		this.name = name;
		this.size = size; 
		this.weight = weight; 
	}
}
