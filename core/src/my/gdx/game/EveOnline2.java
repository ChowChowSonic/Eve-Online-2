package my.gdx.game;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.ScreenUtils;

import my.gdx.game.Hud.Button;
import my.gdx.game.Hud.DockingButton;
import my.gdx.game.Hud.DropdownMenu;
import my.gdx.game.Hud.HealthBar;
import my.gdx.game.Hud.Hud;
import my.gdx.game.Hud.Hud.hudtype;
import my.gdx.game.Hud.InfoMenu;
import my.gdx.game.Hud.InventoryMenu;
import my.gdx.game.Hud.TargetHud;
import my.gdx.game.entities.Asteroid;
import my.gdx.game.entities.CelestialObject;
import my.gdx.game.entities.Crate;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Entity.EntityType;
import my.gdx.game.entities.Player;
import my.gdx.game.entities.Station;
import my.gdx.game.entities.removedEntity;
import my.gdx.game.inventory.Inventory;

public class EveOnline2 extends ApplicationAdapter {
	
	public static Model DEFAULTMODEL;
	public static ArrayList<Entity> entities = new ArrayList<Entity>();
	
	public static Player player;
	public static ArrayList<Disposable> disposables = new ArrayList<Disposable>();
	public static final Inventory materialcensus = new Inventory((float) Math.pow(3, 38)),
	usedmaterials = new Inventory((float) Math.pow(3, 38)),
	vanishedmaterials = new Inventory((float) Math.pow(3, 38));
	public static final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
	public static ArrayList<Hud> windows = new ArrayList<Hud>();
	
	public static ClientAntenna connection;
	
	private static Camera cam;
	private static final long serialVersionUID = 1L;// I need this for some reason and I don't know why.
	private final int renderDist = 260000, vanishingpoint = 9000;// 20100;
	private static float cameradist = 3f;
	private ModelBatch batch;
	private ModelInstance background;
	private ShapeRenderer hudrenderer;
	private SpriteBatch textrenderer;
	private Environment env;
	private boolean justboosted = false; 
	private TargetHud targetmanager; 
	
	/*
	* Reminder: X = -<------------------>+ Y = -[down] [up]+ Z = -[Forward]
	* [Backward]+
	*/
	
	/**
	* I need this in here to create the game for some reason.
	*/
	@Override
	public void create() {
		super.create();
		cam = new PerspectiveCamera(80, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0f, 0f, 3f);
		cam.lookAt(0f, 0f, 0f);
		cam.near = 1f; //closest possible render dist
		cam.far = renderDist; //max render dist
		batch = new ModelBatch();
		
		// Load in all assets from the assets folder
		FileHandle assetFolder = Gdx.files.local("\\core\\assets\\");
		
		for (FileHandle entry : assetFolder.list()) {
			if (entry.extension().equals("obj")) {
				Entity.manager.load(entry.name(), Model.class);
				Entity.manager.finishLoadingAsset(entry.name());
				System.out.println(entry.name() + " Loaded");
			}
		}
		Entity.manager.finishLoading();
		
		// Connect to the server & add the player
		System.out.println("Attempting to connect...");
		connection = new ClientAntenna(/* "Server" */ "DESKTOP-E2274E2", 26000);
		System.out.println("Connected!");
		player = (Player) entities.get(0);
		connection.start();
		
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, Color.YELLOW));
		env.add(new DirectionalLight().set(0.95f, 0.8f, 0.5f, 0f, 0f, 0f));
		
		// add the sun
		/*
		* material = new Material(TextureAttribute.createDiffuse(new
		* Texture(Gdx.files.internal("2k_sun.jpg"))), ColorAttribute.createSpecular(1,
		* 1, 1, 1), FloatAttribute.createShininess(100f)); object =
		* builder.createSphere(500f, 500f, 500f, 100, 100, material, attributes);
		*/
		// EveOnline2.addEntity(new CelestialObject(new Vector3(0,0,0),object, 5000000,
		// 500f));
		
		// add a station & an asteroid
		// EveOnline2.addEntity(new Station(new
		// Vector3(2000,0,0),manager.get("SpaceStation.obj", Model.class), 5000, 50,
		// 100));
		
		// material = new Material(TextureAttribute.createDiffuse(new
		// Texture(Gdx.files.internal("badlogic.jpg"))),
		// ColorAttribute.createSpecular(1, 1, 1, 1),
		// FloatAttribute.createShininess(100f));
		// object = builder.createSphere(10f, 10f, 10f, 10, 10, material, attributes);
		// EveOnline2.addEntity(new Debris(new Vector3(600, 20, 0), object, 10, 1L));
		
		// add the background+HUD
		Entity.manager.load("spacesphere3.obj", Model.class);
		Entity.manager.finishLoading();
		background = new ModelInstance(Entity.manager.get("spacesphere3.obj", Model.class));
		
		textrenderer = Hud.getTextrenderer();
		
		hudrenderer = Hud.getRenderer();
		hudrenderer.setAutoShapeType(true);

		//Adding the HUD
		targetmanager = (new TargetHud(3)); 
		windows.add(targetmanager);
		windows.add(new HealthBar(player));
		String[] strings = {"Welcome to Eve Online 2! \n Press W or S to move backwards and forwards in the direction your camera is facing. \n SHIFT+W Boosts you at extreme speeds \n Press I to open your inventory. \n Right click and drag to rotate the camera "+
		"\n Left click on objects or people to display options regarding them \n CTRL+Left-Click on objects or people to target them.", "In this game, you can choose what you want to do; mining, exploring, trading, fighting, hunting enemies of Humanity: you name it!\n Please enjoy your stay, and remember that the game is still in the alpha stages of development.","Today while playing Fortnite I ran into Allah at pleasant parks while he was trying to run solo squads. Knowing he was weakened, I told my squad to stay high ground while I engaged him on my own. He had worse loot and no heals but he's an amazing player. He is so fucking powerful. I'm not cracked enough at the game to do this alone. I barely escaped with my life and ran out of heals. His edits are immensely cracked and he cranks 90s at an ungodly speeds. I can't imagine what he would do to a new, unsuspecting default skin. I'm scared that I will have to face him again soon if I ever want to continue climbing the Fortnite ladder. I'm currently using medkits and mini shields that my squad gave me to try and heal as quickly as possible. Please be safe everyone. Allah is much stronger than I first imagined and we will have to do this together (maybe even cross team) if we want to shit on this god."};
		String[] imgnames = {"Space.jpg", "Screenshot (1).png","E.png"}; 
		windows.add(new InfoMenu(imgnames, strings)); 
	}// ends create()
	
	/**
	* Here, Public Void Render() serves as an updater for the ingame world, AS WELL
	* AS image rendering.
	*/
	@Override
	public void render() {
		// TODO Auto-generated method stub
		super.render();
		
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		
		// Camera rotation
		if (Gdx.input.isButtonPressed(Buttons.BACK) && cameradist > 2 * cam.near) {
			cameradist -= 0.01;
		} else if (Gdx.input.isButtonPressed(Buttons.FORWARD) && cameradist < 20) {
			cameradist += 0.01;
		}
		
		Vector3 dir = cam.direction.cpy().nor();
		if (Gdx.input.isKeyJustPressed(Keys.W) && !player.isBoosting()) {
			connection.accelPlayer(dir.x, dir.y, dir.z);
		} else if (Gdx.input.isKeyJustPressed(Keys.S) && !player.isBoosting()) {
			connection.accelPlayer(-dir.x, -dir.y, -dir.z);
		} else if (Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && Gdx.input.isKeyPressed(Keys.W)) {
			dir = player.getRotation();
			connection.boostPlayer(dir.x, dir.y, dir.z, true);
			justboosted = true; 
		} else if ((!Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) || !Gdx.input.isKeyPressed(Keys.W)) && justboosted) {
			dir = player.getRotation();
			connection.boostPlayer(dir.x, dir.y, dir.z, false);
			justboosted = false;
		}
		
		// entity management
		if (Gdx.input.isKeyPressed(Keys.SPACE) && !player.isBoosting()) {
			connection.decelplayer();
		}
		
		// HUD & Entity interaction, targeting
		if (Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
			boolean isonHud = false;
			int x = Gdx.input.getX(), y = Gdx.input.getY();
			for (Hud window : windows) {
				if (window.isInBounds(x, y)) {
					isonHud = true;
					window.interact(x, y);
					break;
				}
			}
			
			if(!isonHud){
				Ray vec = cam.getPickRay(x, y); 
				Vector3 position = vec.origin.cpy(); 
				Entity target = null; 
				for(Entity e : entities){
					float dist = e.getPos().dst(position); 
					Vector3 rayLine = position.cpy().add(vec.direction.x * dist, vec.direction.y*dist, vec.direction.z*dist); 
					if(rayLine.dst(e.getPos()) <= e.getSize()){
						if(target == null || target.getPos().dst(position) > e.getPos().dst(position)) target = e; 
					}
					
				}
				removeHUD(hudtype.dropdown);
				if(target != null && !isonHud){
					if(Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && !target.equals(player)){
						targetmanager.addTarget(target);
					}else{
						 addHUD(new DropdownMenu(x, y, target));
					}
				}
			}
		}// end of HUD, Targeting stuff
		
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			e.update(Gdx.graphics.getDeltaTime());
			
		}
		background.transform.set(player.getPos(), new Quaternion());
		
		// camera rotations, distance correction & Movement
		Vector3 normvec = cam.direction.cpy();
		cam.position.set(player.getPos().x - (cameradist * normvec.x), player.getPos().y - (cameradist * normvec.y),
		player.getPos().z - (cameradist * normvec.z));
		cam.lookAt(player.getPos());
		cam.update();
		
		if (Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			float deltax = Gdx.input.getDeltaX();
			float deltay = Gdx.input.getDeltaY();
			Vector3 direction = cam.direction.cpy().crs(cam.up).nor();
			cam.translate(direction.x * deltax * 0.025f, 0, direction.z * deltax * 0.025f);
			
			direction = cam.up.nor();
			
			if ((direction.y > 0.1) || (cam.direction.hasSameDirection(new Vector3(0, 1, 0)) && deltay > 0)
			|| (cam.direction.hasSameDirection(new Vector3(0, -1, 0)) && deltay < 0)) {
				cam.translate(direction.x * deltay * 0.025f, direction.y * deltay * 0.025f,
				direction.z * deltay * 0.025f);
			}
			cam.lookAt(player.getPos());
		}
		cam.up.x = 0;
		cam.up.z = 0;
		
		batch.begin(cam);
		batch.render(background);
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			float distance = e.getPos().dst(player.getPos());
			if (e.getEntityType() == Entity.EntityType.CELESTIALOBJ && distance <= vanishingpoint) {
				e.render();
				batch.render(e.getInstance());
				
			} else if (e.getEntityType() != Entity.EntityType.CELESTIALOBJ && distance < renderDist) {
				e.render();
				batch.render(e.getInstance());
			}
		}
		batch.end();
		usedmaterials.empty();
		
		// Inventory Menu stuff
		if (Gdx.input.isKeyJustPressed(Keys.I)) {
			if (windows.contains(new InventoryMenu(player)))
			windows.remove(new InventoryMenu(player));
			else
			windows.add(new InventoryMenu(player));
		}
		if (player.isTethered() && !windows.contains(new DockingButton())) {
			windows.add(new DockingButton());
		} else if (!player.isTethered()) {
			windows.remove(new DockingButton());
		}
		
		// Hud rendering
		for (int i = 0; i < windows.size(); i++) {
			Hud window = windows.get(i);
			window.updateShape();
		}
		hudrenderer.end();
		
		for (int i = 0; i < windows.size(); i++) {
			Hud window = windows.get(i);
			window.updateText();
		}
		
		textrenderer.end();
		if (Gdx.input.isKeyPressed(Keys.ESCAPE)) {
			this.dispose();
			
		}
		
		//Screenshots -- THIS PART MUST ALWAYS BE DONE LAST
		if(Gdx.input.isKeyJustPressed(Keys.PRINT_SCREEN)){
			byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), true);
			
			// This loop makes sure the whole screenshot is opaque and looks exactly like what the user is seeing
			for (int i = 4; i <= pixels.length; i += 4) {
				pixels[i - 1] = (byte) 255;
			}
			
			Pixmap pixmap = new Pixmap(Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight(), Pixmap.Format.RGBA8888);
			BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
			String fileloc =Gdx.files.getLocalStoragePath()+"Screenshots\\";
			int n = 1;
			FileHandle f;
			do{
				f = new FileHandle(fileloc+"Screenshot ("+n+").png");
				n++;
			}while(f.exists());
			PixmapIO.writePNG(f, pixmap);
			//PixmapIO.writePNG(Gdx.files.external("mypixmap.png"), pixmap);
			pixmap.dispose();
		}
		
		System.gc();
		//System.out.println(player.inventory.toString());
	}
	
	/**
	* I mean, it adds an entity. What did you expect? The spanish inquisition?
	*/
	public static void addEntity(Entity e) {
		if (e == null) {
			System.out.println("Null entity recieved!");
			return;
		} else if (e instanceof removedEntity) {
			for (int i = 0; i < entities.size(); i++) {
				if (e.equals(entities.get(i))) {
					entities.remove(i);
					System.out.print("entity removed!");
					return;
				}
			}
		}
		for (Entity e2 : entities) {
			if (e.equals(e2)) {
				e2.updateEntityFromSerialized(e);
				return;
			}
			
		}
		Entity e3 = buildEntity(e);
		entities.add(e3);
		// System.out.println(e.toString());
	}
	
	/**
	* "Builds" the entity, loading and giving it a model from the Entity's
	* assetmanager, then copies the entity's properties, applies it to an
	* appropriate class and returns said class.
	*/
	public static Entity buildEntity(Entity e) {
		if (e == null)
		return new removedEntity(0L);
		switch(e.getEntityType()){
			case PLAYER:
			Player p = new Player(e.getModelName(), e.getID());
			return p;
			case ASTEROID:
			Asteroid d = new Asteroid(e.getModelName(), e.inventory, (int) e.getSize(), e.getID());
			return d;
			case DEBRIS:
			Crate c = new Crate(e.getModelName(), new Inventory(2500), e.getID()); 
			case CELESTIALOBJ:
			CelestialObject o = new CelestialObject(new Vector3(), e.getModelName(), e.getMass(), e.getSize(),
			e.getID());
			return o;
			case STATION:
			Station e2 = (Station) e;
			Station o2 = new Station(new Vector3(), e.getModelName(), e.getMass(), e.getSize(), e2.getouterRadius(),
			e.getID());
			return o2;
			default:
			Player defaul = new Player(e.getModelName(), e.getID());
			System.out.println("Entity not recognised!");
			return defaul;
		}
	}
	
	/**
	* Sorts the entity list by entity type such that all entities go in the
	* following order: Celestial Object, Station, <Literally anything that's not a
	* player>, players.
	*/
	public static void sortEntities() {
		ArrayList<Entity> newlist = new ArrayList<Entity>();
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e.getEntityType() == EntityType.CELESTIALOBJ)
			newlist.add(e);
		}
		
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e.getEntityType() == EntityType.STATION)
			newlist.add(e);
		}
		
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e.getEntityType() != EntityType.PLAYER || e.getEntityType() != EntityType.CELESTIALOBJ
			|| e.getEntityType() != EntityType.STATION) {
				newlist.add(e);
			}
		}
		
		for (int i = 0; i < entities.size(); i++) {
			Entity e = entities.get(i);
			if (e.getEntityType() == EntityType.PLAYER)
			newlist.add(e);
		}
		
		entities = newlist;
	}
	
	/**
	* Adds a HUD to the screen
	* 
	* @param h
	*/
	public static void addHUD(Hud h) {
		if (!windows.contains(h))
		windows.add(h);
	}
	
	/**
	* it Removes a HUD from the screen
	* 
	* @param hud
	*/
	public static void removeHUD(Hud hud) {
		// if(windows.contains(hud))
		if(hud == null) return; 
		if (hud.getButtons() != null){
			ArrayList<Button> b = hud.getButtons();
			for (int i = 0; i < b.size(); i++) {
				Button button = b.get(i);
				button.dispose();
				windows.remove(button);
			}
		}
		hud.dispose();
		windows.remove(hud);
	}
	
	public static void removeHUD(hudtype type){
		for(int i = 0; i < windows.size(); i++){
			if(windows.get(i).getType() == type) {
				windows.get(i).dispose();
				removeHUD(windows.get(i));
				return; 
			}
		}
		
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
		for(Hud h : windows) h.dispose();
		Entity.manager.dispose();
		batch.dispose();
		textrenderer.dispose();
		hudrenderer.dispose();
		Hud.getFont().dispose();
		connection.close();
		System.gc();
		System.exit(0);
	}
}// ends class