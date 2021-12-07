import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;

import javax.swing.*;

/**
 * Webcam-based drawing 
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Spring 2015 (based on a different webcam app from previous terms)
 */

/*
author: Bansharee Ireen
date: 01.27.2021
purpose: completing scaffold by adding code to process live webcam images after selecting a target color for PS1.
 */

public class CamPaint extends Webcam {
	private char displayMode = 'w';			// what to display: 'w': live webcam, 'r': recolored image, 'p': painting
	private RegionFinder finder;			// handles the finding
	private Color targetColor;          	// color of regions of interest (set by mouse press)
	private Color paintColor = Color.blue;	// the color to put into the painting from the "brush"
	private BufferedImage painting;			// the resulting masterpiece

	/**
	 * Initializes the region finder and the drawing
	 */
	public CamPaint() {
		finder = new RegionFinder();
		clearPainting();
	}

	/**
	 * Resets the painting to a blank image
	 */
	protected void clearPainting() {
		painting = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
	}

	/**
	 * DrawingGUI method, here drawing one of live webcam, recolored image, or painting, 
	 * depending on display variable ('w', 'r', or 'p')
	 */
	@Override
	public void draw(Graphics g) {
		if (image != null) {			// making sure the image is not null
			if (displayMode == 'w' || displayMode == 'r' || displayMode == 'p') {
				super.draw(g);			// draws the webcam image for all three modes
			}
		}
	}

	/**
	 * Webcam method, here finding regions and updating painting.
	 */
	@Override
	public void processImage() {
		if (targetColor != null && image != null) {		// making sure we have an image & target color
			finder = new RegionFinder(image);			// giving finder our image
			finder.findRegions(targetColor);			// finding all regions containing target color

			if (displayMode == 'r') {					// if recoloring is on
				finder.recolorImage();					// recolor all regions
				image = finder.getRecoloredImage();		// update image
			}

			else if (displayMode == 'p') {			// if painting is on
				ArrayList<Point> region = finder.largestRegion();	// receiving the largest region found

				for (Point pixel : region) {		// coloring every point in largest region & its corresponding points in painting
					image.setRGB(pixel.x, pixel.y, paintColor.getRGB());
					painting.setRGB(pixel.x, pixel.y, paintColor.getRGB());
				}
			}
		}
	}

	/**
	 * Overrides the DrawingGUI method to set targetColor.
	 */
	@Override
	public void handleMousePress(int x, int y) {
		if (image != null) { // to be safe, make sure webcam is grabbing an image
			targetColor = new Color(image.getRGB(x, y));	// target color is color of pixel the mouse presses
		}
	}

	/**
	 * DrawingGUI method, here doing various drawing commands
	 */
	@Override
	public void handleKeyPress(char k) {
		if (k == 'p' || k == 'r' || k == 'w') { // display: painting, recolored image, or webcam
			displayMode = k;
		}
		else if (k == 'c') { // clear
			clearPainting();
		}
		else if (k == 'o') { // save the recolored image
			saveImage(finder.getRecoloredImage(), "pictures/recolored.png", "png");
		}
		else if (k == 's') { // save the painting
			saveImage(painting, "pictures/painting.png", "png");
		}
		else {
			System.out.println("unexpected key "+k);
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new CamPaint();
			}
		});
	}
}
