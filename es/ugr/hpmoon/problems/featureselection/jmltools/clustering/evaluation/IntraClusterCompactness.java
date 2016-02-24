package es.ugr.hpmoon.problems.featureselection.jmltools.clustering.evaluation;

import net.sf.javaml.distance.DistanceMeasure;

public class IntraClusterCompactness extends HPMoonCVI
{
    /**
     * Default constructor. Sets the threshold to 0 and uses the Euclidean distance as default
     */
    public IntraClusterCompactness()
    {
    	super();
    }


	/**
     * Constructs the evaluator. Uses the Euclidean distance as default
     * @param threshold Threshold to stop the accumulation of distances
     */
    public IntraClusterCompactness(double threshold)
    {
    	super(threshold);
    }

    /**
     * Constructs the evaluator
     * @param threshold Threshold to stop the accumulation of distances
     * @param dm Distance measure to be used
     */
    public IntraClusterCompactness(double threshold, DistanceMeasure dm)
    {
    	super(threshold, dm);
    }


	@Override
    public double score (double [] sortedDistances)
	{
		double acc = 0;
		int i;
		for (i=0 ; i<sortedDistances.length ; i++)
		{
			if (sortedDistances[i] <= threshold)
				acc += sortedDistances[i];
			else
				break;
		}
				
		if (i>1)
			acc /= i;

		return acc;

	}
}
