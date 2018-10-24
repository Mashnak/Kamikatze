package Entity.Player;

import TileMap.TileMap;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import Entity.Animation;
import Entity.MapObject;
import Handlers.Content;

public class Shurikan extends MapObject {
	
	private static int cost = 200;
	private static int damage = 5;
	private boolean hit;
	private boolean remove;
	private BufferedImage[] sprites;
	private BufferedImage[] hitSprites;
	
	public Shurikan(TileMap tm, boolean right) {
		
		super(tm);
		
		facingRight = right;
		
		moveSpeed = 3.8;
		if(right) dx = moveSpeed;
		else dx = -moveSpeed;
		
		width = 13;
		height = 13;
		cwidth = 10;
		cheight = 10;
		
		sprites = Content.Shurikan[0];
		hitSprites = Content.Shurikan[1];
		
			animation = new Animation();
			animation.setFrames(sprites);
			animation.setDelay(50);
			
	}
	
	public void setHit() {
		if(hit) return;
		hit = true;
		animation.setFrames(hitSprites);
		animation.setDelay(70);
		dx = 0;
	}
	
	public static int getDamage() {return damage;}
	public static int getCost() {return cost;}
	public boolean isHit() { return hit; }
	public boolean shouldRemove() { return remove; }
	
	public void update() {
		
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		if(dx == 0 && !hit) {
			setHit();
		}
		
		animation.update();
		if(hit && animation.hasPlayedOnce()) {
			remove = true;
		}
		
	}
	
	public void draw(Graphics2D g) {
		
		setMapPosition();
		
		super.draw(g);
		
	}
	
}


















