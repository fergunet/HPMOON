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
package es.ugr.hpmoon;

import ec.EvolutionState;
import ec.Individual;
import static ec.Statistics.P_SILENT;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.MultiObjectiveStatistics;
import ec.simple.SimpleStatistics;
import ec.util.Output;
import ec.util.Parameter;
import ec.util.QuickSort;
import ec.util.SortComparator;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author pgarcia
 */
public class HPMOONStatistics extends SimpleStatistics {

     /** front file parameter */
    public static final String P_PARETO_FRONT_FILE = "front";
    public static final String P_SILENT_FRONT_FILE = "silent.front";
        
    public boolean silentFront;

    /** The pareto front log */
    public int frontLog = 0;  // stdout by default

    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state,base);

        silentFront = state.parameters.getBoolean(base.push(P_SILENT), null, false);
        // yes, we're stating it a second time.  It's correct logic.
        silentFront = state.parameters.getBoolean(base.push(P_SILENT_FRONT_FILE), null, silentFront);
        
        File frontFile = state.parameters.getFile(base.push(P_PARETO_FRONT_FILE),null);

        if (silentFront)
            {
            frontLog = Output.NO_LOGS;
            }
        else if (frontFile!=null)
            {
            try
                {
                frontLog = state.output.addLog(frontFile, !compress, compress);
                }
            catch (IOException i)
                {
                state.output.fatal("An IOException occurred while trying to create the log " + frontFile + ":\n" + i);
                }
            }
        else state.output.warning("No Pareto Front statistics file specified, printing to stdout at end.", base.push(P_PARETO_FRONT_FILE));
        }



    
    
    
    
    


    /*public void finalStatistics(final EvolutionState state, final int result) {
        //bypassFinalStatistics(state, result);  // just call super.super.finalStatistics(...)
        super.finalStatistics(state, result);
        //if (doFinal){

        state.output.println("GENERATIONS " + state.generation, statisticslog);
        long initTime = ((NSGA2TimeEvaluator) (state.evaluator)).initTime;
        long spentTime = System.currentTimeMillis() - initTime;
        state.output.println("TIME " + spentTime, statisticslog);
        //} REMOVED doFinal (set in parameterFiles) to avoid printing the individuals! 
    }*/

    @Override
    public void postEvaluationStatistics(final EvolutionState state) {
        //////PRINTING IN STATS FILE
        //System.out.println("PRINTING IN STATISTICS FILE");
        state.output.println("GENERATIONS " + state.generation, statisticslog);
        long initTime = ((NSGA2TimeEvaluator) (state.evaluator)).initTime;
        long spentTime = System.currentTimeMillis() - initTime;
        state.output.println("TIME " + spentTime, statisticslog);
        
        //////PRINTING IN LOG FILE
        
        //System.out.println("GENERATION ENDED, WRITING FRONT");
        for (int s = 0; s < state.population.subpops.length; s++) {
            MultiObjectiveFitness typicalFitness = (MultiObjectiveFitness) (state.population.subpops[s].individuals[0].fitness);
            if (doFinal) {
                state.output.println("\n\nPareto Front of Subpopulation " + s, statisticslog);
            }

            // build front
            ArrayList front = typicalFitness.partitionIntoParetoFront(state.population.subpops[s].individuals, null, null);

            // sort by objective[0]
            Object[] sortedFront = front.toArray();
            QuickSort.qsort(sortedFront, new SortComparator() {
                public boolean lt(Object a, Object b) {
                    return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0)
                            < (((MultiObjectiveFitness) ((Individual) b).fitness)).getObjective(0));
                }

                public boolean gt(Object a, Object b) {
                    return (((MultiObjectiveFitness) (((Individual) a).fitness)).getObjective(0)
                            > ((MultiObjectiveFitness) (((Individual) b).fitness)).getObjective(0));
                }
            });

            // print out front to statistics log
            if (doFinal) {
                for (int i = 0; i < sortedFront.length; i++) {
                    ((Individual) (sortedFront[i])).printIndividualForHumans(state, statisticslog);
                }
            }
            
            //THIS CLEAR THE LOG FILE
            //System.out.println("CLEARING LOG FILE");
            try {
                state.output.reopen(frontLog);
            } catch (Exception ex) {
                state.output.fatal("ERROR REOPENING FRONTLOG" + ex.getMessage());
            }

            //////////////////////////
            

            // write short version of front out to disk
            if (state.population.subpops.length > 1) {
                state.output.println("Subpopulation " + s, frontLog);
            }

            for (int i = 0; i < sortedFront.length; i++) {
                Individual ind = (Individual) (sortedFront[i]);
                MultiObjectiveFitness mof = (MultiObjectiveFitness) (ind.fitness);
                double[] objectives = mof.getObjectives();

                String line = "";
                for (int f = 0; f < objectives.length; f++) {
                    line += (objectives[f] + " ");
                }

                state.output.println(line, frontLog);

            }

        }
    }
}
