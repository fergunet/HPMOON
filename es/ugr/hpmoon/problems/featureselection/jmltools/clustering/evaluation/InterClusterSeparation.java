package es.ugr.hpmoon.problems.featureselection.jmltools.clustering.evaluation;

import net.sf.javaml.distance.DistanceMeasure;

public class InterClusterSeparation extends HPMoonCVI
{
    /**
     * Default constructor. Sets the threshold to 0 and uses the Euclidean distance as default
     */
    public InterClusterSeparation()
    {
    	super();
    }


	/**
     * Constructs the evaluator. Uses the Euclidean distance as default
     * @param threshold Threshold to stop the accumulation of distances
     */
    public InterClusterSeparation(double threshold)
    {
    	super(threshold);
    }

    /**
     * Constructs the evaluator
     * @param threshold Threshold to stop the accumulation of distances
     * @param dm Distance measure to be used
     */
    public InterClusterSeparation(double threshold, DistanceMeasure dm)
    {
    	super(threshold, dm);
    }

	@Override
    public double score (double [] sortedDistances)
	{	
		double acc = 0;
		int i;
		for (i = sortedDistances.length - 1 ; i >= 0 ; i--)
		{
			if (threshold <= sortedDistances[i])
				acc += sortedDistances[i];
			else
				break;
		}
				
		if (i < sortedDistances.length - 1)
			acc /= ((sortedDistances.length) - i - 1);

		return -acc;
	}
}
