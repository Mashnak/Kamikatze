package GameState;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import Audio.AudioPlayer;
import Handlers.Keys;
import Main.GamePanel;


public class PauseState extends GameState {
	
	private Font titleFont,
				 optionFont;
	
	private int currentChoice = 0;
	private String[] options = {
			"Resume",
			"  Acid"	,
			" Menu" 
			
	};
	
	public PauseState(GameStateManager gsm) {
		
		super(gsm);
		
		// fonts
		titleFont = new Font("Century Gothic", Font.PLAIN, 14);
		optionFont = new Font("Times New Roman",Font.PLAIN, 12);
		
		//sounds
		AudioPlayer.load("/SFX/menuselect.mp3", "menuselect");
		AudioPlayer.load("/SFX/menuoption.mp3", "menuoption");
	}
	
	public void init() {}
	
	public void update() {
		handleInput();
	}
	
	public void draw(Graphics2D g) {
		
		//draw BG
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);
		
		//draw Title
		g.setColor(Color.WHITE);
		g.setFont(titleFont);
		g.drawString("Game Paused", 115, 70);
		
		//draw options
		g.setFont(optionFont);
		for(int i = 0; i < options.length; i++) {
			if(i == currentChoice) {
				g.setColor(Color.RED);
			}
			else {
				g.setColor(Color.darkGray);
			}
			g.drawString(options[i], 140, 140 + i * 15);
		}
	}
	
	private void select() {
		if(currentChoice == 0) {
			AudioPlayer.play("menuselect");
			gsm.setPaused(false);
		}
		if(currentChoice == 1) {
			AudioPlayer.play("menuselect");
			gsm.setPaused(false);
			gsm.setState(GameStateManager.ACIDSTATE);
		}
		if(currentChoice == 2) {
			AudioPlayer.play("menuselect");
			gsm.setPaused(false);
			gsm.setState(GameStateManager.MENUSTATE);
		}
	}
	
	public void handleInput() {
		
		if(Keys.isPressed(Keys.ENTER)) select();
		
		if(Keys.isPressed(Keys.ESCAPE)) {
			AudioPlayer.play("menuselect");
			gsm.setPaused(false);
		}
		
		if(Keys.isPressed(Keys.BUTTON1)) {
			AudioPlayer.play("menuselect");
			gsm.setPaused(false);
			gsm.setState(GameStateManager.MENUSTATE);
		}
		
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
