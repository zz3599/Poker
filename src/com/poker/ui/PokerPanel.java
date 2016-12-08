package com.poker.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import com.poker.lib.GameEngine;
import com.poker.lib.Player;
import com.poker.state.AbstractPokerGameState.GAMESTATE;
import com.poker.ui.listener.button.BetButtonMouseListener;
import com.poker.ui.listener.button.CheckOrCallButtonMouseListener;
import com.poker.ui.listener.button.FoldButtonMouseListener;
import com.poker.ui.listener.button.GameStateButtonMouseListener;
import com.poker.ui.listener.slider.BettingSliderChangeListener;

/**
 * Renders UI, contentpane for the actual game graphics, mouse listener.
 * @author brookz
 *
 */
public class PokerPanel extends JPanel implements Observer{
	private GameEngine engine;
	private JPanel gamePanel;
	private JPanel actionPanel;
	private static final double gamePanelRatio = 0.8;
	private static final String CHECK_STRING = "check";
	private static final String CALL_STRING = "call";
	private int width;
	private int height;
	/** Label when in start game */
	private JLabel loadingLabel = new JLabel("Loading...");
	
	/** Menu state*/
	private JButton playMenuButton = new JButton("Play");
	private JButton exitMenuButton = new JButton("Exit");
	
	private JButton betButton = new JButton("Bet");
	private JSlider betSlider = new JSlider(JSlider.HORIZONTAL);
	private JButton checkOrCallButton = new JButton("Check");
	private JButton foldButton = new JButton("Fold");
	private JLabel statusLabel = new JLabel("");
	/** If we get repeated updates with this particular value, only process the first and ignore the rest */	
	private int previousPlayerMoney = 0;
	
	public PokerPanel(GameEngine engine, int width, int height) {
		this.width = width;
		this.height = height;
		this.setBackground(Color.GRAY);
		this.engine = engine;
		this.setPreferredSize(new Dimension(width, height));
		this.gamePanel = new GamePanel(new BorderLayout(), engine);
		this.gamePanel.setPreferredSize(new Dimension(width,
				(int) (gamePanelRatio * height)));
		this.actionPanel = new JPanel(new GridLayout(1, 5));
		this.actionPanel.setPreferredSize(new Dimension( (width),
				(int) ((1.0 - gamePanelRatio) * height)));
		this.add(gamePanel, BorderLayout.NORTH);
		this.add(actionPanel, BorderLayout.SOUTH);
		
		// Initialize menu buttons		
		this.playMenuButton.setPreferredSize(new Dimension(this.actionPanel
				.getPreferredSize().width, this.actionPanel
				.getPreferredSize().height / 2));		
		this.playMenuButton.addMouseListener(new GameStateButtonMouseListener(engine, GAMESTATE.STARTROUND));
		this.exitMenuButton.setPreferredSize(new Dimension(this.actionPanel
				.getPreferredSize().width, this.actionPanel
				.getPreferredSize().height / 2));		
		this.exitMenuButton.addMouseListener(new GameStateButtonMouseListener(engine, GAMESTATE.EXIT));
		
		this.loadingLabel.addMouseListener(new GameStateButtonMouseListener(engine, GAMESTATE.MENU));
		
		this.betSlider.addChangeListener(new BettingSliderChangeListener(engine));
		this.betButton.addMouseListener(new BetButtonMouseListener(engine));
		this.checkOrCallButton.addMouseListener(new CheckOrCallButtonMouseListener(engine));
		this.foldButton.addMouseListener(new FoldButtonMouseListener(engine));
		this.setUserButtonsEnabled(false);
	}

	/**
	 * Only paint the UI. RenderManager paints the actual game contents.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);	
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        GAMESTATE currentState = engine.getStateManager().getCurrentState().getGameState();
        if (this.actionPanel.getComponentCount() == 0){
        	System.out.println("Adding GUI...");
			switch (currentState) {
			case STARTGAME:
				// Very beginning of the game - render some generic title stuff
				System.out.println("Loading...");
				this.actionPanel.add(loadingLabel, BorderLayout.CENTER);			
				break;
			case MENU:
				// Play or exit buttons
				this.actionPanel.add(playMenuButton, BorderLayout.NORTH);
				this.actionPanel.add(exitMenuButton, BorderLayout.SOUTH);
				break;
			case STARTROUND:		
			case PREFLOP_BET:
			case FLOP:
				this.actionPanel.add(betButton);
				this.actionPanel.add(betSlider);
				this.actionPanel.add(checkOrCallButton);
				this.actionPanel.add(foldButton);
				this.actionPanel.add(statusLabel);
				break;
			}
			this.revalidate();
        }
	}
	
	/**
	 * Removes all components from the game controls panel. This signals that on the next render, new buttons will be added
	 * to the game panel based on the game state. 
	 */
	public void removeGamePanelButtons(){
		this.actionPanel.removeAll();
	}
	
	public void setUserButtonsEnabled(boolean enabled){
		betButton.setEnabled(enabled);
		checkOrCallButton.setEnabled(enabled);
		foldButton.setEnabled(enabled);
		betSlider.setEnabled(enabled);
	}
	
	public void updateSliderModel(Player player, int currentCallBet){
		// Stop updating if it's the same event.
		if (this.previousPlayerMoney == player.money){
			System.out.println("Same bet, no update");
			return;
		}
		this.previousPlayerMoney = player.money;
		betSlider.getModel().setMaximum(player.money);
		betSlider.getModel().setMinimum(currentCallBet);
		betSlider.setMajorTickSpacing(player.money/4);
		betSlider.setPaintLabels(true);
		betSlider.setPaintTicks(true);
		betSlider.setValue(player.betAmount);
		//betSlider.setSnapToTicks(true);
	}
	
	public void setStatusString(String status){
		this.statusLabel.setText(status);
	}
	
	public void updateCheckCallButtonText(){
		if (engine.getContext().maxBet <= 0){
			checkOrCallButton.setText(CHECK_STRING);
		} else {
			checkOrCallButton.setText(CALL_STRING);
		}
	}
	
	public JPanel getGamePanel(){
		return this.gamePanel;
	}
	
	public int getPlayerBetAmount(){
		return betSlider.getValue();
	}

	@Override
	public void update(Observable o, Object arg) {		
		this.repaint();		
	}
}
