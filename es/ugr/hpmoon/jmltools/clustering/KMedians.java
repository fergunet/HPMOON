/**
 * This file is part of the HPMoon Project Library
 * 
 * Copyright (c) 2016, Jesús González
 * 
 */

package es.ugr.hpmoon.jmltools.clustering;

import java.util.Arrays;

import ec.util.MersenneTwister;

import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.ManhattanDistance;
import net.sf.javaml.tools.DatasetTools;

/**
 * Implements the K-medians algorithm.
 * 
 * @author Jesús González
 * 
 */
public class KMedians implements Clusterer {
    /**
     * The number of clusters.
     */
    private int numberOfClusters = -1;

    /**
     * The number of iterations the algorithm should make. If this value is
     * Integer.INFINITY, then the algorithm runs until the centroids no longer
     * change.
     * 
     */
    private int numberOfIterations = -1;

    /**
     * Random generator for this clusterer.
     */
    private MersenneTwister rg;

    /**
     * The distance measure used in the algorithm, defaults to Manhattan
     * distance.
     */
    private DistanceMeasure dm;

    /**
     * The centroids of the different clusters.
     */
    private Instance[] centroids;

    /**
     * Constuct a default K-medians clusterer with 100 iterations, 4 clusters, a
     * default random generator and using the Manhattan distance.
     */
    public KMedians() {
        this(4);
    }

    /**
     * Constuct a default K-medians clusterer with the specified number of
     * clusters, 100 iterations, a default random generator and using the
     * Manhattan distance.
     * 
     * @param k the number of clusters to create
     */
    public KMedians(int k) {
        this(k, 100);
    }

    /**
     * Create a new K-medians clusterer with the given number of clusters
     * and iterations. The internal random generator is a new one based upon the
     * current system time. For the distance we use the Manhattan n-space
     * distance.
     * 
     * @param clusters
     *            the number of clusters
     * @param iterations
     *            the number of iterations
     */
    public KMedians(int clusters, int iterations) {
        this.numberOfClusters = clusters;
        this.numberOfIterations = iterations;
        this.dm = new ManhattanDistance();
        rg = new MersenneTwister(System.currentTimeMillis());
    }

    /**
     * Create a new K-medians clusterer with the given number of clusters
     * and iterations. The internal random generator is a new one based upon the
     * current system time. For the distance we use the Manhattan n-space
     * distance.
     * 
     * @param clusters
     *            the number of clusters
     * @param iterations
     *            the number of iterations
     * @param dm
     *            the distance measure
     */
    public KMedians(int clusters, int iterations, DistanceMeasure dm) {
        this.numberOfClusters = clusters;
        this.numberOfIterations = iterations;
        this.dm = dm;
        rg = new MersenneTwister(System.currentTimeMillis());
    }

    /**
     * Execute the KMedians clustering algorithm on the data set that is provided.
     * 
     * @param data data set to cluster
     * @return the clusters as an array of Datasets. Each Dataset represents a cluster.
     */
    public Dataset[] cluster(Dataset data) {
        if (data.size() == 0)
            throw new RuntimeException("The dataset should not be empty");
        if (numberOfClusters == 0)
            throw new RuntimeException("There should be at least one cluster");
        // Place K points into the space represented by the objects that are
        // being clustered. These points represent the initial group of
        // centroids.
        // DatasetTools.
        Instance min = DatasetTools.minAttributes(data);
        Instance max = DatasetTools.maxAttributes(data);
        this.centroids = new Instance[numberOfClusters];
        int instanceLength = data.instance(0).noAttributes();
        for (int j = 0; j < numberOfClusters; j++) {
            double[] randomInstance = new double[instanceLength];
            for (int i = 0; i < instanceLength; i++) {
                double dist = Math.abs(max.value(i) - min.value(i));
                randomInstance[i] = (float) (min.value(i) + rg.nextDouble() * dist);

            }
            this.centroids[j] = new DenseInstance(randomInstance);
        }

        int iterationCount = 0;
        boolean centroidsChanged = true;
        boolean randomCentroids = true;
        while (randomCentroids || (iterationCount < this.numberOfIterations && centroidsChanged))
        {
            iterationCount++;
            // Assign each object to the group that has the closest centroid.
            int[] assignment = new int[data.size()];
            
            // Number of data in each cluster
            int[] clustersSize = new int[this.numberOfClusters];
            for (int i=0 ; i<this.numberOfClusters ; i++)
            	clustersSize[i] = 0;
            
            for (int i = 0; i < data.size(); i++) {
                int tmpCluster = 0;
                double minDistance = dm.measure(centroids[0], data.instance(i));
                for (int j = 1; j < centroids.length; j++) {
                    double dist = dm.measure(centroids[j], data.instance(i));
                    if (dm.compare(dist, minDistance)) {
                        minDistance = dist;
                        tmpCluster = j;
                    }
                }
                assignment[i] = tmpCluster;
                clustersSize[tmpCluster]++;

            }
            
            centroidsChanged = false;
            randomCentroids = false;

            // When all objects have been assigned, recalculate the positions of
            // the K centroids and start over.
            // The new position of the centroid is the median of the current cluster.
            
            // For each cluster ...
            for (int i=0 ; i<this.numberOfClusters ; i++)
            {
            	// If it has any data, obtain its median
            	if (clustersSize[i]>0)
            	{
            		// data belonging to cluster i
            		double [][] clusterData = new double[instanceLength][clustersSize[i]];
            		int clusterDataIndex = 0;
            		for (int j=0 ; j<data.size(); j++)
            		{
            			if (assignment[j]==i)
            			{
            				Instance in = data.instance(j);
            				for (int k=0 ; k<instanceLength ; k++)
            					clusterData[k][clusterDataIndex] = in.value(k);
            				clusterDataIndex++;
            			}
            		}
            		
            		// sort the attributes
            		for (int j=0 ; j<instanceLength ; j++)
            			Arrays.sort(clusterData[j]);
            		
            		// obtain the median of each attribute
            		double[] tmp = new double[instanceLength];
            		
                	int medianPos = clustersSize[i]/2;
                	if ( (clustersSize[i] & 1) == 0 )
                	{
                        for (int j = 0; j < instanceLength; j++)
                    		tmp[j] = (clusterData[j][medianPos]+clusterData[j][medianPos-1])/2;
                	}
                	else
                	{
                        for (int j = 0; j < instanceLength; j++)
                    		tmp[j] = clusterData[j][medianPos];
                	}
                    
            		Instance newCentroid = new DenseInstance(tmp);
            		if (dm.measure(newCentroid, centroids[i]) > 0.0001)
            		{
                    	centroidsChanged = true;
                    	centroids[i] = newCentroid;
            		}
            	}
            	// else generate a new centroid
            	else {
                    double[] randomInstance = new double[instanceLength];
                    for (int j = 0; j < instanceLength; j++) {
                        double dist = Math.abs(max.value(j) - min.value(j));
                        randomInstance[j] = (float) (min.value(j) + rg.nextDouble() * dist);

                    }
                    randomCentroids = true;
                    this.centroids[i] = new DenseInstance(randomInstance);
                }
            }
        }
        Dataset[] output = new Dataset[centroids.length];
        for (int i = 0; i < centroids.length; i++)
            output[i] = new DefaultDataset();
        for (int i = 0; i < data.size(); i++) {
            int tmpCluster = 0;
            double minDistance = dm.measure(centroids[0], data.instance(i));
            for (int j = 0; j < centroids.length; j++) {
                double dist = dm.measure(centroids[j], data.instance(i));
                if (dm.compare(dist, minDistance)) {
                    minDistance = dist;
                    tmpCluster = j;
                }
            }
            output[tmpCluster].add(data.instance(i));

        }
        return output;
    }
}
