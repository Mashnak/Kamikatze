package Entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import Entity.Player.Player;
import Entity.Player.PlayerSave;
import GameState.GameState;

public class HUD {
	
	private Player player;
	
	private BufferedImage 	hud,
							icons,
							heart, 
							treestump, 
							shurikan; 
	private Font font;
	
	public HUD(Player p) {
		player = p;
		try {
			
			hud = ImageIO.read(
				getClass().getResourceAsStream(
					"/HUD/hud.gif"
				));
			
			icons = ImageIO.read(
				getClass().getResourceAsStream(
					"/HUD/icons.gif"
				));
			treestump = icons.getSubimage(0, 0, 13, 13);
			heart = icons.getSubimage(13, 0, 13, 13);
			shurikan = icons.getSubimage(26, 0, 13, 13);
			
			font = new Font("Arial", Font.PLAIN, 14);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void draw(Graphics2D g) {
		
		//draw hud
		g.drawImage(hud, 0, 1, null);
		g.drawImage(hud, 0, 21, null);
		g.drawImage(hud, 0, 41, null);
		g.setFont(font);
		g.setColor(Color.WHITE);
		
		
		//draw icons of health lives and ammo
		for(int i = 0; i < player.getHealth(); i++) {
			g.drawImage(heart, 1 + i * 15, 4, null);
		}
		
		for(int i = 0; i < player.getLives(); i++) {
			g.drawImage(treestump, 1 + i * 15, 24, null);
		}
		
		g.drawImage(shurikan, 7, 44, null);
		
		
		//draw numbers of health&lives
//		g.drawString(
//			player.getHealth() + "/" + player.getMaxHealth(),
//			80,
//			15
//		);
//		
//		g.drawString(
//			player.getLives() + "/" + PlayerSave.getLives(),
//			80,
//			35
//		);
		
		//draw numbers of ammo
		g.drawString(
			player.getAmmo() / 100 + "/" + player.getMaxAmmo() / 100,
			25,
			55
		);
		
		if(!player.fakeDeath() && player.getLives() >0 && player.getHealth() == 0)g.drawString("press <ENTER> to use Treestump", 60, 100);
		
		//draw coordinates of player
		g.drawString("X:" + player.getx(), 270, 10);
		g.drawString("Y:" + player.gety(), 270, 21);
		
	}
	
}













