/**
 * This file is part of the HPMoon Project Library
 * 
 * Copyright (c) 2016, Jesús González
 * 
 */

package es.ugr.hpmoon.jmltools.clustering.evaluation;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.tools.DatasetTools;

/**
 * Intra-cluster compactness index. It is based in the overall deviation criterion proposed
 * by Handl and Knowles for MOCK:
 * J. Handl & J. Knowles. An evolutionary approach to multiobjective clustering.
 * IEEE Trans. Evol Computation, 11(1):56-76, 2007.
 * 
 * The difference with the original index is that in this case the sum of distances is divided by
 * the diameter of the dataset, calculated as the distance between two reference points, the first
 * formed by the minimum values of the attributes and the second formed by the maximum values of
 * the attributes. With this modification, clustering results using different number of attributes
 * can be compared fairly.
 *  
 * @author Jesús González Peñalver
 */
public class IntraClusterCompactness implements ClusterEvaluation {
    /**
     * Construct a new IntraClusterCompactness evaluation measure that will
     * use the Euclidean distance to measure the errors.
     * 
     */
    public IntraClusterCompactness()
    {
        this(new EuclideanDistance());
    }

    /**
     * Construct a IntraClusterCompacteness cluster evaluation measure that will
     * use the supplied distance metric to measure the errors.
     * 
     */
    public IntraClusterCompactness(DistanceMeasure dm)
    {
        this.dm = dm;
    }

    private DistanceMeasure dm;

    /**
     * The index is calculated as the sum of the distances from each data point to the centroid of
     * the cluster it belongs to. Lower values of this index mean more compact clusters.
     */
    public double score(Dataset[] clusters)
    {
    	double sumOfDistances = 0;
    	Instance minAttrs = clusters[0].instance(0).copy();
    	Instance maxAttrs = minAttrs.copy();
    	
    	for (int i=0 ; i<clusters.length ; i++)
    	{
    		Instance centroid = DatasetTools.average(clusters[i]);
    		for (int j = 0; j < clusters[i].size(); j++)
    			sumOfDistances += dm.measure(clusters[i].instance(j), centroid);
    		
    		Instance minCluster = DatasetTools.minAttributes(clusters[i]);
    		Instance maxCluster = DatasetTools.maxAttributes(clusters[i]);
    		for (int j=0 ; j<minAttrs.noAttributes() ; j++)
    		{
    			if (minCluster.value(j) < minAttrs.value(j))
    				minAttrs.put(j,minCluster.value(j));

    			if (maxCluster.value(j) > maxAttrs.value(j))
    				maxAttrs.put(j,maxCluster.value(j));
    		}
    	}

    	double diameter = dm.measure(minAttrs, maxAttrs);    	
    	return sumOfDistances / diameter;
    }

    /**
     * Compares the two scores according to the criterion in the implementation.
     * Some score should be maximized, others should be minimized. This method
     * returns true if the second score is 'better' than the first score.
     * 
     * @param score1
     *            the first score
     * @param score2
     *            the second score
     * @return As this index should be minimized, this function will return true if score2 < score1 
     */
   public boolean compareScore(double score1, double score2)
    {
        // TODO solve bug: score is NaN when clusters with 0 instances
        // should be minimized
        return score2 < score1;
    }

}
