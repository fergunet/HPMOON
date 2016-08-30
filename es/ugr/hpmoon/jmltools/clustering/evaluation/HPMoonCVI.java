package es.ugr.hpmoon.jmltools.clustering.evaluation;

import java.util.Arrays;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.ManhattanDistance;
import net.sf.javaml.tools.DatasetTools;

public class HPMoonCVI
{
    /**
     * Distance measure
     */
    private DistanceMeasure dm;
    
    /**
     * Inter-cluster separation index
     */
    private double interCusterSepar;
    
    /**
     * Intra-cluster compactness index
     */
    private double intraCusterCompac;

    /**
     * Default constructor. Sets the threshold to 0 and uses the Manhattan distance as default
     */
    public HPMoonCVI()
    {
    	this (new ManhattanDistance());
    }

    /**
     * Constructs the evaluator
     * @param threshold Threshold to stop the accumulation of distances
     * @param dm Distance measure to be used
     */
    public HPMoonCVI(DistanceMeasure dm)
    {
		this.dm = dm;
		this.interCusterSepar = -1;
		this.intraCusterCompac = -1;
    }

	/**
	 * Sets a reference point formed by the minimum value of each attribute of the data. Then selects the closest element
	 * to this reference as a starting point and extracts it from the dataset. Then an iterative process
	 * begins selecting the closest point (selected element) in the set to the last extracted point (current
	 * element). The distance between the current and the selected points is stored in an array. Then, the selected point is
	 * extracted from the set of eligible points and becomes the current point for the next iteration. The process
	 * finishes when the dataset is empty.
	 * @param data The data set
	 * @return The array of distances
	 */
	public double [] distances(Dataset data)
	{
		Dataset dataTmp = data.copy();
		double [] distances = new double [data.size()-1];
		
		/* Sets the reference point as the minimum values for the attributes of the dataset */
		Instance reference = DatasetTools.minAttributes(dataTmp);
		
		/* Selects the closest element to the minimum reference */
		Instance current = dataTmp.kNearest(1, reference, this.dm).iterator().next();
		dataTmp.remove(current);

		/* Find the closest element to current and estimate the distance */ 
		for (int i=0 ; i<distances.length ; i++)
		{
			Instance nearest = dataTmp.kNearest(1, current, this.dm).iterator().next();
			distances[i] = this.dm.measure(current, nearest);
			dataTmp.remove(nearest);
			current = nearest;
		}
		
		return distances;
	}

	/**
	 * Computes the inter-cluster separation and intra-cluster compactness estimations for a data set
	 * @param data The data set
	 */
	private void eval (Dataset data)
	{
		/* Obtain the distances */ 
		double [] distances = distances(data);
                /*String outpu = "";
                for(double d:distances)
                    outpu = outpu+d+" ";
                System.out.println(outpu);*/
		/* Order the array of distances */
		Arrays.sort(distances);
		
		/* Calculate the intra-cluster compactness as the Q1 quartile of the distances */
	    this.intraCusterCompac = distances[distances.length/4];
		
		/* Calculate the inter-cluster separation as the Q3 quartile of the distances */
	    this.interCusterSepar = distances[distances.length*3/4];

	    /* Calculate the inter-cluster separation as the max length minus the Q3 quartile of the distances */
//	    this.interCusterSepar = distances[distances.length-1] - distances[distances.length*3/4];
	    
/*		int pos = distances.length*3/4;

		// Calculate the intra-cluster compactness as mean of the remaining 25% of sorted distances 
		this.intraCusterCompac = 0;
		for (int i=pos ; i<distances.length ; i++)
			this.intraCusterCompac += distances[i];
		if (distances.length-pos>0)
			this.intraCusterCompac /= (distances.length-pos);
		
		// Calculate the inter-cluster separation as mean of the first 75% of sorted distances 
		this.interCusterSepar = 0;
		for (int i=0 ; i<pos ; i++)
			this.interCusterSepar += distances[i];
		if (pos>0)
			this.interCusterSepar /= pos;
*/	}
	
	/**
	 * Returns the inter-cluster separation estimation for a data set
	 * @param data The data set
	 */
	public double interClusterSeparation (Dataset data)
	{
		//if (this.interCusterSepar < 0)
			eval(data);
		
		return this.interCusterSepar;
	}

	/**
	 * Returns the inter-cluster separation estimation for a data set
	 * @param data The data set
	 */
	public double intraClusterCompactness (Dataset data)
	{
		//if (this.intraCusterCompac < 0)
			eval(data);
		
		return this.intraCusterCompac;
	}
}
