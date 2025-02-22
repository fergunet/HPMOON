/**
 * This file is part of the Java Machine Learning Library
 * 
 * The Java Machine Learning Library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * The Java Machine Learning Library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with the Java Machine Learning Library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 * Copyright (c) 2006-2012, Thomas Abeel
 * 
 * Project: http://java-ml.sourceforge.net/
 * 
 */
package es.ugr.hpmoon.jmltools.clustering.evaluation;

import net.sf.javaml.clustering.evaluation.ClusterEvaluation;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.Instance;
import net.sf.javaml.distance.DistanceMeasure;
import net.sf.javaml.distance.EuclideanDistance;

/**
 * Dunn based Inter-cluster separation index
 *  
 * @author Jesús González Peñalver
 */
public class DunnBasedInterClusterSeparation implements ClusterEvaluation {
    /**
     * Construct a new DunnBasedInterClusterSeparation evaluation measure that will
     * use the Euclidean distance to measure the errors.
     * 
     */
    public DunnBasedInterClusterSeparation()
    {
        this(new EuclideanDistance());
    }

    /**
     * Construct a new DunnBasedInterClusterSeparation cluster evaluation measure that will
     * use the supplied distance metric to measure the errors.
     * 
     */
    public DunnBasedInterClusterSeparation(DistanceMeasure dm)
    {
        this.dm = dm;
    }

    private DistanceMeasure dm;

    public double score(Dataset[] clusters)
    {
    	double meanMinSep = 0;
    	
    	for (int k=0 ; k<clusters.length-1 ; k++)
    	{
    		double minSep = Double.MAX_VALUE;
    		for (int l=k+1 ; l<clusters.length ; l++)
    		{
    			for (Instance instanceK : clusters[k])
    			{
    				for (Instance instanceL : clusters[l])
    				{
    					double d = dm.measure(instanceL, instanceK);
    					if (d<minSep)
    						minSep = d;
    				}
    			}
    				
    		}
    		meanMinSep += minSep;
    	}
    	
    	return clusters.length/meanMinSep;
    }

    public boolean compareScore(double score1, double score2)
    {
        // TODO solve bug: score is NaN when clusters with 0 instances
        // should be minimized
        return score2 < score1;
    }

}
