package es.ugr.hpmoon.jmltools.clustering.evaluation;

import java.util.Arrays;

import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.tools.DatasetTools;

public class Compactness implements FSClusterEvaluation
{
    /**
     * Distance measure
     */
    private DistanceMeasure dm;
        
    /**
     * Intra-cluster compactness index
     */
    private double compactness;
    
    /**
     * Threshold to detect a jump to another cluster
     */
    double jumpTh = 0.3;

    /**
     * Default constructor. Sets the threshold to 0 and uses the Euclidean distance as default
     */
    public Compactness()
    {
    	this (new EuclideanDistance());
    }

    /**
     * Constructs the evaluator
     * @param threshold Threshold to stop the accumulation of distances
     * @param dm Distance measure to be used
     */
    public Compactness(DistanceMeasure dm)
    {
		this.dm = dm;
		this.compactness = -1;
    }

	/**
	 * Sets a reference point formed by the minimum value of each attribute of the data. Then selects the closest element
	 * to this reference as a starting point and extracts it from the set of data. Then an iterative process
	 * begins selecting the closest point (selected element) in the set to the last extracted point (current
	 * element). The distance between the current and the selected points is stored in an array. Then, the selected point is
	 * extracted from the set of eligible points and becomes the current point for the next iteration. The process
	 * finishes when the set of data is empty.
	 * @param data The set of data
	 * @return The array of distances
	 */
	public double [] distances(Dataset data)
	{
		Dataset dataTmp = data.copy();
		double [] distances = new double [data.size()-1];
		
		/* Sets the reference point as the minimum values for the attributes of the dataset */
		Instance reference = DatasetTools.minAttributes(dataTmp);
		
		/* Selects the first point and sets as the centroid of the first cluster */
		Instance centroid = dataTmp.kNearest(1, reference, this.dm).iterator().next();
		dataTmp.remove(centroid);

		/* Number of attributes */
		int instanceLength = centroid.noAttributes();
		
		/* Size of current cluster */
		int clusterSize = 1;

		/* Mean of distances */
		double meanD = 0;
		
		/* Find the closest element to current centroid and estimate the distance */ 
		for (int i=0 ; i<distances.length ; i++)
		{

			Instance nearest = dataTmp.kNearest(1, centroid, this.dm).iterator().next();
			distances[i] = this.dm.measure(centroid, nearest);
			dataTmp.remove(nearest);
			
			double slope = distances[i] - meanD;
				
			/* If a new cluster is detected ... */
			if (slope>jumpTh)
			{
				centroid = nearest;
				clusterSize = 1;
				meanD = 0;
			}
			else
			{
				meanD = ((meanD*(clusterSize-1))+distances[i])/clusterSize;
				// obtain the mean of each attribute
		    	double[] mean = new double[instanceLength];
		    		
		    	/* update the centroid of the cluster */
				for (int j=0 ; j<instanceLength; j++)
				{
					mean[j] = ((centroid.value(j)*clusterSize)+nearest.value(j))/(clusterSize+1);
				}
				centroid = new DenseInstance(mean);
				clusterSize ++;
			}
			
			// log
			//System.out.println("MeanD: " + meanD + "\td:" + distances[i] + "\ts:" + slope);
		}
		
		return distances;
	}

    /**
     * Returns the score of a clusterer performed over a dataset taking into account only a subset of features.
     * 
     * @param data
     *            the set of data after applying a clustering algorithm in order to select some features
     * @return the score the clusterer obtained on this particular dataset
     */
	public double score(Dataset data) {
		if (this.compactness < 0)
		{
			/* Obtain the distances */ 
			double [] distances = distances(data);

			/* Order the array of distances */
			Arrays.sort(distances);
			
			/* Calculate the intra-cluster compactness as the Q1 quartile of the distances */
		    this.compactness = distances[distances.length/4];

		}
		
		return this.compactness;
	}

    /**
     * Compares the two scores according to the criterion in the implementation.
     * Some score should be maxed, others should be minimized. This method
     * returns true if the second score is 'better' than the first score.
     * 
     * @param score1
     *            the first score
     * @param score2
     *            the second score
     * @return true if the second score is better than the first, false in all
     *         other cases
     */
	public boolean compareScore(double score1, double score2)
	{
		return (score2<score1) ? true : false;
	}
}
