package com.poker.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.engine.EngineException;
import com.poker.lib.GameEngine;
import com.poker.state.AbstractPokerGameState;
import com.poker.state.AbstractPokerGameState.GAMESTATE;
import com.poker.ui.listener.ButtonMouseListener;

/**
 * Renders UI, contentpane for the actual game graphics, mouse listener.
 * @author brookz
 *
 */
public class PokerPanel extends JPanel {
	private GameEngine engine;
	private JPanel gamePanel;
	private JPanel actionPanel;
	private static final double gamePanelRatio = 0.8;
	
	private JButton playMenuButton = new JButton("Play");
	private JButton exitMenuButton = new JButton("Exit");
	
	
	public PokerPanel(GameEngine engine, int width, int height) {
		this.setBackground(Color.BLACK);
		this.engine = engine;
		this.setPreferredSize(new Dimension(width, height));
		this.gamePanel = new JPanel(new BorderLayout());
		this.gamePanel.setPreferredSize(new Dimension(width,
				(int) (gamePanelRatio * height)));
		this.actionPanel = new JPanel(new FlowLayout());
		this.actionPanel.setPreferredSize(new Dimension( (width),
				(int) ((1.0 - gamePanelRatio) * height)));
		this.add(gamePanel, BorderLayout.NORTH);
		this.add(actionPanel, BorderLayout.SOUTH);
		
		// Initialize menu buttons		
		this.playMenuButton.setPreferredSize(new Dimension(this.gamePanel
				.getPreferredSize().width, this.gamePanel
				.getPreferredSize().height / 2));		
		this.playMenuButton.addMouseListener(new ButtonMouseListener(engine, GAMESTATE.STARTROUND));
		this.exitMenuButton.setPreferredSize(new Dimension(this.gamePanel
				.getPreferredSize().width, this.gamePanel
				.getPreferredSize().height / 2));		
		this.exitMenuButton.addMouseListener(new ButtonMouseListener(engine, GAMESTATE.EXIT));
		
	}

	/**
	 * Only paint the UI. RenderManager paints the actual game contents.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);		
		AbstractPokerGameState state = engine.getStateManager().getCurrentState();
		
		switch (state.getGameState()) {
		case STARTGAME:
			// Very beginning of the game - render some generic title stuff
			// Then transition to menu state
			engine.getStateManager().advanceState(GAMESTATE.MENU);
			break;
		case MENU:
			// Play or exit buttons			
			this.gamePanel.add(playMenuButton, BorderLayout.NORTH);
			this.gamePanel.add(exitMenuButton, BorderLayout.SOUTH);
			this.revalidate();
			break;
		case STARTROUND:
			this.gamePanel.removeAll();
			this.revalidate();
			break;
		}
		
	}
	
	public JPanel getGamePanel(){
		return this.gamePanel;
	}
}
