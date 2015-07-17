package es.ugr.hpmoon;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.DoubleVectorIndividual;

/**
 *
 * @author pgarcia
 */
public class SOMEvaluator extends Problem implements SimpleProblemForm{

    @Override
    public void setup(final EvolutionState state, final Parameter base){
        super.setup(state, base);
    }
    
    @Override
    public void evaluate(EvolutionState state, Individual ind, int subpopulation, int threadnum) {
        if( !( ind instanceof DoubleVectorIndividual ) )
            state.output.fatal( "The individuals for this problem should be DoubleVectorIndividuals." );
            
        //state.output.message("EVALUANDO EN LA POP "+subpopulation);
        DoubleVectorIndividual temp = (DoubleVectorIndividual)ind;
        double[] genome = temp.genome;
        int numDecisionVars = genome.length;

        double[] objectives = ((MultiObjectiveFitness)ind.fitness).getObjectives();
        
        
        
        //////////////////////
        double f, g, h, sum;
        f = genome[0];
        objectives[0] = f;
        sum = 0;
        for(int i = 1; i< numDecisionVars; ++i)
                    sum += genome[i];
        g = 1d+9d*sum/(numDecisionVars - 1.0);
        h = 1d-Math.sqrt(f/g);
        objectives[1] = (g*h);
        //////////////////////
        
        
        ((MultiObjectiveFitness)ind.fitness).setObjectives(state, objectives);
        ind.evaluated = true;
    }
    
}
