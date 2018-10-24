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


public class Level1State extends GameState {
	
	private Background bg;
	
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
	private ArrayList<Rectangle> tb;
	private boolean eventFinish;
	private boolean eventDead;
	
	public Level1State(GameStateManager gsm) {
		super(gsm);
		init();
	}
	
	public void init() {
		
		tileMap = new TileMap(30);
		tileMap.loadTiles("/Tilesets/grasstileset.gif");
		tileMap.loadMap("/Maps/level1-1.map");
		tileMap.setPosition(0, 0);
		tileMap.setTween(1);
		
		bg = new Background("/Backgrounds/grassbg1.gif", 0.1);
		
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
		tb = new ArrayList<Rectangle>();
		eventStart();
		
		//bgMusic = new AudioPlayer("/Music/level1-1.mp3");
		//bgMusic.play();
		AudioPlayer.load("/SFX/menuselect.mp3", "GamePaused");
		
	}
	
	private void populateEnemies() {
		
		enemies.clear();
		
		Slugger s;
		Point[] points = new Point[] {
			new Point(860, 200),
			new Point(1525, 200),
			new Point(1680, 200),
			new Point(1800, 200)
		};
		for(int i = 0; i < points.length; i++) {
			s = new Slugger(tileMap);
			s.setPosition(points[i].x, points[i].y);
			enemies.add(s);
		}
		
	}
	
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
		bg.setPosition(tileMap.getx(), tileMap.gety());
		
		// update player
		if(treestump.isEmpty() || !eventDead)
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
		bg.draw(g);
		
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
		if(treestump.isEmpty() && !eventDead)
			player.draw(g);
		
		// draw hud
		hud.draw(g);
		
		// draw transition boxes
		g.setColor(java.awt.Color.BLACK);
		for(int i = 0; i < tb.size(); i++) {
			g.fill(tb.get(i));
		}
		
	}

	@Override
	public void handleInput() {
		
		if(Keys.isPressed(Keys.ESCAPE)) {
			AudioPlayer.play("GamePaused");
			gsm.setPaused(true);
		}
		
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
		eventCount++;
		if(eventCount == 1) {
			tb.clear();
			tb.add(new Rectangle(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
			tb.add(new Rectangle(0, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
			tb.add(new Rectangle(0, GamePanel.HEIGHT / 2, GamePanel.WIDTH, GamePanel.HEIGHT / 2));
			tb.add(new Rectangle(GamePanel.WIDTH / 2, 0, GamePanel.WIDTH / 2, GamePanel.HEIGHT));
		}
		if(eventCount > 1 && eventCount < 60) {
			tb.get(0).height -= 4;
			tb.get(1).width -= 6;
			tb.get(2).y += 4;
			tb.get(3).x += 6;
		}
		//if(eventCount == 30) title.begin();
		if(eventCount == 60) {
			eventStart = blockInput = false;
			eventCount = 0;
			//subtitle.begin();
			tb.clear();
		}
	}

	
	// player has died
	private void eventDead() {
		if(player.isKnockback()){	
			return;
		}
		player.stop();
			
		if(treestump.isEmpty() && eventCount == 0 )treestump.add(new TreeStump( player.getx(), player.gety()));
		
		eventCount++;
		
		if(eventCount == 1) {
			player.setDead();
			//player.stop();
		}
		if(eventCount == 600) {
			tb.clear();
			tb.add(new Rectangle(
				GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
		}
		else if(eventCount > 600) {
			tb.get(0).x -= 6;
			tb.get(0).y -= 4;
			tb.get(0).width += 12;
			tb.get(0).height += 8;
		}
		if(eventCount >= 660) {
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
			tb.clear();
			tb.add(new Rectangle(
				GamePanel.WIDTH / 2, GamePanel.HEIGHT / 2, 0, 0));
		}
		else if(eventCount > 120) {
			tb.get(0).x -= 6;
			tb.get(0).y -= 4;
			tb.get(0).width += 12;
			tb.get(0).height += 8;
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












