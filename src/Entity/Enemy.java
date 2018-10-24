package Entity;

import Audio.AudioPlayer;
import TileMap.TileMap;

public class Enemy extends MapObject {
	
	protected int health;
	protected int maxHealth;
	protected boolean dead;
	protected int damage;
	
	protected boolean flinching;
	protected long flinchTimer;
	
	public Enemy(TileMap tm) {
		super(tm);
		AudioPlayer.load("/SFX/Enemies/explode.mp3", "explode");
		AudioPlayer.load("/SFX/Enemies/enemyhit.mp3", "enemyhit");
	}
	
	public boolean isDead() { return dead; }
	
	public int getDamage() { return damage; }
	
	public void hit(int damage) {
		if(dead || flinching) return;
		AudioPlayer.play("enemyhit");
		health -= damage;
		if(health < 0) health = 0;
		if(health == 0) {AudioPlayer.play("explode");dead = true;}
		flinching = true;
		flinchTimer = System.nanoTime();
	}
	
	public void update() {}
	
}














