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
import jmetal.qualityIndicator.Spread;
import jmetal.util.NonDominatedSolutionList;
import jmetal.util.ExtractParetoFront;

/**
 *
 * @author pgarcia
 */
public class ShowStatistics {
    public static String JOB_ID = "job.";
    public static String STAT_EXTENSION = "stat";
    public static String COLUMNS_EXTENSION = "columns";
    private double[][] truePareto;
    
    
    public void showMOstatistics(String directory, String filedescriptor, int numJobs){
        Hypervolume hvcalculator = new Hypervolume();
        Spread spreadcalculator = new Spread();
        
        
        double[] hvs = new double[numJobs];
        double[] spreads = new double[numJobs];
        double[] idcs = new double[numJobs];
        double[] avgnumsols = new double[numJobs];
        double[] times = new double[numJobs];
        
        for(int i = 0; i<numJobs; i++){
            String filename = directory+"/"+JOB_ID + i+"."+filedescriptor+".front";
            double[][] pareto = extractParetoFromIslandsFile(filename, 2);
            double hv = hvcalculator.hypervolume(pareto, truePareto, 2);
            double spread = spreadcalculator.spread(pareto, truePareto, 2);
            double idc = -1; //Finish
            double avnumsol = getAverageNumSols(filename);
            String filenameTime = directory+"/"+JOB_ID + i+"."+filedescriptor+".stat";
            double time = readTime(filenameTime);
            System.out.println(hv+"\t"+spread+"\t"+idc+"\t"+avnumsol+"\t"+time);
            
        }
        
    
    }
    
    public double[][] extractParetoFromIslandsFile(String filename, int dimensions){
        ExtractParetoFront epf = new ExtractParetoFront(filename, dimensions);
        
        return null;
    }
    
    public double getAverageNumSols(String filename){
        return -1;
    }
    
    public double readTime(String filename){
        return -1;
    }
    
    
    
}
