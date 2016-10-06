package com.poker.ui;

import java.awt.Graphics;
import java.awt.LayoutManager;
import java.io.IOException;

import javax.swing.JPanel;

import com.poker.lib.GameEngine;
import com.poker.lib.RenderList;

public class GamePanel extends JPanel {
	private GameEngine engine;
	private RenderManager renderManager;
	
	public GamePanel(LayoutManager layout, GameEngine engine){
		super(layout);
		this.engine = engine;
		this.renderManager = new RenderManager(this);
	}
	
	public void paintComponent(Graphics g){
		
		RenderList renderList = this.engine.getStateManager().getCurrentState().getRenderList();
		try {
			renderManager.render(renderList, g);			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
