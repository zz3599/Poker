package com.poker.ui;

import javax.swing.JFrame;

import com.poker.lib.GameEngine;

public class PokerFrame extends JFrame {
	private GameEngine engine;
	private PokerPanel panel;
	private static final int DEFAULT_WIDTH = 1000;
	private static final int DEFAULT_HEIGHT = 800;

	public PokerFrame(GameEngine engine) {
		super("Poker");
		this.engine = engine;
		this.panel = new PokerPanel(engine, DEFAULT_WIDTH, DEFAULT_HEIGHT);
		this.setContentPane(panel);
		this.validate();
		this.pack();		
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public PokerPanel getPokerPanel(){
		return this.panel;
	}
}
