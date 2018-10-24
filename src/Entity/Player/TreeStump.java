package Entity.Player;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import Entity.Animation;
import Handlers.Content;

public class TreeStump {
	private int x;
	private int y;
	private int xmap;
	private int ymap;
	
	private int width;
	private int height;
	
	private Animation animation;
	private BufferedImage[] sprites;
	
	private boolean remove;
	
	public TreeStump(int x, int y) {
		
		this.x = x;
		this.y = y;
		
		width = 100;
		height = 100;
		
		sprites = Content.Treestump[0];
		
		animation = new Animation();
		animation.setFrames(sprites);
		animation.setDelay(100);
		
	}
	
	public void update() {
		animation.update();
		if(animation.hasPlayedOnce()) {
			remove = true;
		}
	}
	
	public boolean shouldRemove() { return remove; }
	
	public void setMapPosition(int x, int y) {
		xmap = x;
		ymap = y;
	}
	
	public void draw(Graphics2D g) {
		g.drawImage(
			animation.getImage(),
			x + xmap - width / 2,
			y + ymap - height / 2,
			null
		);
	}
	
}
















