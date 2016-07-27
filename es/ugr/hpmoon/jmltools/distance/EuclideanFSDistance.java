/**
 * This file is part of the HPMoon Project Library
 * 
 * Copyright (c) 2016, Jesús González
 * 
 */

package es.ugr.hpmoon.jmltools.distance;

import net.sf.javaml.core.Instance;

/**
 * This class implements the Euclidean distance.
 * 
 * The Euclidean distance between two points P=(p1,p2,...,pn) and
 * Q=(q1,q2,...,qn) in the Euclidean n-space is defined as: sqrt((p1-q1)^2 +
 * (p2-q2)^2 + ... + (pn-qn)^2)
 * 
 * The Euclidean distance is a special instance of the NormDistance. The
 * Euclidean distance corresponds to the 2-norm distance.
 * 
 * 
 * 
 * @linkplain http://en.wikipedia.org/wiki/Euclidean_distance
 * @linkplain http://en.wikipedia.org/wiki/Euclidean_space
 * 
 * @author Jesús González
 */
public class EuclideanFSDistance extends AbstractFSDistance {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor. All features are used by default
	 */
	public EuclideanFSDistance()
	{
		super();
	}
	
	/**
	 * Constructs a distance measure which only will take into account the features selected by the mask
	 * @param mask Mask of selected features
	 */
	public EuclideanFSDistance(boolean [] mask)
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
        	sum += ((mask==null || mask[i])?1:0) * (x.value(i) - y.value(i)) * (x.value(i) - y.value(i));
        }
        return Math.sqrt(sum);
    }
}
