package GameState;

import TileMap.Background;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import Audio.AudioPlayer;
import Entity.Animation;
import Entity.Player.PlayerSave;
import Handlers.Keys;

public class MenuState extends GameState {
	
	private Background bg;
	private BufferedImage spriteSheet;
	private BufferedImage[] paw, pawReverse;
	private int pawChoice;
	private int numFrames = 6;
	private Animation animation;
	
	private int currentChoice = 0;
	private String[] options = {
		"Start",
		"Quit"
	};
	
	private Color titleColor;
	private Font titleFont;
	
	private Font font,
				 font2;
	
	public MenuState(GameStateManager gsm) {
		
		super(gsm);
		
		try {
			
			//Background
			bg = new Background("/Backgrounds/menubg.gif", 1);
			bg.setVector(-0.1, 0);
		
			spriteSheet = ImageIO.read(getClass().getResourceAsStream("/Sprites/Player/CatPaw.gif"));
			
			
			//Paw
			paw = new BufferedImage[numFrames];
			pawReverse = new BufferedImage[numFrames];
			pawChoice=1;
				
				for(int j = 0; j < numFrames; j++) {
															
						paw[j] = spriteSheet.getSubimage(
								j * 125,
								0,
								125,
								125
						);	
						
						pawReverse[paw.length-j-1]=paw[j];
	
				}
			
			animation = new Animation();
			animation.setFrames(paw);
			animation.setDelay(400);
			
			
			//Fonts
			titleColor = new Color(128, 0, 0);
			titleFont = new Font(
					"Century Gothic",
					Font.PLAIN,
					28);
			
			font = new Font("Times New Roman", Font.PLAIN, 12);
			font2 = new Font("Arial", Font.PLAIN, 10);
			
			//load Sound
			AudioPlayer.load("/SFX/menuoption.mp3", "menuoption");
			AudioPlayer.load("/SFX/menuselect.mp3", "menuselect");
			
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		animation.setFrames(paw);
		animation.setDelay(150);
	}
	
	
	public void init() {}
	
	public void update() {
		
		// check keys
		handleInput();
				
		bg.update();
		
		animation.update();
		if(animation.hasPlayedOnce())animation.setFrames(getPaws());
		
	}
	

	public void draw(Graphics2D g) {
		
		// draw bg
		bg.draw(g);
		
		//CatPaw
		g.drawImage(animation.getImage(), 195, 115, null);
		
		// draw title
		g.setColor(titleColor);
		g.setFont(titleFont);
		g.drawString("kamiKatze", 90, 70);
		
		// draw menu options
		g.setFont(font);
		for(int i = 0; i < options.length; i++) {
			if(i == currentChoice) {
				g.setColor(Color.RED);
			}
			else {
				g.setColor(Color.BLACK);
			}
			g.drawString(options[i], 145, 140 + i * 15);
		}
		
		// other
		g.setColor(Color.GRAY);
		g.setFont(font2);
		g.drawString("WS 2016/17 Maxim K. , Markus S.", 5, 232);
	}
	
	
	private void select() {
		if(currentChoice == 0) {
			AudioPlayer.play("menuselect");
			PlayerSave.init();
			gsm.setState(GameStateManager.LEVEL1ASTATE);
		}
		if(currentChoice == 1) {
			System.exit(0);
		}
	}
	
	
	/**
	 * Paw Animation only in one Way
	 * to avoid One-Way Animation we return the reversed Images
	 * 
	 */
	private BufferedImage[] getPaws() {
		if(pawChoice==0){pawChoice=1; return paw;}
		if(pawChoice==1){pawChoice=0; return pawReverse;}
		return null;
	}
	
	
	public void handleInput() {
		if(Keys.isPressed(Keys.ENTER)) select();
		if(Keys.isPressed(Keys.UP)) {
			if(currentChoice > 0) {
				AudioPlayer.play("menuoption", 0);
				currentChoice--;
			}
		}
		if(Keys.isPressed(Keys.DOWN)) {
			if(currentChoice < options.length - 1) {
				AudioPlayer.play("menuoption", 0);
				currentChoice++;
			}
		}
		
	}
	
}










