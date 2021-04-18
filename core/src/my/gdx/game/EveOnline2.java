package my.gdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.utils.Disposable;

import my.gdx.game.entities.CelestialObject;
import my.gdx.game.entities.Debris;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
import my.gdx.game.entities.Station;
import my.gdx.game.entities.Entity.EntityType;
import my.gdx.game.inventory.Inventory;

public class EveOnline2 extends ApplicationAdapter{
	
	public static Model DEFAULTMODEL; 
	public static ArrayList<Entity> unbuiltentities = new ArrayList<Entity>(), entities = new ArrayList<Entity>();
	public static AssetManager manager;
	public static ModelBuilder builder;
	public static Player player; 
	public static ArrayList<Disposable> disposables = new ArrayList<Disposable>(); 
	public static final Inventory materialcensus = new Inventory((float)Math.pow(3, 38)), usedmaterials = new Inventory((float)Math.pow(3, 38)), vanishedmaterials = new Inventory((float)Math.pow(3, 38));	
	public static final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
	
	
	private static Camera cam;
	private static ArrayList<Hud> windows = new ArrayList<Hud>();
	private static final long serialVersionUID = 1L;//I need this for some reason and I don't know why.
	private static float cameradist = 3f;
	private static ModelBatch batch;
	private final int renderDist = 260000, vanishingpoint = 9000;//20100;
	private Model object;
	private ModelInstance background;
	private ShapeRenderer hudrenderer;
	private SpriteBatch textrenderer;
	private Environment env; 
	private ClientAntenna connection; 
	/*
	* Reminder:
	* X = -<------------------>+
	* Y = -[down]  	    [up]+
	* Z = -[Forward] [Backward]+ 
	*/
	@Override
	public void create() {
		super.create();
		cam = new PerspectiveCamera(80,Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f,0f,3f);
		cam.lookAt(0f,0f,0f);
		cam.near = 1f;
		cam.far = renderDist;
		batch = new ModelBatch();
		builder = new ModelBuilder();
		manager = new AssetManager();
		
		manager.load("spacesphere3.obj", Model.class);
		manager.load("SpaceStation.obj", Model.class);
		manager.load("ship.obj", Model.class);
		manager.finishLoading();
		DEFAULTMODEL = manager.get("ship.obj", Model.class); 
		//System.out.println(manager.get("SpaceStation.obj", Model.class).toString() + "/" + manager.get("ship.obj", Model.class).toString());
		Material material = new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("badlogic.jpg"))), 
		ColorAttribute.createSpecular(1, 1, 1, 1),
		FloatAttribute.createShininess(8f));
		object = builder.createSphere(1f, 1f, 1f, 24, 24, material, attributes);
		
		//Connect to the server & add the player
		System.out.println("Attempting to connect...");
		connection = new ClientAntenna(/*"Server"*/ "DESKTOP-E2274E2", 26000); 
		System.out.println("Connected!");
		player = (Player) buildEntity(unbuiltentities.get(0));
		unbuiltentities.remove(0); 
		entities.add(player); 
		connection.start();
		
		
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, Color.YELLOW));
		env.add(new DirectionalLight().set(0.95f, 0.8f, 0.5f, 0f, 0f, 0f));
		
		//add the sun
		/*material = new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("2k_sun.jpg"))), 
		ColorAttribute.createSpecular(1, 1, 1, 1),
		FloatAttribute.createShininess(100f));
		object = builder.createSphere(500f, 500f, 500f, 100, 100, material, attributes);*/
		//EveOnline2.addEntity(new CelestialObject(new Vector3(0,0,0),object, 5000000, 500f));
		
		//add a station & an asteroid
		//EveOnline2.addEntity(new Station(new Vector3(2000,0,0),manager.get("SpaceStation.obj", Model.class), 5000, 50, 100));
		
		//material = new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("badlogic.jpg"))), 
		//ColorAttribute.createSpecular(1, 1, 1, 1),
		//FloatAttribute.createShininess(100f));		
		//object = builder.createSphere(10f, 10f, 10f, 10, 10, material, attributes);
		//EveOnline2.addEntity(new Debris(new Vector3(600, 20, 0), object, 10, 1L)); 
		
		//add the background+HUD
		background = new ModelInstance(manager.get("spacesphere3.obj", Model.class));
		
		textrenderer = Hud.getTextrenderer();
		
		hudrenderer = Hud.getRenderer();
		hudrenderer.setAutoShapeType(true);
		
		windows.add(new HealthBar(player));
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void render() {
		// TODO Auto-generated method stub
		super.render();
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		//build all unbuilt entities
		if(unbuiltentities.size()>0){
			for(int i = 0; i < unbuiltentities.size(); i++){
				Entity e = buildEntity(unbuiltentities.get(i)); 
				entities.add(e); 
			}
			unbuiltentities.clear();
		}
		
		//Camera rotation
		if(Gdx.input.isButtonPressed(Buttons.BACK) && cameradist > 2*cam.near){
			cameradist -=0.01; 
		}else if(Gdx.input.isButtonPressed(Buttons.FORWARD) && cameradist < 20){
			cameradist +=0.01; 
		}
		
		
		if(Gdx.input.isKeyJustPressed(Keys.W) && !player.justpressedboost()){
			Vector3 dir =cam.direction.cpy().nor();
			connection.accelPlayer(player.getID(), dir.x, dir.y, dir.z);
		}else if(Gdx.input.isKeyJustPressed(Keys.S) && !player.justpressedboost()){
			Vector3 dir =cam.direction.cpy().nor();
			connection.accelPlayer(player.getID(), -dir.x, -dir.y, -dir.z);
		}
		
		//entity management
		for(int i =0; i < entities.size()-1; i++)
		for(int e =i+1; e < entities.size(); e++) {
			if(entities.get(i) != null && entities.get(e) !=null) {
				entities.get(i).touches(entities.get(e));
			}
		}
		background.transform.set(player.getPos(), new Quaternion());
		//updateEnitity(connection.requestEntity(player.getID())); 
		for(Entity e : entities) {
			updateEnitity(connection.requestEntity(e.getID()));
			e.update(Gdx.graphics.getDeltaTime());
		}

		batch.begin(cam);
		batch.render(background);
		for(Entity e : entities) {
			float distance = e.getPos().dst(player.getPos());
			if(e.getEntityType() == Entity.EntityType.CELESTIALOBJ && distance <= vanishingpoint) {
				batch.render(e.getInstance());
			}else if(e.getEntityType() != Entity.EntityType.CELESTIALOBJ &&distance < renderDist) {
				batch.render(e.getInstance());
			}
			//Material Census gathering
			
			if(e.inventory != null) {
				usedmaterials.additem(e.inventory.getItems()); 
			}
			
		}
		//camera rotations, distance correction & Movement
		Vector3 normvec = cam.direction.cpy(); 
		cam.position.set(player.getPos().x-(cameradist*normvec.x), player.getPos().y-(cameradist*normvec.y),player.getPos().z-(cameradist*normvec.z));
		cam.lookAt(player.getPos());
		cam.update();
		runItemCensus();
		usedmaterials.empty();
		

		if(Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			float deltax = Gdx.input.getDeltaX(); 
			float deltay = Gdx.input.getDeltaY();
			Vector3 direction = cam.direction.cpy().crs(cam.up).nor();
			cam.translate(direction.x*deltax*0.025f,0,direction.z*deltax*0.025f);
			
			direction = cam.up.nor(); 
			
			if((direction.y > 0.1) || (cam.direction.hasSameDirection(new Vector3(0,1,0)) && deltay > 0) || (cam.direction.hasSameDirection(new Vector3(0,-1,0)) && deltay < 0)){
				cam.translate(direction.x*deltay*0.025f,direction.y*deltay*0.025f,direction.z*deltay*0.025f);
			}
			cam.lookAt(player.getPos());
		}
		cam.up.x = 0; cam.up.z =0;
		batch.end();
		
		//Inventory Menu stuff
		if(Gdx.input.isKeyJustPressed(Keys.I)){
			if(windows.contains(new InventoryMenu(player))) windows.remove(new InventoryMenu(player));
			else windows.add(new InventoryMenu(player));
		}
		if(player.isTethered()){
			windows.add(new DockingButton());
		}else{
			windows.remove(new DockingButton());
		}
		
		
		//Hud rendering
		for(Hud window : windows) {
			window.updateShape();
		}
		hudrenderer.end();
		
		for(Hud window : windows) {
			window.updateText();
		}
		
		textrenderer.end();
		if(Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			this.dispose();
			
		}
		System.gc();
	}
	
	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		manager.dispose();
		batch.dispose();
		textrenderer.dispose();
		hudrenderer.dispose();
		//connection.close();
		System.gc();
		System.exit(1);
	}
	
	public static Vector3 getCamRotation() {
		return cam.direction;
	}
	
	public static Matrix4 getCamMatrix() {
		return cam.combined;
	}
	
	public static void addEntity(Entity e) {
		unbuiltentities.add(e);
	}
	
	public static Entity buildEntity(Entity e){
		Material material = null;
		Model m = new Model();
		if(e.getEntityType() == EntityType.PLAYER){
			m = manager.get("ship.obj", Model.class); 
			e.buildEntity(m); 
			//entities.add(e);
			sortEntities();
			return new Player(e.getModel(), e.getEntityType(), e.getID()); 
		}else if(e.getEntityType() == EntityType.ASTEROID){
			/*material = new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("badlogic.jpg"))), 
			ColorAttribute.createSpecular(1, 1, 1, 1),
			FloatAttribute.createShininess(100f));
			m = builder.createSphere(e.getSize(), e.getSize(), e.getSize(), 10, 10, material, attributes);*/
			e.buildEntity(manager.get("SpaceStation.obj", Model.class)); 
			entities.add(e);
			//sortEntities();
			return new Debris(e.getPos(), e.getModel(), e.inventory, (int) e.getSize(), e.getID()); 
		}else{
			m = manager.get("ship.obj", Model.class); 
			e.buildEntity(m);
			entities.add(e);
			//sortEntities();
			return new Player(m, e.getEntityType(), e.getID()); 
		}
	}
	
	public static void updateEnitity(Entity e){
		if(e==null)System.out.print("entity not recieved!");
		e.buildEntity(new Model());
		for(int i = 0; i < entities.size(); i++){
			if(e.equals(entities.get(i))) {
				//System.out.println("pos: "+e.getVel());
				entities.get(i).setPos(e.getPos());
				entities.get(i).setVel(e.getVel());
				entities.get(i).setAccel(e.getAccel());
				//System.out.println(e.getVel());
				Vector3 normvec = cam.direction.cpy(); 
				cam.position.set(player.getPos().x-(cameradist*normvec.x), player.getPos().y-(cameradist*normvec.y),player.getPos().z-(cameradist*normvec.z));
				cam.lookAt(player.getPos());
				cam.update();
			}
			if(e.equals(entities.get(i))) entities.get(i).setPos(e.getPos());
		}
	}
	
	public static void sortEntities(){
		ArrayList<Entity> newlist = new ArrayList<Entity>(); 
		int playersinlist = 0;
		for(Entity e : entities){
			if(e.getEntityType() == EntityType.CELESTIALOBJ){
				newlist.add(0,e); 
			}else if(e.getEntityType() == EntityType.PLAYER){
				newlist.add(newlist.size(), e);
				playersinlist++;
			}else{
				if(newlist.size() > 1 || playersinlist < newlist.size()-1){
					newlist.add(newlist.size()-(playersinlist+1), e);
				}else newlist.add(e);
			}
			entities = newlist; 
		}
	}
	
	public Player getPlayer() {
		for(Entity e : entities){ {
			if(e instanceof Player)
			return (Player) e;
		}
	}
	//Player p = new Player(manager.get("ship.obj", Model.class), Entity.EntityType.PLAYER, 1L);
	//addEntity(p);
	return null;
}

private void runItemCensus() {
	Inventory underflow = new Inventory(usedmaterials.getDifferences(materialcensus), 999999); 
	Inventory overflow = new Inventory(materialcensus.getDifferences(usedmaterials),999999);
	
	materialcensus.additem(overflow.getItems());
	vanishedmaterials.additem(underflow.getItems());
	
	
	if(vanishedmaterials.getItemcount() > 100) {
		/*addEntity(new Debris( new Vector3(700, 5, 0), 
		builder.createSphere(15, 15, 15, 10, 10, new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("badlogic.jpg"))), 
		ColorAttribute.createSpecular(1, 1, 1, 1),FloatAttribute.createShininess(8f)), attributes), 
		vanishedmaterials.getItems(),15));
		vanishedmaterials.empty();*/
		//System.out.println("Available: "+materialcensus.toString() + "\nUsed: "+usedmaterials.toString()+ "\nUnaccounted for: "+vanishedmaterials.toString());
		//System.out.println("Excess:\n"+overflow.toString() +"Lacking:\n"+ underflow.toString());
		//System.out.println("Asteroid Spawned!");
	}
}//ends runItemCensus()

public static void addHUD(Hud h){
	if(!windows.contains(h))
	windows.add(h);
}

public static void removeHUD(Hud hud){
	//if(windows.contains(hud))
	windows.remove(hud);
}

}//ends class