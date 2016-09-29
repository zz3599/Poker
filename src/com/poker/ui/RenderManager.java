package com.poker.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import com.poker.lib.IRenderable;
import com.poker.lib.RenderList;

public class RenderManager {
	private static final Dimension cardBoundary = new Dimension(100, 100);
	private Graphics g;
	private Component c;
	
	public RenderManager(Graphics g, Component c){
		this.g = g;
		this.c = c;
	}
	
	public void render(RenderList renderList) throws IOException{
		List<IRenderable> communityCards = renderList.getRenderList(RenderList.COMMUNITY_CARD_TYPE);
		if (communityCards != null) {
			for (int i = 0; i < communityCards.size(); i++) {
				IRenderable communityCard = communityCards.get(i);
				System.out.println("Rendering: " + communityCard.getImageURL());
				BufferedImage img = ImageIO.read(new File(communityCard
						.getImageURL()));
				Dimension scaledDimension = getScaledDimension(new Dimension(
						img.getWidth(), img.getHeight()), cardBoundary);
				g.drawImage(img, i * 100, 0, scaledDimension.width,
						scaledDimension.height, null);
				// Render list does not care about the actual content pane.
				// Content pane should clear the contents
			}
		}
	}
		
	/**
	 * Gets the scaled dimension of imgSize that will fit inside boundary.
	 * @param imgSize
	 * @param boundary
	 * @return
	 */
	public static Dimension getScaledDimension(Dimension imgSize, Dimension boundary) {
	    int original_width = imgSize.width;
	    int original_height = imgSize.height;
	    int bound_width = boundary.width;
	    int bound_height = boundary.height;
	    int new_width = original_width;
	    int new_height = original_height;

	    // first check if we need to scale width
	    if (original_width > bound_width) {
	        //scale width to fit
	        new_width = bound_width;
	        //scale height to maintain aspect ratio
	        new_height = (new_width * original_height) / original_width;
	    }

	    // then check if we need to scale even with the new height
	    if (new_height > bound_height) {
	        //scale height to fit instead
	        new_height = bound_height;
	        //scale width to maintain aspect ratio
	        new_width = (new_height * original_width) / original_height;
	    }

	    return new Dimension(new_width, new_height);
	}
}
