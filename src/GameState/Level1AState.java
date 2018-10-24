package GameState;

import Main.GamePanel;
import TileMap.*;
import Entity.*;
import Entity.Enemies.*;
import Entity.Player.Player;
import Entity.Player.PlayerSave;
import Entity.Player.TreeStump;

import Audio.AudioPlayer;

import java.awt.*;
import java.util.ArrayList;

import GameState.GameStateManager;
import Handlers.Keys;


public class Level1AState extends GameState {
	
	private Background sky, clouds, mountains;
	
	private Player player;
	private TileMap tileMap;
	
	private ArrayList<Enemy> enemies;
	private ArrayList<Explosion> explosions;
	private ArrayList<TreeStump> treestump;
	
	private HUD hud;
	
	// events
	private boolean blockInput = false;
	private int eventCount = 0;
	private boolean eventStart;
	private ArrayList<Rectangle> transitionBox;
	private boolean eventFinish;
	private boolean eventDead;
	
	public Level1AState(GameStateManager gsm) {
		super(gsm);
		init();
	}
	
	public void init() {
		
		//tilemap
		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/grasstileset.gif");
		tileMap.loadMap("/Maps/level1A.map");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);
		
		//background
		sky = new Background("/Backgrounds/sky.gif", 0);
		clouds = new Background("/Backgrounds/clouds.gif", 1);
		mountains = new Background("/Backgrounds/mountains1.gif", 0.2);
		
		clouds.setVector(-0.02, 0);
		
		//player
		player = new Player(tileMap);
		player.setPosition(100, 100);
		player.setHealth(PlayerSave.getHealth());
		player.setLives(PlayerSave.getLives());
		
		//Enemies
		enemies = new ArrayList<Enemy>();
		populateEnemies();
		
		//explosions
		explosions = new ArrayList<Explosion>();
		
		//Hud
		hud = new HUD(player);
		
		//treestump
		treestump = new ArrayList<TreeStump>();
		
		// start event
		eventStart = true;
		transitionBox = new ArrayList<Rectangle>();
		eventStart();
		
		AudioPlayer.load("/Music/level1-1.mp3", "level1");
		AudioPlayer.load("/SFX/menuselect.mp3", "GamePaused");
		AudioPlayer.load("/SFX/Player/treestump.mp3", "fakeDeath");
		//AudioPlayer.play("level1");
		
	}
	
	private void populateEnemies() {
		
		enemies.clear();
		
		Slugger s;
		Point[] points = new Point[] {
			new Point(860, 200),
			new Point(1525, 200),
			new Point(1680, 200),
			new Point(1800, 200),
			new Point(2300, 200),
			new Point(2400, 200),
		};
		
		for(int i = 0; i < points.length; i++) {
			s = new Slugger(tileMap);
			s.setPosition(points[i].x, points[i].y);
			enemies.add(s);
		}
		
		points = null;
		
		Spider sp;
		points = new Point[] {
				new Point(200, 200),
				new Point(1970, 75),
				new Point(2000, 120),
				new Point(2030, 100),
				new Point(2060, 80),
		};
		
		for(int i = 0; i < points.length; i++) {
			sp = new Spider(tileMap);
			sp.setPosition(points[i].x, points[i].y);
			enemies.add(sp);
		};
	};
		
	public void update() {
		
		// check keys
		handleInput();
		
		/**
		// check if end of level event should start
				if(teleport.contains(player)) {
					eventFinish = blockInput = true;
				}**/
				
		// check if player dead event should start
			if(player.getHealth() == 0 || player.gety() > tileMap.getHeight()) {
				player.setDead();
				eventDead = blockInput = true;
		}
			
		// play events
			if(eventStart) eventStart();
			if(eventDead) eventDead();
			if(eventFinish) eventFinish();
		
		/**
		// move title and subtitle
		if(title != null) {
			title.update();
			if(title.shouldRemove()) title = null;
		}
		if(subtitle != null) {
			subtitle.update();
			if(subtitle.shouldRemove()) subtitle = null;
		}
				**/
		
		// set background
		clouds.update();
		mountains.setPosition(tileMap.getx(), tileMap.gety());
		
		// update player
		player.update();
		
		//update Tilemap
		tileMap.setPosition(
			GamePanel.WIDTH / 2 - player.getx(),
			GamePanel.HEIGHT / 2 - player.gety()
		);
		tileMap.update();
		tileMap.fixBounds();
			
		// attack enemies
		player.checkAttack(enemies);
		
		// update all enemies
		for(int i = 0; i < enemies.size(); i++) {
			Enemy e = enemies.get(i);
			e.update();
			if(e.isDead()) {
				enemies.remove(i);
				i--;
				explosions.add(new Explosion(/*tileMap,*/ e.getx(), e.gety()));
			}
		}
		/*
		// update enemy projectiles
				for(int i = 0; i < eprojectiles.size(); i++) {
					EnemyProjectile ep = eprojectiles.get(i);
					ep.update();
					if(ep.shouldRemove()) {
						eprojectiles.remove(i);
						i--;
					}
				}
		*/
		
		// update explosions
		for(int i = 0; i < explosions.size(); i++) {
			explosions.get(i).update();
			if(explosions.get(i).shouldRemove()) {
				explosions.remove(i);
				i--;
			}
		}
		
		// update treestump
		for(int i = 0; i < treestump.size(); i++) {
			treestump.get(i).update();
			if(treestump.get(i).shouldRemove()) {
				treestump.remove(i);
				i--;
			}
		}
		
		// update teleport
		//	teleport.update();
	}
	
	public void draw(Graphics2D g) {
		
		// draw bg
		sky.draw(g);
		clouds.draw(g);
		mountains.draw(g);
		
		// draw tilemap
		tileMap.draw(g);
		
		// draw enemies
		for(int i = 0; i < enemies.size(); i++) {
			enemies.get(i).draw(g);
		}
		/**
		// draw enemy projectiles
				for(int i = 0; i < eprojectiles.size(); i++) {
					eprojectiles.get(i).draw(g);
				}**/
		
		// draw explosions
		for(int i = 0; i < explosions.size(); i++) {
			explosions.get(i).setMapPosition(
				(int)tileMap.getx(), (int)tileMap.gety());
			explosions.get(i).draw(g);
		}
		// draw treestump
		for(int i = 0; i < treestump.size(); i++) {
			treestump.get(i).setMapPosition(
				(int)tileMap.getx(), (int)tileMap.gety()-30);
			treestump.get(i).draw(g);
		}
		
		// draw player
		if (treestump.isEmpty() && !player.fakeDeath())
		player.draw(g);
		
		// draw hud
		hud.draw(g);
		
		// draw transition boxes
		g.setColor(java.awt.Color.BLACK);
		for(int i = 0; i < transitionBox.size(); i++) {
			g.fill(transitionBox.get(i));
		}
		
	}

	@Override
	public void handleInput() {
		
		if(Keys.isPressed(Keys.ESCAPE)) {
			AudioPlayer.play("GamePaused");
			gsm.setPaused(true);
		}
		if(Keys.isPressed(Keys.ENTER) && player.getHealth()==0 && player.getLives()>0)player.setFakeDeath(true);;
		
		if(blockInput || player.getHealth() == 0) return;
		player.setUp(Keys.keyState[Keys.UP]);
		player.setDown(Keys.keyState[Keys.DOWN]);
		player.setLeft(Keys.keyState[Keys.LEFT]);
		player.setRight(Keys.keyState[Keys.RIGHT]);
		player.setJumping(Keys.keyState[Keys.BUTTON1]);
		player.setGliding(Keys.keyState[Keys.BUTTON2]);
		if(Keys.isPressed(Keys.BUTTON3)) player.setScratching();
		if(Keys.isPressed(Keys.BUTTON4)) player.setFiring();
	}
	
///////////////////////////////////////////////////////
////////////////////EVENTS
///////////////////////////////////////////////////////
	
	// reset level
	private void reset() {
		player.reset();
		player.setPosition(100, 100);
		populateEnemies();
		blockInput = true;
		eventCount = 0;
		tileMap.setShaking(false, 0);
		eventStart = true;
		eventStart();
//		title = new Title(hageonText.getSubimage(0, 0, 178, 20));
//		title.sety(60);
//		subtitle = new Title(hageonText.getSubimage(0, 33, 91, 13));
//		subtitle.sety(85);
	}
		
	// level started
	private void eventStart() {
		player.setFakeDeath(false);
		
		eventCount++;
		if(eventCount == 1) {
			transitionBox.clear();
			transitionBox.add(new Rectangle(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
			transitionBox.add(new Rectangle(0, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
			transitionBox.add(new Rectangle(0, GamePanel.HEIGHT / 2, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
			transitionBox.add(new Rectangle(GamePanel.WIDTH / 2, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
		}
		if(eventCount > 1 && eventCount < 60) {
			transitionBox.get(0).height -= 4;
			transitionBox.get(1).width -= 6;
			transitionBox.get(2).y += 4;
			transitionBox.get(3).x += 6;
		}
		//if(eventCount == 30) title.begin();
		if(eventCount == 60) {
			eventStart = blockInput = false;
			eventCount = 0;
			//subtitle.begin();
			transitionBox.clear();
		}
	}

	
	// player has died
	private void eventDead() {

		if(player.isKnockback())return;
		if(!player.fakeDeath() && player.getLives() >0 )return;
		
		if(treestump.isEmpty() && player.getLives() > 0 && eventCount == 0 ){
			treestump.add(new TreeStump( player.getx(), player.gety()));
			AudioPlayer.play("fakeDeath");
		}
		
		eventCount++;
		
		if(eventCount == 1) {
			player.setDead();
			player.stop();
		}
		if(eventCount == 180) {
			transitionBox.clear();
			transitionBox.add(new Rectangle(
				GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
		}
		else if(eventCount > 180) {
			transitionBox.get(0).x -= 6;
			transitionBox.get(0).y -= 4;
			transitionBox.get(0).width += 12;
			transitionBox.get(0).height += 8;
		}
		if(eventCount >= 240) {
			if(player.getLives() == 0) {
				gsm.setState(GameStateManager.MENUSTATE);
			}
			else {
				eventDead = blockInput = false;
				eventCount = 0;
				player.loseLife();
				reset();
			}
		}
	}
	
	// finished level
	private void eventFinish() {
		eventCount++;
		if(eventCount == 1) {
			AudioPlayer.play("teleport");
			//player.setTeleporting(true);
			player.stop();
		}
		else if(eventCount == 120) {
			transitionBox.clear();
			transitionBox.add(new Rectangle(
				GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
		}
		else if(eventCount > 120) {
			transitionBox.get(0).x -= 6;
			transitionBox.get(0).y -= 4;
			transitionBox.get(0).width += 12;
			transitionBox.get(0).height += 8;
			AudioPlayer.stop("teleport");
		}
		if(eventCount == 180) {
			PlayerSave.setHealth(player.getHealth());
			PlayerSave.setLives(player.getLives());
			//PlayerSave.setTime(player.getTime());
			//gsm.setState(GameStateManager.LEVEL1BSTATE);
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}












