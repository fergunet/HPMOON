/**
 * This file is part of the HPMoon Project Library
 * 
 * Copyright (c) 2016, Jesús González
 * 
 */

package es.ugr.hpmoon.jmltools.clustering;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.tools.DatasetTools;

/**
 * Implements the Neighborhood based clustering algorithm.
 * 
 * @author Jesús González
 * 
 */
public class Neighborhood2 implements Clusterer
{
    /**
     * The distance measure used in the algorithm.
     */
    private DistanceMeasure dm;
    
    /**
     * Threshold to detect a jump to another cluster
     */
    double jumpTh;
    
    /**
     * Number of neighbors taken into account to estimate the first meanD of the cluster
     */
    int neighborsForFirstMeanD = 10;


    /**
     * Construct a default neighborhood based clusterer using the Euclidean distance.
     */
    public Neighborhood2 ()
    {
        this (0.3, new EuclideanDistance());
    }

    /**
     * Construct a default neighborhood based clusterer.
     * 
     * @param dm the distance measure
     */
    public Neighborhood2 (DistanceMeasure dm)
    {
        this (0.3, dm);
    }

    /**
     * Construct a default neighborhood based clusterer.
     * 
     * @param jumpTh threshold to detect a jump to another cluster
     * @param dm the distance measure
     */
    public Neighborhood2 (double jumpTh, DistanceMeasure dm)
    {
    	this.jumpTh = jumpTh;
    	this.dm = dm;
    }


    /**
     * Execute the Neighborhood based clustering algorithm on the data set that is provided.
     * 
     * @param data data set to cluster
     * @return the clusters as an array of Datasets. Each Dataset represents a cluster.
     */
    public Dataset[] cluster(Dataset data)
    {
        if (data.size() == 0)
            throw new RuntimeException("The dataset should not be empty");

        /* Number of attributes */
        int instanceLength = data.instance(0).noAttributes();
		
        /* Remaining data */
        Dataset remainingData = data.copy();
        
		/* Detected clusters */
		ArrayList<Dataset> foundClusters = new ArrayList<Dataset>();

		/* Current cluster */
		Dataset currentCluster = new DefaultDataset();
		foundClusters.add(currentCluster);
        
        /* Selects the first point and sets as the centroid of the first cluster */
        Instance nearest = remainingData.kNearest(1, DatasetTools.minAttributes(remainingData), this.dm).iterator().next();
		remainingData.remove(nearest);

		currentCluster.add(nearest);

		/* Size of current cluster */
		int currentClusterSize = 1;

		/* Current centroid */
		Instance currentCentroid = nearest.copy();
		
		/* Initialize meanD according to the neighborhood of the current centroid */
		double meanD = 0;
		int nNeighbors = 0;
		Iterator<Instance> it = remainingData.kNearest(neighborsForFirstMeanD, currentCentroid, this.dm).iterator();
		while (it.hasNext())
		{
			meanD += dm.measure(currentCentroid, it.next());
			nNeighbors++;
		}
		
		if (nNeighbors>0)
			meanD /= nNeighbors;
		
		while (remainingData.size()>0)
		{
			/* Find the closest element to current centroid and estimate the distance */
			nearest = remainingData.kNearest(1, currentCentroid, this.dm).iterator().next();
			remainingData.remove(nearest);

			double d = this.dm.measure(currentCentroid, nearest);
			double grad = d - meanD;
				
			// Log
			//System.out.printf("x: %.3f\t y: %.3f\t d: %.3f\t meanD: %.3f\t ratio: %.3f\n", nearest.value(1), nearest.value(2), d, meanD, ratio);

			/* If a new cluster is detected ... */
			if (grad>jumpTh)
			{
				// Log
				//System.out.println("\nS A L T O\n");
				
				currentCluster = new DefaultDataset();
				foundClusters.add(currentCluster);
				currentCentroid = nearest.copy();
				currentCluster.add(nearest);
				currentClusterSize = 1;

				meanD = 0;
				nNeighbors = 0;
				it = remainingData.kNearest(neighborsForFirstMeanD, currentCentroid, this.dm).iterator();
				while (it.hasNext())
				{
					meanD += dm.measure(currentCentroid, it.next());
					nNeighbors++;
				}
				
				if (nNeighbors>0)
					meanD /= nNeighbors;
			}
			else
			{
				// update the mean of distances
				meanD = ((meanD*currentClusterSize)+d)/(currentClusterSize+1);

				// obtain the mean of each attribute
		    	double[] mean = new double[instanceLength];
		    		
		    	/* update the centroid of the cluster */
				for (int j=0 ; j<instanceLength; j++)
				{
					mean[j] = ((currentCentroid.value(j)*currentClusterSize)+nearest.value(j))/(currentClusterSize+1);
				}
				currentCentroid = new DenseInstance(mean);
				currentCluster.add(nearest);
				currentClusterSize ++;
			}
		}
        
        Dataset[] output = new Dataset[foundClusters.size()];
        for (int i = 0; i < output.length; i++)
            output[i] = foundClusters.get(i);

        return output;
    }
}
