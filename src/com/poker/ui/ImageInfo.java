package com.poker.ui;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

public class ImageInfo {
	private Dimension dimension;
	private BufferedImage image;
	
	public ImageInfo(Dimension dimension, BufferedImage image){
		this.dimension = dimension;
		this.image = image;
	}

	public Dimension getDimension() {
		return dimension;
	}

	public BufferedImage getImage() {
		return image;
	}	
}
