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

import net.sf.javaml.core.Dataset;

/**
 * This interface provides a frame for all measure that can be used to evaluate
 * the quality of a selection of features performed via a clustering algorithm.
 * 
 * @author Jesús González
 * 
 */
public interface FSClusterEvaluation
{

    /**
     * Returns the score of a clusterer performed over a dataset taking into account only a subset of features.
     * 
     * @param centroids
     *            the set of centroids after applying a clustering algorithm in order to select some features
     * @return the score the clusterer obtained on this particular dataset
     */
    public double score(Dataset centroids);

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
    public boolean compareScore(double score1, double score2);
}
