package com.poker.ui;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.io.IOException;

import javax.swing.JPanel;

import com.poker.lib.GameEngine;
import com.poker.lib.RenderList;

public class GamePanel extends JPanel {
	private GameEngine engine;
	
	public GamePanel(LayoutManager layout, GameEngine engine){
		super(layout);
		this.engine = engine;
	}
	
	public void paintComponent(Graphics g){
		RenderManager renderManager = new RenderManager(g, this);
		RenderList renderList = this.engine.getStateManager().getCurrentState().getRenderList();
		try {
			renderManager.render(renderList);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
