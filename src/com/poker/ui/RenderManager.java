package com.poker.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import com.poker.lib.IRenderable;
import com.poker.lib.Player;
import com.poker.lib.RenderList;
import com.poker.utils.LRUCache;

/**
 * This renders things, but it shouldn't be stupid. It should cache the images after loading them.
 *
 **/
public class RenderManager {
	private static final int DEFAULT_CACHE_SIZE = 1000;
	private static final Dimension cardBoundary = new Dimension(100, 100);
	private static final Dimension playerBoundary = new Dimension(50, 50);
	private Graphics2D g;
	private Component c;
	private LRUCache<String, ImageInfo> imageCache = new LRUCache<String, ImageInfo>(DEFAULT_CACHE_SIZE, true);
	
	
	public RenderManager(Graphics g, Component c){
		this.g = (Graphics2D)g;
        this.g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
		this.c = c;
	}
	
	public void render(RenderList renderList) throws IOException{
		List<IRenderable> communityCards = renderList.getRenderList(RenderList.COMMUNITY_CARD_TYPE);
		if (communityCards != null) {			
			for (int i = 0; i < communityCards.size(); i++) {
				IRenderable communityCard = communityCards.get(i);
				System.out.println("Rendering: " + communityCard.getImageURL());
				this.render(communityCard, i*100, 0, cardBoundary);				
				// Render list does not care about the actual content pane.
				// Content pane should clear the contents
			}
		}
		// We draw the players in a circular fashion. Given a window width, width/2 is the radius.
		int centerX = (int) (c.getWidth() * 0.4);
		int centerY = (int) (c.getHeight() * 0.4);
		int radius = Math.min(centerX, centerY);
		List<IRenderable> players = renderList.getRenderList(RenderList.PLAYER_TYPE);
		if (players != null){
			// Assuming the center of the size dimension is our circle's middle.
			// Height ratio = sin(2*pi/# players), width ratio = cos(2*pi/# players).
			// We can start at the actual location of (width/2, height), then rotate in such a fashion.
			for(int i = 0; i < players.size(); i++){
				Player player = (Player) players.get(i);
				// For the first player, the dimensions come to (0,1). But we want to start at (0,-1)
				float dx = (float)(Math.sin(2 * Math.PI * i / players.size())) * radius;
				float dy = (float)(Math.cos(2 * Math.PI * i/ players.size()) * radius); 
				
				float x = centerX + dx;
				float y = centerY + dy;
				//Draw the names
				g.drawString(player.name, x, y);
				this.renderHorizontal((int)x, (int)y + 30, 50, cardBoundary, player.hand.getCards());
			}
		}
		//Render the pot size		
		String potSize = renderList.getDrawString(RenderList.POT_SIZE);
		if (potSize != null){
			System.out.println("Potsize: " + potSize);
			g.drawString("Potsize: " + potSize, c.getWidth()/2, 10);
		}
	}
	
	public <T extends IRenderable> void renderHorizontal(int x, int y, int dx, Dimension boundary, Collection<T> renderables){
		int i = 0;
		for(T renderable : renderables){
			try {
				this.render(renderable, x + (i * dx), y, boundary);
				i++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
		
	private void render(IRenderable renderable, int x, int y, Dimension boundary) throws IOException{
		if (renderable.getImageURL() == null || renderable.getImageURL().isEmpty()){
			return;
		}
		BufferedImage img = null;
		Dimension scaledDimension = null;
		// First see if we can get it from the cache.
		if (this.imageCache.contains(renderable.getImageURL())){
			ImageInfo imgInfo = this.imageCache.get(renderable.getImageURL());
			img = imgInfo.getImage();
			scaledDimension = imgInfo.getDimension();
		} else {
			// If not, do the calculations here and then put it in the cache.
			img = ImageIO.read(new File(renderable
					.getImageURL()));
			scaledDimension = getScaledDimension(new Dimension(
					img.getWidth(), img.getHeight()), boundary);	
			this.imageCache.put(renderable.getImageURL(), new ImageInfo(scaledDimension, img));
		}		
		g.drawImage(img, x, y, scaledDimension.width,
				scaledDimension.height, null);
		System.out.println("Rendered " + renderable.getImageURL());
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
