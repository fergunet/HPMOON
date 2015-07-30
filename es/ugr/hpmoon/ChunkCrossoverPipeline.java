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
    public static final String P_DISJOINT = "disjunct";
    public static final String DISJOINT_TRUE = "true";
    public static final String DISJOINT_FALSE = "false";
    public static final String DISJOINT_NONE = "none";
    public static final int NUM_SOURCES = 2;
    
    public String disjoint;

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
        disjunct = state.parameters.getString(base.push(P_DISJOINT), null);
        if(disjoint==null || (disjo))
            state.output.fatal("ERROR: parameter "+P_DISJOINT+" must not be null");
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
            
            int chunkSize = parent0.genomeLength()/numberOfIslands;
                       
            VectorIndividual chunk0 =  (VectorIndividual) parent0.clone();
            VectorIndividual chunk1 =  (VectorIndividual) parent1.clone();

            //DEBUG!!! DELETE!!!!!!!!!!!!!!!!!!!!!!!!
            //islandId = 0;
           
            //END DEBUG 
            
            //Creating split points and cutting the individual in pieces
            int[] points = this.getCutPoints(numberOfIslands, chunkSize, false);

            
            Object[] chunks0 = new Object[numberOfIslands];
            Object[] chunks1 = new Object[numberOfIslands];
            parent0.split(points, chunks0);
            parent1.split(points, chunks1);
           
            
            
            
            //If not using disjunt parameter then add +-1 chunk for parent
            int pre = (islandId-1)%numberOfIslands;
            if (pre<0) pre = pre+numberOfIslands;
            int pos = (islandId+1)%numberOfIslands;
            int lastsize = parent0.genomeLength()-points[points.length-1];
            
            if(disjunct.equals(DISJOINT_FALSE)){
                state.output.message("DISJUNTOS "+pre+" " +islandId+" "+pos);
                Object[] forChunk0 = new Object[3];
                Object[] forChunk1 = new Object[3];
                
                 forChunk0[0] = chunks0[pre];
                 forChunk0[1] = chunks0[islandId]; 
                 forChunk0[2] = chunks0[pos];
                
                 forChunk1[0] = chunks1[pre];
                 forChunk1[1] = chunks1[islandId]; 
                 forChunk1[2] = chunks1[pos];

                
                chunk0.join(forChunk0);
                chunk1.join(forChunk1);
                
                
                    
            }else{
                Object chunkGenome0 = chunks0[islandId]; //TODO check if islands starts in 1!
                Object chunkGenome1 = chunks1[islandId]; //TODO check if islands starts in 1!
                chunk0.setGenome(chunkGenome0); 
                chunk1.setGenome(chunkGenome1);
            }
            state.output.message("BEFORECHUNK0 "+chunk0.genomeLength()+" "+chunk0.genotypeToStringForHumans());
            state.output.message("BEFORECHUNK1 "+chunk1.genomeLength()+" "+chunk1.genotypeToStringForHumans());
            
            chunk0.defaultCrossover(state, thread, chunk1);
            
            state.output.message("AFTERCHUNK0  "+chunk0.genomeLength()+" "+chunk0.genotypeToStringForHumans());
            state.output.message("AFTERCHUNK1  "+chunk1.genomeLength()+" "+chunk1.genotypeToStringForHumans());
            
            
            
            if(disjunct.equals(DISJOINT_FALSE)){
                
                int[] newpoints = new int[2];
                newpoints[0] = chunkSize;
                newpoints[1] = chunkSize*2;
                
                if(islandId == 0){
                    newpoints[0] = lastsize;
                    newpoints[1] = lastsize + chunkSize;
                }
                if(islandId == (numberOfIslands-1)){
                    newpoints[1] = chunkSize+lastsize;
                }

                Object[] chunks0_3 = new Object[3]; 
                chunk0.split(newpoints, chunks0_3);
                chunks0[pre] = chunks0_3[0];
                chunks0[islandId] = chunks0_3[1];
                chunks0[pos] = chunks0_3[2];
                
                Object[] chunks1_3 = new Object[3]; 
                chunk1.split(newpoints, chunks1_3);
                chunks1[pre] = chunks1_3[0];
                chunks1[islandId] = chunks1_3[1];
                chunks1[pos] = chunks1_3[2];
                
            }else{
                chunks0[islandId]=chunk0.getGenome();
                chunks1[islandId]=chunk1.getGenome();
            }
            
            parent0.join(chunks0);
            parent1.join(chunks1);

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
    
    private int[] getCutPoints(int numberOfIslands, int chunkSize, boolean disjunct){
        int[] cutpoints = new int[numberOfIslands-1];
            for(int i=0;i<numberOfIslands-1;i++)
                cutpoints[i] = (i+1)*chunkSize;
        return cutpoints;
    }
}
    
    
    
    
    
    
    

