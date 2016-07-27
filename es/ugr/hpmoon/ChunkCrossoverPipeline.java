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
import ec.util.Parameter;
import ec.vector.VectorDefaults;
import ec.vector.VectorIndividual;

/**
 * This class is based on VectorCrossoverPipeline
 * @author pgarcia
 */
public class ChunkCrossoverPipeline extends BreedingPipeline{
    public static final String P_TOSS = "toss";
    public static final String P_CROSSOVER = "chunkxover";

    public static final int NUM_SOURCES = 2;
    
    public String disjoint;
    public int extraChunks;

    /** Should the pipeline discard the second parent after crossing over? */
    public boolean tossSecondParent;

    /** Temporary holding place for parents */
    VectorIndividual parents[];

    public ChunkCrossoverPipeline() { parents = new VectorIndividual[2]; }
    
    public Parameter defaultBase() { return VectorDefaults.base().push(P_CROSSOVER); }

    /** Returns 2 */
    public int numSources() { return NUM_SOURCES; }

    public Object clone()
        {
        ChunkCrossoverPipeline c = (ChunkCrossoverPipeline)(super.clone());

        // deep-cloned stuff
        c.parents = (VectorIndividual[]) parents.clone();

        return c;
        }

    public void setup(final EvolutionState state, final Parameter base)
        {
        super.setup(state,base);
        Parameter def = defaultBase();
        tossSecondParent = state.parameters.getBoolean(base.push(P_TOSS),
            def.push(P_TOSS),false);
        
        //DISJUNCT
        disjoint = state.parameters.getString(base.push(HPMOONUtils.P_DISJOINT), null);
        if(disjoint==null || (!disjoint.equals(HPMOONUtils.DISJOINT_FALSE) && !disjoint.equals(HPMOONUtils.DISJOINT_TRUE) && !disjoint.equals(HPMOONUtils.DISJOINT_FALSE_VARIABLE) && !disjoint.equals(HPMOONUtils.DISJOINT_FALSE_VARIABLE) && !disjoint.equals(HPMOONUtils.DISJOINT_NONE)))
            state.output.fatal("ERROR: parameter "+HPMOONUtils.P_DISJOINT+" must not be null, or different than 'true', 'false' or 'none'");
        
        //EXTRACHUNKS
        if(disjoint.equals(HPMOONUtils.DISJOINT_FALSE_VARIABLE))
            extraChunks = state.parameters.getInt(base.push(HPMOONUtils.P_EXTRACHUNKS), null);
        
        }
        
    /** Returns 2 * minimum number of typical individuals produced by any sources, else
        1* minimum number if tossSecondParent is true. */
    public int typicalIndsProduced()
        {
        return (tossSecondParent? minChildProduction(): minChildProduction()*2);
        }

    public int produce(final int min, 
        final int max, 
        final int start,
        final int subpopulation,
        final Individual[] inds,
        final EvolutionState state,
        final int thread) 

        {
            
        //Extracting island id
        int[]info =  HPMOONUtils.getIslandIdAndNumIslands(state, subpopulation, thread);
        int numberOfIslands = info[1];
        int islandId = info[0];
       
            
        
            
        // how many individuals should we make?
        int n = typicalIndsProduced();
        if (n < min) n = min;
        if (n > max) n = max;
                
        // should we bother?
        if (!state.random[thread].nextBoolean(likelihood))
            return reproduce(n, start, subpopulation, inds, state, thread, true);  // DO produce children from source -- we've not done so already

        for(int q=start;q<n+start; /* no increment */)  // keep on going until we're filled up
            {
            // grab two individuals from our sources
            if (sources[0]==sources[1])  // grab from the same source
                {
                sources[0].produce(2,2,0,subpopulation,parents,state,thread);
                if (!(sources[0] instanceof BreedingPipeline))  // it's a selection method probably
                    { 
                    parents[0] = (VectorIndividual)(parents[0].clone());
                    parents[1] = (VectorIndividual)(parents[1].clone());
                    }
                }
            else // grab from different sources
                {
                sources[0].produce(1,1,0,subpopulation,parents,state,thread);
                sources[1].produce(1,1,1,subpopulation,parents,state,thread);
                if (!(sources[0] instanceof BreedingPipeline))  // it's a selection method probably
                    parents[0] = (VectorIndividual)(parents[0].clone());
                if (!(sources[1] instanceof BreedingPipeline)) // it's a selection method probably
                    parents[1] = (VectorIndividual)(parents[1].clone());
                }
                
            // at this point, parents[] contains our two selected individuals,
            // AND they're copied so we own them and can make whatever modifications
            // we like on them.
    
            // so we'll cross them over now.  Since this is the default pipeline,
            // we'll just do it by calling defaultCrossover on the first child
            
            //Original code
            //parents[0].defaultCrossover(state,thread,parents[1]);
            //parents[0].evaluated=false;
            //parents[1].evaluated=false;
            
            //STARTS CHUNK CODE by pgarcia
            
            
            VectorIndividual parent0 = parents[0];
            VectorIndividual parent1 = parents[1];
            
            
            state.output.message("BEFORE0 "+parent0.genomeLength()+" "+parent0.genotypeToStringForHumans());
            state.output.message("BEFORE1 "+parent1.genomeLength()+" "+parent1.genotypeToStringForHumans());
            
            

            //DEBUG!!! DELETE!!!!!!!!!!!!!!!!!!!!!!!!
            //islandId = 0;
           
            //END DEBUG 
            //state.output.message(islandId+" "+numberOfIslands);
            
           
            VectorIndividual smallFather = HPMOONUtils.getSubIndividual(parent0, islandId, numberOfIslands, disjoint,extraChunks);
            VectorIndividual smallMother = HPMOONUtils.getSubIndividual(parent1, islandId, numberOfIslands, disjoint,extraChunks);
            
            
            state.output.message("BEFORECHUNK0 "+smallFather.genomeLength()+" "+smallFather.genotypeToStringForHumans());
            state.output.message("BEFORECHUNK1 "+smallMother.genomeLength()+" "+smallMother.genotypeToStringForHumans());
            
            smallFather.defaultCrossover(state, thread,smallMother);
            
            state.output.message("AFTERCHUNK0  "+smallFather.genomeLength()+" "+smallFather.genotypeToStringForHumans());
            state.output.message("AFTERCHUNK1  "+smallMother.genomeLength()+" "+smallMother.genotypeToStringForHumans());
            
            
            
            HPMOONUtils.reconstructIndividual(parent0, smallFather, disjoint, islandId, numberOfIslands,extraChunks);
            HPMOONUtils.reconstructIndividual(parent1, smallMother, disjoint, islandId, numberOfIslands,extraChunks);
            
            


            state.output.message("AFTER0  "+parent0.genomeLength()+" "+parent0.genotypeToStringForHumans());
            state.output.message("AFTER1  "+parent1.genomeLength()+" "+parent1.genotypeToStringForHumans());
            parent0.evaluated = false;
            parent1.evaluated = false;
            
            
            //END OF CHUNK CODE BY pgarcia 
            // add 'em to the population
            inds[q] = parents[0];
            q++;
            if (q<n+start && !tossSecondParent)
                {
                inds[q] = parents[1];
                q++;
                }
            }
        return n;
        }
    

}
    
    
    
    
    
    
    

