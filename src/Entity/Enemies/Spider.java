package Entity.Enemies;

import Entity.*;
import Handlers.Content;
import TileMap.TileMap;

import java.awt.image.BufferedImage;
import java.awt.Graphics2D;

public class Spider extends Enemy {
	
	private BufferedImage[] sprites;
	
	public Spider(TileMap tm) {
		
		super(tm);
		
		moveSpeed = 0.3;
		maxSpeed = 1;
		fallSpeed = 0.2;
		maxFallSpeed = 10.0;
		
		width = 30;
		height = 30;
		cwidth = 20;
		cheight = 20;
		
		health = maxHealth = 15;
		damage = 1;
		
		sprites = Content.Spider[0];
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(300);
		
		right = true;
		facingRight = true;
		
	}
	
	private void getNextPosition() {
		
		// 
		
	}
	
	public void update() {
		
		// update position
		getNextPosition();
		checkTileMapCollision();
		setPosition(xtemp, ytemp);
		
		// check flinching
		if(flinching) {
			long elapsed =
				(System.nanoTime() - flinchTimer) / 1000000;
			if(elapsed > 400) {
				flinching = false;
			}
		}
		
		// if it hits a wall, go other direction
		if(up && y == 40) {
			up = false;
			down = true;
		}
		else if(down && y == 130) {
			up = true;
			down = false;
			
		}
		
		// update animation
		animation.update();
		
	}
	
	public void draw(Graphics2D g) {
		
		//if(notOnScreen()) return;
		
		setMapPosition();
		
		super.draw(g);
		
	}
	
}











