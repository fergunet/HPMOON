/**
 * This file is part of the HPMoon Project Library
 * 
 * Copyright (c) 2016, Jesús González
 * 
 */

package es.ugr.hpmoon.jmltools.distance;

import net.sf.javaml.distance.AbstractDistance;

/**
 * Abstract super class for all distance used to solve Feature Selection problems
 * 
 * @see net.sf.javaml.distance.DistanceMeasure
 * @see net.sf.javaml.distance.AbstractDistance
 * 
 * @author Jesús González
 * 
 */
public abstract class AbstractFSDistance extends AbstractDistance
{

	private static final long serialVersionUID = 1L;

	/**
	 * Mask for the selected attributes
	 */
	protected boolean [] mask;

	/**
	 * Default constructor. All features are used by default
	 */
	public AbstractFSDistance()
	{
		super();
		this.mask = null;
	}
	
	/**
	 * Constructs a distance measure which only will take into account the features selected by the mask
	 * @param mask Mask of selected features
	 */
	public AbstractFSDistance(boolean [] mask)
	{
		super();
		this.mask = mask;
	}
	

}
