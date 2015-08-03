/*
 * Copyright (C) 2015 pgarcia
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package es.ugr.statistics;

import jmetal.qualityIndicator.Hypervolume;
import jmetal.util.NonDominatedSolutionList;
import jmetal.util.ExtractParetoFront;

/**
 *
 * @author pgarcia
 */
public class ShowStatistics {
    public static String JOB_ID = "job.";
    public static String STAT_EXTENSION = "stat";
    private double[][] truePareto;
    
    
    public void showMOstatistics(String filedescriptor){
    Hypervolume hv = new Hypervolume();
    }
    
    public double[] extractParetoFromIslandsFile(String filename, int dimensions){
        ExtractParetoFront epf = new ExtractParetoFront(filename, dimensions);
        
        return null;
    }
    
    
    
}
