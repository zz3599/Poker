package com.poker.ui;

import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.poker.lib.GameEngine;

public class PokerPanel extends JPanel {
	private GameEngine engine;

	public PokerPanel(GameEngine engine) {
		this.engine = engine;
		this.setPreferredSize(new Dimension(500, 500));
		
	}

	@Override
	public void paintComponent(Graphics g) {

	}
}
