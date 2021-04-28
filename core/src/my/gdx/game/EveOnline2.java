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
import my.gdx.game.entities.removedEntity;
import my.gdx.game.entities.Entity.EntityType;
import my.gdx.game.inventory.Inventory;

public class EveOnline2 extends ApplicationAdapter{
	
	public static Model DEFAULTMODEL; 
	public static ArrayList<Entity> unbuiltentities = new ArrayList<Entity>(), entities = new ArrayList<Entity>();
	
	public static Player player; 
	public static ArrayList<Disposable> disposables = new ArrayList<Disposable>(); 
	public static final Inventory materialcensus = new Inventory((float)Math.pow(3, 38)), usedmaterials = new Inventory((float)Math.pow(3, 38)), vanishedmaterials = new Inventory((float)Math.pow(3, 38));	
	public static final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
	
	
	private static Camera cam;
	private static ArrayList<Hud> windows = new ArrayList<Hud>();
	private static final long serialVersionUID = 1L;//I need this for some reason and I don't know why.
	private final int renderDist = 260000, vanishingpoint = 9000;//20100;
	private static float cameradist = 3f;
	private ModelBatch batch;
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
		
		//Connect to the server & add the player
		System.out.println("Attempting to connect...");
		connection = new ClientAntenna(/*"Server"*/ "DESKTOP-E2274E2", 26000); 
		System.out.println("Connected!");
		if(unbuiltentities.size() > 0){
			player = (Player) buildEntity(unbuiltentities.get(0));
		}else{
			do{
				player = (Player)buildEntity(connection.requestEntity(0));
			}while(player == null);
		}
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
		Entity.manager.load("spacesphere3.obj", Model.class);
		Entity.manager.finishLoading();
		background = new ModelInstance(Entity.manager.get("spacesphere3.obj", Model.class));
		
		textrenderer = Hud.getTextrenderer();
		
		hudrenderer = Hud.getRenderer();
		hudrenderer.setAutoShapeType(true);
		
		windows.add(new HealthBar(player));
	}//ends create()
	
	@Override
	public void render() {
		// TODO Auto-generated method stub
		super.render();
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		//build all unbuilt entities
		if(unbuiltentities.size()>0){
			for(int i = 0; i < unbuiltentities.size(); i++){
				boolean entityfound = false;
				Entity e2 = unbuiltentities.get(i);
				if(e2== null) continue; 
				for(Entity e : entities){
					if(e.equals(e2)){
						updateEntityFromSerialized(e, e2); 
						
						entityfound = true;
						break;
					}
					
				}
				if(!entityfound){
					Entity e = buildEntity(unbuiltentities.get(i));
					entities.add(e);
				} 
			}
			unbuiltentities.clear();
		}
		if(player.getTetheringStationID() !=0  )System.out.println(player.getTetheringStationID());
		//Camera rotation
		if(Gdx.input.isButtonPressed(Buttons.BACK) && cameradist > 2*cam.near){
			cameradist -=0.01; 
		}else if(Gdx.input.isButtonPressed(Buttons.FORWARD) && cameradist < 20){
			cameradist +=0.01; 
		}
		
		
		if(Gdx.input.isKeyJustPressed(Keys.W) && !player.isBoosting()){
			Vector3 dir =cam.direction.cpy().nor();
			connection.accelPlayer(player.getID(), dir.x, dir.y, dir.z);
		}else if(Gdx.input.isKeyJustPressed(Keys.S) && !player.isBoosting()){
			Vector3 dir =cam.direction.cpy().nor();
			connection.accelPlayer(player.getID(), -dir.x, -dir.y, -dir.z);
		}
		
		//entity management
		if(Gdx.input.isKeyPressed(Keys.SPACE) && !player.isBoosting()) {
			connection.decelplayer(player.getID());     
		}
		
		if(Gdx.input.isButtonPressed(Buttons.LEFT)) {
			Vector3 dir =cam.direction.cpy().nor();
			connection.boostPlayer(player.getID(), dir.x, dir.y, dir.z);
		}
		
		for(Entity e : entities) {e.update(Gdx.graphics.getDeltaTime());}
		background.transform.set(player.getPos(), new Quaternion());
		
		
		//camera rotations, distance correction & Movement
		Vector3 normvec = cam.direction.cpy(); 
		cam.position.set(player.getPos().x-(cameradist*normvec.x), player.getPos().y-(cameradist*normvec.y),player.getPos().z-(cameradist*normvec.z));
		cam.lookAt(player.getPos());
		cam.update();
		
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
		
		batch.begin(cam);
		batch.render(background);
		for(int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i); 
			float distance = e.getPos().dst(player.getPos());
			if(e.getEntityType() == Entity.EntityType.CELESTIALOBJ && distance <= vanishingpoint) {
				batch.render(e.getInstance());
			}else if(e.getEntityType() != Entity.EntityType.CELESTIALOBJ &&distance < renderDist) {
				batch.render(e.getInstance());
			}
		}
		batch.end();
		usedmaterials.empty();
		
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
	
	/**
	* Takes two equivalent entities: one outdated entity, and an "updated" yet unbuilt one from the server. 
	* It builds the updated one, and sets all outdated info on the old one to the more recent info. Namely, the position, velocity and accel. 
	* @param alreadyPresentEntity
	* @param serializedEntity
	*/
	public static void updateEntityFromSerialized(Entity alreadyPresentEntity, Entity serializedEntity){
		serializedEntity.buildSerializedEntity();
		//System.out.println("pos: "+e.getVel());
		alreadyPresentEntity.setPos(serializedEntity.getPos());
		alreadyPresentEntity.setVel(serializedEntity.getVel());
		alreadyPresentEntity.setAccel(serializedEntity.getAccel());
		//System.out.println(e.getVel());
	}
	/**
	* I mean, it adds an entity. What did you expect? The spanish inquisition?
	*/
	public static void addEntity(Entity e) {
		if(e == null) {
			System.out.println("Null entity recieved!");
			return;
		}else if(e instanceof removedEntity){
			for(int i = 0; i < entities.size(); i++){
				if(e.equals(entities.get(i))){
					entities.remove(i);
					System.out.print("entity removed!");
					return;
				}
			}
		}
		unbuiltentities.add(e);
		//System.out.println(e.toString());
	}
	
	/** 
	* "Builds" the entity, loading and giving it a model from the Entity's assetmanager, then copies the entity's properties, applies it to an appropriate class and returns said class. 
	*/
	public static Entity buildEntity(Entity e){
		if(e == null) return new removedEntity(0L); 
		e.buildSerializedEntity(); 
		if(e.getEntityType() == EntityType.PLAYER){
			Player p = new Player(e.getModelName(), e.getEntityType(), e.getID()); 
			p.buildSerializedEntity();
			return p;
		}else if(e.getEntityType() == EntityType.ASTEROID){
			Debris d = new Debris(e.getPos(), e.getModelName(), e.inventory, (int) e.getSize(), e.getID()); 
			d.buildSerializedEntity();
			return d;
		}else if (e.getEntityType() == EntityType.CELESTIALOBJ){
			CelestialObject o = new CelestialObject(e.getPos(), e.getModelName(), e.getMass(), e.getSize(), e.getID()); 
			o.buildSerializedEntity();
			return o; 
		}else if (e.getEntityType() == EntityType.STATION){
			Station e2 = (Station) e;
			Station o = new Station(e.getPos(), e.getModelName(), e.getMass(), e.getSize(), e2.getouterRadius(), e.getID()); 
			o.buildSerializedEntity();
			return o; 
		}else{
			Player p = new Player(e.getModelName(), e.getEntityType(), e.getID()); 
			p.buildSerializedEntity();
			System.out.println("Entity not recognised!");
			return p; 
		}
	}
	
	/**
	* Sorts the entity list by entity type such that all entities go in the following order:
	* Celestial Object, Station, <Literally anything that's not a player>, players. 
	*/
	public static void sortEntities(){
		ArrayList<Entity> newlist = new ArrayList<Entity>(); 
		for(Entity e : entities){
			if(e.getEntityType() == EntityType.CELESTIALOBJ) newlist.add(e);
		}
		
		for(Entity e : entities){
			if(e.getEntityType() == EntityType.STATION) newlist.add(e); 
		}
		
		for(Entity e : entities){
			if(e.getEntityType() != EntityType.PLAYER || e.getEntityType() != EntityType.CELESTIALOBJ || e.getEntityType() != EntityType.STATION){
				newlist.add(e);
			}
		}
		
		for(Entity e : entities){
			if(e.getEntityType() == EntityType.PLAYER) newlist.add(e); 
		}
		
		entities = newlist; 
	}
	
	/**
	* Adds a HUD to the screen
	* @param h
	*/
	public static void addHUD(Hud h){
		if(!windows.contains(h))
		windows.add(h);
	}
	
	/**
	* Removes a HUD from the screen
	* @param hud
	*/
	public static void removeHUD(Hud hud){
		//if(windows.contains(hud))
		windows.remove(hud);
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
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
		Entity.manager.dispose();
		batch.dispose();
		textrenderer.dispose();
		hudrenderer.dispose();
		connection.close();
		System.gc();
		System.exit(0);
	}
}//ends class