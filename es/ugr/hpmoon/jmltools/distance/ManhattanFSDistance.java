/**
 * This file is part of the HPMoon Project Library
 * 
 * Copyright (c) 2016, Jesús González
 * 
 */

package es.ugr.hpmoon.jmltools.distance;

import net.sf.javaml.core.Instance;

/**
 * The Manhattan distance is the sum of the (absolute) differences of their
 * coordinates. The taxicab metric is also known as recti-linear distance,
 * Minkowski's L1 distance, city block distance, or Manhattan distance.
 * 
 * 
 * @linkplain http://en.wikipedia.org/wiki/Taxicab_geometry
 * @linkplain http://www.nist.gov/dads/HTML/manhattanDistance.html
 * @linkplain http://mathworld.wolfram.com/TaxicabMetric.html
 * 
 * @author Jesús González
 */
public class ManhattanFSDistance extends AbstractFSDistance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor. All features are used by default
	 */
	public ManhattanFSDistance()
	{
		super();
	}
	
	/**
	 * Constructs a distance measure which only will take into account the features selected by the mask
	 * @param mask Mask of selected features
	 */
	public ManhattanFSDistance(boolean [] mask)
	{
		super(mask);
	}

    /**
     * Calculates the Manhattan distance as the sum of the absolute differences
     * of their coordinates.
     * 
     * @return the Manhattan distance between the two instances.
     */
    public double measure(Instance x, Instance y) {
        if (x.noAttributes() != y.noAttributes())
            throw new RuntimeException("Both instances should contain the same number of values.");
        double sum = 0.0;
        
        int l = Math.min(x.noAttributes(), mask.length);
        for (int i = 0; i < l; i++)
        {
        	sum += ((mask==null || mask[i])?1:0) * Math.abs(x.value(i) - y.value(i));
        }
        return sum;
    }
}
