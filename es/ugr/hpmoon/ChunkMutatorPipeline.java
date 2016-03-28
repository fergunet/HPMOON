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

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.app.multiplexer.MultiplexerData;
import ec.util.Parameter;
import ec.vector.VectorDefaults;
import ec.vector.VectorIndividual;

/**
 * This class is like VectorMutationPipeline, but only mutates a specific chunk of the genome.
 * Requires the parameters numberOfIslands and island Id.
 * @author pgarcia
 */
public class ChunkMutatorPipeline extends BreedingPipeline{
    public static final String P_CHUNK_MUTATOR = "chunk-mutator";
    public static final int NUM_SOURCES = 1;
    
    private String disjoint;

    @Override
    public void setup(EvolutionState state, Parameter base) {
        super.setup(state, base);
        //DISJUNCT
        disjoint = state.parameters.getString(base.push(HPMOONUtils.P_DISJOINT), null);
        if(disjoint==null || (!disjoint.equals(HPMOONUtils.DISJOINT_FALSE) && !disjoint.equals(HPMOONUtils.DISJOINT_TRUE) && !disjoint.equals(HPMOONUtils.DISJOINT_FALSE_VARIABLE) && !disjoint.equals(HPMOONUtils.DISJOINT_NONE)))
            state.output.fatal("ERROR: parameter "+HPMOONUtils.P_DISJOINT+" must not be null, or different than 'true', 'false' or 'none'");
        
    }
    
    @Override
    public Parameter defaultBase(){
        return VectorDefaults.base().push(P_CHUNK_MUTATOR);
    }
    
    @Override
    public int numSources(){return NUM_SOURCES;}

    @Override
    public int produce(int min, int max, int start, int subpopulation, Individual[] inds, EvolutionState state, int thread) {
        //Extracting island id 
        int[]info =  HPMOONUtils.getIslandIdAndNumIslands(state, subpopulation, thread);
        int numberOfIslands = info[1];
        int islandId = info[0];
        //state.output.message("MUTANDO EN ISLA "+subpopulation);
        

// grab individuals from our source and stick 'em right into inds.
        // we'll modify them from there
        int n = sources[0].produce(min,max,start,subpopulation,inds,state,thread);

        // should we bother?
        if (!state.random[thread].nextBoolean(likelihood))
            return reproduce(n, start, subpopulation, inds, state, thread, false);  // DON'T produce children from source -- we already did

        // clone the individuals if necessary
        if (!(sources[0] instanceof BreedingPipeline))
            for(int q=start;q<n+start;q++)
                inds[q] = (Individual)(inds[q].clone());

        // mutate 'em
        //HERE ARE THE MODIFICATIONS WITH RESPECT TO VectorMutationPipeline
        for(int q=start;q<n+start;q++)
            {
            //((VectorIndividual)inds[q]).defaultMutate(state,thread);
            //((VectorIndividual)inds[q]).evaluated=false;
                
            VectorIndividual vi = (VectorIndividual) inds[q];

            //state.output.message("MUTBEFORE "+vi.genomeLength()+" "+vi.genotypeToStringForHumans());
            
                       
            VectorIndividual chunk =  HPMOONUtils.getSubIndividual(vi, islandId, numberOfIslands, disjoint);
            chunk.defaultMutate(state, thread);
            HPMOONUtils.reconstructIndividual(vi, chunk, disjoint, islandId, numberOfIslands);
            
            //state.output.message("MUTAFTER  "+vi.genomeLength()+" "+vi.genotypeToStringForHumans());

            vi.evaluated = false;
            }

        return n;
    }
    
}
