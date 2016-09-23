package com.poker.ui;

import java.awt.Color;

import javax.swing.JFrame;

import com.poker.lib.GameEngine;

public class PokerFrame extends JFrame {
	private GameEngine engine;
	private PokerPanel panel;

	public PokerFrame(GameEngine engine) {
		super("Poker");
		this.engine = engine;
		this.panel = new PokerPanel(engine);
		this.add(panel);
		this.pack();
		this.setBackground(Color.BLACK);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
}
