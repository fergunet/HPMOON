package es.ugr.hpmoon.problems.featureselection.jmltools.clustering.evaluation;

import java.util.Arrays;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.tools.DatasetTools;

public abstract class HPMoonCVI implements ClusterEvaluation
{
    /**
     * Threshold
     */
    protected double threshold;

    /**
     * Distance measure
     */
    private DistanceMeasure dm;

    /**
     * Default constructor. Sets the threshold to 0 and uses the Euclidean distance as default
     */
    public HPMoonCVI()
    {
    	this (0);
    }

	/**
     * Constructs the evaluator. Uses the Euclidean distance as default
     * @param threshold Threshold to stop the accumulation of distances
     */
    public HPMoonCVI(double threshold)
    {
    	this (threshold, new EuclideanDistance());
    }

    /**
     * Constructs the evaluator
     * @param threshold Threshold to stop the accumulation of distances
     * @param dm Distance measure to be used
     */
    public HPMoonCVI(double threshold, DistanceMeasure dm)
    {
    	this.threshold = threshold;
		this.dm = dm;
    }

    /**
     * Returns the threshold
     */
    public double getThreshold()
    {
		return threshold;
	}

    /**
     * Sets the threshold
     * @param threshold A new value for the threshold
     */
	public void setThreshold(double threshold)
	{
		this.threshold = threshold;
	}

	/**
	 * Sets a reference point formed by the minimum value of each attribute of the centroids. Then selects the closest prototype
	 * to this reference as a starting point and extracts it from the set of centroids. Then an iterative process
	 * begins selecting the closest centroid (selected centroid) in the set to the last extracted centroid (current
	 * centroid). The distance between the current and the selected centroid is stored in an array. Then, the selected centroid is
	 * extracted from the set of eligible centroids and becomes the current centroid for the next iteration. The process
	 * finishes when the set of eligible centroids is empty. Then the array of stored distances is sorted and returned
	 * @param clusters A partition of the data set
	 * @return The sorted array of distances
	 */
	public double [] sortedDistances(Dataset [] clusters)
	{
		/* Centroids of the clusters */
		Dataset centroids = new DefaultDataset();
		for (int i = 0 ; i< clusters.length ; i++)
			centroids.add(DatasetTools.average(clusters[i]));
		
		
		/* Sets the reference point as the minimum values for the attributes of the centroids */
		Instance reference = DatasetTools.minAttributes(centroids);
		
		/* Selects the closest prototype to the minimum reference */
		Instance current = centroids.kNearest(1, reference, dm).iterator().next();
		centroids.remove(current);

		/* Find the closest centroid to current and estimate the distance */ 
		double [] distances = new double [clusters.length - 1];
		for (int i=0 ; i<distances.length ; i++)
		{
			Instance nearest = centroids.kNearest(1, current, dm).iterator().next();
			distances[i] = dm.measure(current, nearest);
			centroids.remove(nearest);
			current = nearest;
		}
		
		/* Order the array of distances */
		Arrays.sort(distances);
		
		return distances;
	}

	/**
	 * Computes the cvi according to the sorted array of distances
	 * @param sortedDistances Array of sorted distances obtained from the centroid of the clusters
	 * @return the score of the cvi
	 */
	public abstract double score (double [] sortedDistances);
	
	@Override
	public double score (Dataset[] clusters)
	{
		/* Obtain the array of sorted distances */
		return score(sortedDistances(clusters));
	}

	@Override
	public boolean compareScore(double score1, double score2) {
        return score2 < score1;
	}
}
