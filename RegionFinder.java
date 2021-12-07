import java.awt.*;
import java.awt.image.*;
import java.util.*;

/**
 * Region growing algorithm: finds and holds regions in an image.
 * Each region is a list of contiguous points with colors similar to a target color.
 * Scaffold for PS-1, Dartmouth CS 10, Fall 2016
 * 
 * @author Chris Bailey-Kellogg, Winter 2014 (based on a very different structure from Fall 2012)
 * @author Travis W. Peters, Dartmouth CS 10, Updated Winter 2015
 * @author CBK, Spring 2015, updated for CamPaint
 */

/*
author: Bansharee Ireen
date: 01.27.2021
purpose: completing scaffold by adding code for region detection, testing color similarity, and recoloring for PS1.
 */
public class RegionFinder {
	private static final int maxColorDiff = 720;			// how similar color must be to the target color to belong to a region
															// suitable value for maxColorDiff depends on your implementation of colorMatch() and how much difference in color you want to allow
	private static final int minRegion = 50; 				// how many points in a region to be worth considering

	private BufferedImage image;                            // the image in which to find regions
	private BufferedImage recoloredImage;                   // the image with identified regions recolored

	private ArrayList<ArrayList<Point>> regions;			// a region is a list of points
															// so the identified regions are in a list of lists of points

	public RegionFinder() {
		this.image = null;
	}

	public RegionFinder(BufferedImage image) {
		this.regions = new ArrayList<ArrayList<Point>>();
		this.image = image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage getImage() {
		return image;
	}

	public BufferedImage getRecoloredImage() {
		return recoloredImage;
	}

	/**
	 * Sets regions to the flood fill regions in the image, similar enough to the targetColor.
	 */
	public void findRegions(Color targetColor) {
		int radius = 1;		// setting the radius of each pixel

		// creating an additional image in all black that colors visited pixels
		BufferedImage visited = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		// looping over all pixels
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				// assigning the current point and its color to the below variables
				Point currentPoint = new Point(x, y);
				Color currentColor = new Color(image.getRGB(x, y));

				// below code only runs if pixel has not been visited and it has the correct color
				if (visited.getRGB(x, y) == 0 && colorMatch(targetColor, currentColor)) {
					ArrayList<Point> newRegion = new ArrayList<Point>();     // starts a new region of points

					ArrayList<Point> toVisit = new ArrayList<>();    		 // to track what to visit
					toVisit.add(currentPoint);                        		 // initialize with current point

					while (toVisit.size() != 0) {
						// grabbing point from end of list to visit
						Point visitPoint = toVisit.remove(toVisit.size()-1);

						// going to next iteration of while loop if grabbed point has been visited
						if (visited.getRGB(visitPoint.x, visitPoint.y) != 0) {
							continue;
						}

						newRegion.add(visitPoint);                           // adding point to current region
						visited.setRGB(visitPoint.x, visitPoint.y, 1);   // and marking it as visited

						// looping over neighbours of the point visited
						for (int ny = Math.max(0, visitPoint.y - radius);
							 ny < Math.min(image.getHeight(), visitPoint.y + 1 + radius);
							 ny++) {
							for (int nx = Math.max(0, visitPoint.x - radius);
								 nx < Math.min(image.getWidth(), visitPoint.x + 1 + radius);
								 nx++) {

								if (colorMatch(targetColor, new Color(image.getRGB(nx, ny)))) {
									toVisit.add(new Point(nx, ny));        // colors match, so neighbor will be visited
								}
							}
						}
					}

					if (newRegion.size() >= minRegion) {	// if region is large enough,
						regions.add(newRegion);				// add it to regions ArrayList
					}
				}
			}
		}
	}

	/**
	 * Tests whether the two colors are "similar enough" (your definition, subject to the maxColorDiff threshold, which you can vary)
	 */
	private static boolean colorMatch(Color c1, Color c2) {
		// using the Euclidean distance to compute the difference between colors c1 & c2
		int d = (c1.getRed() - c2.getRed()) * (c1.getRed() - c2.getRed())
				+ (c1.getGreen() - c2.getGreen()) * (c1.getGreen() - c2.getGreen())
				+ (c1.getBlue() - c2.getBlue()) * (c1.getBlue() - c2.getBlue());

		// returns true if the difference is acceptable
		return (d < maxColorDiff);
	}

	/**
	 * Returns the largest region detected (if any region has been detected)
	 */
	public ArrayList<Point> largestRegion() {
		int maxSize = 0;							// keeps track of the largest size of the regions
		ArrayList<Point> desiredRegion = new ArrayList<Point>();		// keeps track of the largest region

		if (regions.size() != 0) {
			for (ArrayList<Point> region : regions) {
				if (region.size() >= maxSize) {		// compares the sizes of the regions
					maxSize = region.size();		// updates the largest size
					desiredRegion = region;			// updates largest region
				}
			}
		}
		return desiredRegion;
	}

	/**
	 * Sets recoloredImage to be a copy of image, 
	 * but with each region a uniform random color, 
	 * so we can see where they are
	 */
	public void recolorImage() {
		// First copy the original
		recoloredImage = new BufferedImage(image.getColorModel(),image.copyData(null), image.getColorModel().isAlphaPremultiplied(),null);

		// Now recolor the regions in it
		for (ArrayList<Point> region: regions) {              	// for every region in regions ArrayList
			// generating random RGB value
			int v = (int) (Math.random() * 16777216);

			for (Point toColor : region) {                    	// for every point in each region
				// grabbing a random color for every region
				Color randomColor = new Color(v);

				// using black only for "shapes.png"
//				Color desiredColor = Color.BLACK;

				recoloredImage.setRGB(toColor.x, toColor.y, randomColor.getRGB());
			}
		}
	}
}