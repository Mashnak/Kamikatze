package Entity.Player;

import TileMap.*;

import java.util.ArrayList;
import javax.imageio.ImageIO;

import Audio.AudioPlayer;
import Entity.Enemy;
import Entity.MapObject;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends MapObject {
	
	// player stuff
	private int lives;
	private int health;
	private int maxHealth;
	private int ammo;
	private int maxAmmo;
	private int score;
	private boolean teleporting;
	private boolean flinching;
	private boolean knockback;
	private boolean fakeDeath;
	private long flinchTimer;
	
	// shurikan
	private boolean firing;
	private ArrayList<Shurikan> shurikans;
	
	// scratch
	private boolean scratching;
	private int scratchDamage;
	private int scratchRange;
	
	// gliding
	private boolean gliding;
	
	// animations
	private ArrayList<BufferedImage[]> sprites;
	private final int[] NUMFRAMES = {
		2, 8, 1, 2, 4, 2, 5, 1, 1
	};
	private final int[] FRAMEWIDTHS = {
		30, 30, 30, 30, 30, 30, 60, 30, 30
		};
	private final int[] FRAMEHEIGHTS = {
		30, 30, 30, 30, 30, 30, 30, 30, 30
	};
	private final int[] SPRITEDELAYS = {
		400, 40, -1, 100, 50, 100, 75, -1, -1
	};
	
	// animation actions
	private static final int IDLE = 0;
	private static final int WALKING = 1;
	private static final int JUMPING = 2;
	private static final int FALLING = 3;
	private static final int GLIDING = 4;
	private static final int SHURIKAN = 5;
	private static final int SCRATCHING = 6;
	private static final int KNOCKBACK = 7;
	private static final int DEAD = 8;
	
	// emotes
	private BufferedImage confused;
	private BufferedImage surprised;
	public static final int NONE = 0;
	public static final int CONFUSED = 1;
	public static final int SURPRISED = 2;
	private int emote = NONE;
	
	
	public Player(TileMap tm) {
		
		super(tm);
		
		width = 30;
		height = 30;
		cwidth = 20;
		cheight = 20;
		
		moveSpeed = 0.3;
		maxSpeed = 1.6;
		stopSpeed = 0.4;
		fallSpeed = 0.15;
		maxFallSpeed = 4.0;
		jumpStart = -4.8;
		stopJumpSpeed = 0.3;
		
		facingRight = true;
		
		lives = 3;
		health = maxHealth = 5;
		ammo = maxAmmo = 2500;
		
		shurikans = new ArrayList<Shurikan>();
		
		scratchDamage = 8;
		scratchRange = 40;
		
		// load sprites
		try {
			
			BufferedImage spritesheet = ImageIO.read(
				getClass().getResourceAsStream(
					"/Sprites/Player/playersprites.gif"
				)
			);
			
			int count = 0;
			sprites = new ArrayList<BufferedImage[]>();
			for(int i = 0; i < NUMFRAMES.length; i++) {
				
				BufferedImage[] bi = new BufferedImage[NUMFRAMES[i]];
				
				for(int j = 0; j < NUMFRAMES[i]; j++) {
					
						bi[j] = spritesheet.getSubimage(
								j * FRAMEWIDTHS[i],
								count,
								FRAMEWIDTHS[i],
								FRAMEHEIGHTS[i]
						);
				}
				
				sprites.add(bi);
				count += FRAMEHEIGHTS[i];
				
			}
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		setAnimation(IDLE);
		
		//---------------------------------------------------
		
		AudioPlayer.load("/SFX/Player/jump.mp3", "jump");
		AudioPlayer.load("/SFX/Player/scratch.mp3", "scratch");
		AudioPlayer.load("/SFX/Player/fly.mp3", "gliding");
		AudioPlayer.load("/SFX/Player/attack.mp3", "attack");
		AudioPlayer.load("/SFX/Player/hit.mp3", "hit");
		AudioPlayer.load("/SFX/Player/lands.mp3", "lands");
		
	}
	//------------------------------------------------------
	
	public int getHealth() { return health; }
	public int getMaxHealth() { return maxHealth; }
	public int getAmmo() { return ammo; }
	public int getMaxAmmo() { return maxAmmo; }
	
	public void setHealth(int i){health = i;}
	public void setLives(int i){lives = i;}
	public void gainLife(){ lives++;}
	public void loseLife(){ lives--;}
	public int getLives(){return lives;}
	
	public void setEmote(int i) { emote = i;}
	
	public void setTeleporting(boolean b) { teleporting = b; }
	public void setJumping(boolean b){if(knockback)return;jumping = b;}
	
	public void setFiring() {if(knockback)return; firing = true;}
	public void setScratching() {if(knockback)return; scratching = true;}
	public void setGliding(boolean b) { gliding = b;}
	public boolean isKnockback(){return knockback;}
	
	public void setFakeDeath(boolean b){fakeDeath=b;}
	public boolean fakeDeath(){return fakeDeath;}
	
			
	public void setDead(){
		health = 0;
		stop();
	}
	
	public int getScore(){ return score;}
	public void increaseScore(int score) {this.score += score;}
	
	public void checkAttack(ArrayList<Enemy> enemies) {
		
		// loop through enemies
		for(int i = 0; i < enemies.size(); i++) {
			
			Enemy e = enemies.get(i);
			
			// scratch attack
			if(scratching) {
				if(facingRight) {
					if(
						e.getx() > x &&
						e.getx() < x + scratchRange && 
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.hit(scratchDamage);
					}
				}
				else {
					if(
						e.getx() < x &&
						e.getx() > x - scratchRange &&
						e.gety() > y - height / 2 &&
						e.gety() < y + height / 2
					) {
						e.hit(scratchDamage);
					}
				}
			}
			
			// shurikans
			for(int j = 0; j < shurikans.size(); j++) {
				if(shurikans.get(j).intersects(e)) {
					e.hit(Shurikan.getDamage());
					shurikans.get(j).setHit();
					break;
				}
			}
			
			// check enemy collision
			if(intersects(e) && health!=0) {
				hit(e.getDamage());
			}
			
		}
		
	}
	
	//Player gets hit
	public void hit(int damage) {
		if(flinching) return;
		AudioPlayer.play("hit");
		stop();
		health -= damage;
		if(health < 0) health = 0;
		flinching = true;
		flinchTimer = System.nanoTime();
		if(facingRight) dx = -1;
		else dx = 1;
		dy = -3;
		knockback = true;
		falling = true;
		jumping = false;
	}
	
	public void reset() {
		health = maxHealth;
		facingRight = true;
		currentAction = -1;
		stop();
	}
	
	public void stop() {
		left = right = up = down = 
				flinching = scratching = firing = jumping = false;
	}
	
	private void getNextPosition() {
		
		if(knockback) {
			dy += fallSpeed * 2;
			if(!falling) knockback = false;
			return;
		}
		
		// movement
		if(left) {
			dx -= moveSpeed;
			if(dx < -maxSpeed) {
				dx = -maxSpeed;
			}
		}
		else if(right) {
			dx += moveSpeed;
			if(dx > maxSpeed) {
				dx = maxSpeed;
			}
		}
		else {
			if(dx > 0) {
				dx -= stopSpeed;
				if(dx < 0) {
					dx = 0;
				}
			}
			else if(dx < 0) {
				dx += stopSpeed;
				if(dx > 0) {
					dx = 0;
				}
			}
		}
		
		// cannot move while attacking, except in air
		if(
		(scratching || firing) &&
		!(jumping || falling)) {
			dx = 0;
		}
		
		// jumping
		if(jumping && !falling) {
			dy = jumpStart;
			falling = true;	
			AudioPlayer.play("jump");
		}
		
		// falling
		if(falling) {
			
			if(dy > 0 && gliding) dy += fallSpeed * 0.1;
			else dy += fallSpeed;
			
			if(dy > 0) jumping = false;
			if(dy < 0 && !jumping) dy += stopJumpSpeed;
			
			if(dy > maxFallSpeed) dy = maxFallSpeed;
			
		}
		
	}
	
	private void setAnimation(int i) {
		currentAction = i;
		animation.setFrames(sprites.get(currentAction));
		animation.setDelay(SPRITEDELAYS[currentAction]);
		width = FRAMEWIDTHS[currentAction];
		height = FRAMEHEIGHTS[currentAction];
	}
	
	public void update() {
		
		// update position
		boolean isFalling = falling;
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		if(isFalling && !falling) {
			AudioPlayer.play("lands");
		}
		if(dx == 0) x = (int)x;
		
		//check gliding has stopped
		if(!gliding)AudioPlayer.stop("gliding");
		
		// check attack has stopped
		if(currentAction == SCRATCHING) {
			if(animation.hasPlayedOnce()) scratching = false;
		}
		if(currentAction == SHURIKAN) {
			if(animation.hasPlayedOnce()) firing = false;
		}
		
		// shurikan attack
		ammo += 1;
		if(ammo > maxAmmo) ammo = maxAmmo;
		if(firing && currentAction != SHURIKAN) {
			if(ammo > Shurikan.getCost()) {
				AudioPlayer.play("attack");
				ammo -= Shurikan.getCost();
				Shurikan shurikan = new Shurikan(tileMap, facingRight);
				shurikan.setPosition(x, y);
				shurikans.add(shurikan);
			}
		}
		
		// update shurikans
		for(int i = 0; i < shurikans.size(); i++) {
			shurikans.get(i).update();
			if(shurikans.get(i).shouldRemove()) {
				shurikans.remove(i);
				i--;
			}
		}
		
		// check done flinching
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 2000) { 		//2000 for 2sec flinching
				flinching = false;
			}
		}
		// check attack finished
			if(currentAction == SCRATCHING) {
					if(animation.hasPlayedOnce())
					scratching = false;
			}
		
		// set animation, ordered by priority			
		if(scratching) {
			if(currentAction != SCRATCHING) {
				AudioPlayer.play("scratch");
				setAnimation(SCRATCHING);
			}
		}
		else if(firing) {
			if(currentAction != SHURIKAN)
				setAnimation(SHURIKAN);	
		}
		else if(dy > 0) {		
			if(gliding) {				
				if(currentAction != GLIDING) {
					AudioPlayer.play("gliding");
					setAnimation(GLIDING);	
				}
			}
			else if(currentAction != FALLING) 
					setAnimation(FALLING);
			
		}
		else if(dy < 0) {
			if(currentAction != JUMPING) 
				setAnimation(JUMPING);
			
		}
		else if(left || right) {
			if(currentAction != WALKING) 
				setAnimation(WALKING);
			
		}
		else {
			if(currentAction != IDLE) 
				setAnimation(IDLE);
			
		}
		if(health == 0) {
			if(currentAction != DEAD) 
				setAnimation(DEAD);
		}
		if(knockback){
			if(currentAction!=KNOCKBACK)
				setAnimation(KNOCKBACK);
		}
		
		animation.update();
		
		// set direction
		if(currentAction != SCRATCHING && currentAction != SHURIKAN && !knockback) {
			if(right) facingRight = true;
			if(left) facingRight = false;
		}
		
	}
	
	public void draw(Graphics2D g) {
		
		setMapPosition();
		
		// draw shurikans
		for(int i = 0; i < shurikans.size(); i++) {
			shurikans.get(i).draw(g);
		}
		
		// draw player
		if(flinching && !knockback) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed / 100 % 2 == 0) {
				return;
			}
		}
		
		super.draw(g);
		
	}
	
}

















