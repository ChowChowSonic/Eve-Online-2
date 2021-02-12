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
import com.badlogic.gdx.utils.Disposable;

import my.gdx.game.entities.CelestialObject;
import my.gdx.game.entities.Debris;
import my.gdx.game.entities.Entity;
import my.gdx.game.entities.Player;
import my.gdx.game.entities.Station;
import my.gdx.game.inventory.Inventory;

public class EveOnline2 extends ApplicationAdapter{
	
	public static ModelBuilder builder;
	public static Player player; 
	public static ArrayList<Disposable> disposables = new ArrayList<Disposable>(); 
	public static final Inventory materialcensus = new Inventory((float)Math.pow(3, 38)), usedmaterials = new Inventory((float)Math.pow(3, 38)), vanishedmaterials = new Inventory((float)Math.pow(3, 38));	
	public final long attributes = Usage.Position | Usage.Normal | Usage.TextureCoordinates;
	
	private static Camera cam;
	private static ArrayList<Entity> entities = new ArrayList<Entity>();
	private static AssetManager manager;
	private final int renderDist = 260000, vanishingpoint = 9000;//20100;
	private ModelBatch batch;
	private Model object;
	private ModelInstance background;
	private ArrayList<Hud> windows = new ArrayList<Hud>();
	private ShapeRenderer hudrenderer;
	private SpriteBatch textrenderer;
	private Environment env; 
	private float cameradist = 3f;

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

		Material material = new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("badlogic.jpg"))), 
		ColorAttribute.createSpecular(1, 1, 1, 1),
		FloatAttribute.createShininess(8f));
		object = builder.createSphere(1f, 1f, 1f, 24, 24, material, attributes);
		
		//add the player
		EveOnline2.addEntity(new Player(manager.get("ship.obj", Model.class), Entity.EntityType.PLAYER));
		player = this.getPlayer();
		
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, Color.YELLOW));
		env.add(new DirectionalLight().set(0.95f, 0.8f, 0.5f, 0f, 0f, 0f));
		
		//add the sun
		player.setPos(600, 0, 0);
		material = new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("2k_sun.jpg"))), 
		ColorAttribute.createSpecular(1, 1, 1, 1),
		FloatAttribute.createShininess(100f));
		object = builder.createSphere(500f, 500f, 500f, 100, 100, material, attributes);
		EveOnline2.addEntity(new CelestialObject(new Vector3(0,0,0),object, 5000000, 500f));
		
		//add a station & an asteroid
		EveOnline2.addEntity(new Station(new Vector3(2000,0,0),manager.get("SpaceStation.obj", Model.class), 5000, 50, 100));
		
		material = new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("badlogic.jpg"))), 
		ColorAttribute.createSpecular(1, 1, 1, 1),
		FloatAttribute.createShininess(100f));		
		object = builder.createSphere(10f, 10f, 10f, 10, 10, material, attributes);
		EveOnline2.addEntity(new Debris(new Vector3(600, 20, 0), object, 10) ); 
		
		//add the background+HUD
		background = new ModelInstance(manager.get("spacesphere3.obj", Model.class));
		
		textrenderer = Hud.getTextrenderer();
		
		hudrenderer = Hud.getRenderer();
		hudrenderer.setAutoShapeType(true);
		
		windows.add(new HealthBar(player));
		windows.add(new InventoryMenu(player));
		windows.add(new DockingButton()); 
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
		if(Gdx.input.isButtonPressed(Buttons.BACK) && cameradist > 2*cam.near){
			cameradist -=0.01; 
		}else if(Gdx.input.isButtonPressed(Buttons.FORWARD) && cameradist < 20){
			cameradist +=0.01; 
		}
		//camera distance correction
		if(cam.position.dst(player.getPos()) > cameradist || cam.position.dst(player.getPos()) < cameradist) {
			Vector3 normvec = cam.position.cpy().sub(player.getPos()).nor();
			cam.position.set(player.getPos().x+(cameradist*normvec.x), player.getPos().y+(cameradist*normvec.y),player.getPos().z+(cameradist*normvec.z));
			cam.lookAt(player.getPos());
		}//*/
		//Camera rotations
		if(Gdx.input.isButtonPressed(Buttons.RIGHT)) {
			
			float deltax = Gdx.input.getDeltaX(); 
			float deltay = Gdx.input.getDeltaY();
			Vector3 direction = cam.direction.cpy().crs(cam.up).nor();
			cam.translate(direction.x*deltax*0.025f,0,direction.z*deltax*0.025f);
			
			direction = cam.up.nor(); 
			
			if((direction.y > 0.1) || (cam.direction.hasSameDirection(new Vector3(0,1,0)) && deltay > 0) || (cam.direction.hasSameDirection(new Vector3(0,-1,0)) && deltay < 0)){
					cam.translate(direction.x*deltay*0.025f,direction.y*deltay*0.025f,direction.z*deltay*0.025f);
			}
			cam.up.x = 0; cam.up.z =0;
			cam.lookAt(player.getPos());
		}
		
		
		//entity management
		for(int i =0; i < entities.size(); i++)
		for(int e =0; e < entities.size(); e++) {
			if(i < e) {
				entities.get(i).touches(entities.get(e));
			}
		}
		background.transform.set(player.getPos(), new Quaternion());
		
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
				//System.out.println(e.getEntityType()+":\n"+e.inventory.toString());
			}
			
			e.update(Gdx.graphics.getDeltaTime());
		}
		batch.end();
		runItemCensus();
		usedmaterials.empty();
		
		cam.translate(player.getVel());
		cam.update();
		
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
		if(e instanceof CelestialObject)
		entities.add(0, e);
		else if(e instanceof Player) {
			entities.add(entities.size(), e);
		}else entities.add(entities.size(), e);
	}
	
	public Player getPlayer() {
		for(Entity e : entities){ {
			if(e instanceof Player)
			return (Player) e;
		}
	}
	Player p = new Player(manager.get("ship.obj", Model.class), Entity.EntityType.PLAYER);
	addEntity(p);
	return p;
}

private void runItemCensus() {
	Inventory underflow = new Inventory(usedmaterials.getDifferences(materialcensus), 999999); 
	Inventory overflow = new Inventory(materialcensus.getDifferences(usedmaterials),999999);
	
	materialcensus.additem(overflow.getItems());
	vanishedmaterials.additem(underflow.getItems());
	
	
	if(vanishedmaterials.getItemcount() > 100) {
		addEntity(new Debris( new Vector3(700, 5, 0), 
		builder.createSphere(15, 15, 15, 10, 10, new Material(TextureAttribute.createDiffuse(new Texture(Gdx.files.internal("badlogic.jpg"))), 
		ColorAttribute.createSpecular(1, 1, 1, 1),FloatAttribute.createShininess(8f)), attributes), 
		vanishedmaterials.getItems(),15));
		vanishedmaterials.empty();
		//System.out.println("Available: "+materialcensus.toString() + "\nUsed: "+usedmaterials.toString()+ "\nUnaccounted for: "+vanishedmaterials.toString());
		//System.out.println("Excess:\n"+overflow.toString() +"Lacking:\n"+ underflow.toString());
		//System.out.println("Asteroid Spawned!");
	}
}//ends runItemCensus()

}//ends class