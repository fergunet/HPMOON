package es.ugr.hpmoon.problem;

import java.io.File;
import java.io.IOException;

import ec.EvolutionState;
import ec.Individual;
import ec.Problem;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleProblemForm;
import ec.util.Parameter;
import ec.vector.BitVectorIndividual;

import es.ugr.hpmoon.jmltools.HPMoonDatasetTools;
import es.ugr.hpmoon.jmltools.clustering.evaluation.HPMoonCVI;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.tools.data.FileHandler;


public class FeatureSelection extends Problem implements SimpleProblemForm
{
	private static final long serialVersionUID = 8094183456024089262L;

	/* Default parameter base for the problem */
	public static final String P_FS_PROBLEM = "fs_problem";

	/* Define a parameter to load the dataset */
	public static final String P_FS_DATASET = "dataset";
	private Dataset data;
	
	/* Define a parameter for the clustering algorithm */
	public static final String P_CLUSTERER = "clusterer";
	private Clusterer clusterer;

	/* Define a parameter for the number of centroids to be used in the clustering algorithm */
	public static final String P_CLUSTERER_NUM_CENTROIDS = "clusterer.num_centroids";
	private int clustererNumCentroids;
	
	/* Minimum number of centroids for the clustering algorithm */
	private static int clustererMinNumCentroids = 2;

	/* Define a parameter for the number of iterations to be performed by the clustering algorithm */
	public static final String P_CLUSTERER_NUM_ITERATIONS = "clusterer.num_iterations";
	private int clustererNumIterations;

	/* Minimum number of iterations for the clustering algorithm */
	private static int clustererMinNumIterations = 100;

	/* Define a parameter for the first objective function */
	public static final String P_CVI_1 = "cvi1";
	private HPMoonCVI cvi1;

	/* Define a parameter for the second objective function */
	public static final String P_CVI_2 = "cvi2";
	private HPMoonCVI cvi2;

	/**
	 * Returns the default base for this problem.
	 */
	public Parameter defaultBase() { return new Parameter(P_FS_PROBLEM); }

    /**
     * Sets up the problem by reading it from the parameters stored in state,
     * built off of the parameter base base.
     * @param state The evolution state
     * @param base  The parameter base
     */
	public void setup(final EvolutionState state, final Parameter base)
    {
    	super.setup(state,base);   // always call super.setup(...) first if it exists!
        System.out.println("Q PASA");
    	try
    	{
    		/* default base for the parameters used to configure this problem */
    		Parameter def = defaultBase();
	    	
    		/* Obtain the dataset */
    		File dataFile = state.parameters.getFile(base.push(P_FS_DATASET), def.push(P_FS_DATASET));
	    	if (dataFile==null)
	    		state.output.fatal("Missing dataset", base.push(P_FS_DATASET), def.push(P_FS_DATASET));
			
	    	this.data = FileHandler.loadDataset(dataFile);
	    	/*
	    	// Obtain the number of centroids for the clustering algorithm 

	    	// gets an int greater or equal to minNumCentroids from the parameter database,
	    	// returning a value of minNumCentroids-1 if the parameter doesn't exist or was 
	        // smaller than minNumCentroids
	        this.clustererNumCentroids = state.parameters.getInt(base.push(P_CLUSTERER_NUM_CENTROIDS), def.push(P_CLUSTERER_NUM_CENTROIDS), clustererMinNumCentroids);
	        if (this.clustererNumCentroids < clustererMinNumCentroids)
	            state.output.fatal(String.format("The number of centroids should be greater than %d", clustererMinNumCentroids-1),
	                base.push(P_CLUSTERER_NUM_CENTROIDS),def.push(P_CLUSTERER_NUM_CENTROIDS));

	    	// Obtain the number of iterations for the clustering algorithm 
	        this.clustererNumIterations = state.parameters.getInt(base.push(P_CLUSTERER_NUM_ITERATIONS), def.push(P_CLUSTERER_NUM_ITERATIONS), clustererMinNumIterations);
	        if (this.clustererNumIterations < clustererMinNumIterations)
	            state.output.fatal(String.format("The number of iterations should be greater than %d", clustererMinNumIterations-1),
	                base.push(P_CLUSTERER_NUM_ITERATIONS),def.push(P_CLUSTERER_NUM_ITERATIONS));

	        // Obtain the clustering algorithm 
	        String clustererClassName = state.parameters.getStringWithDefault(base.push(P_CLUSTERER), def.push(P_CLUSTERER), null);
	        
	        // Test if it is a supported clustering algorithm 
	        if (clustererClassName.equals("net.sf.javaml.clustering.KMeans"))
	        	this.clusterer = new KMeans(clustererNumCentroids, clustererNumIterations);
	        else
	        	state.output.fatal("Not supported clusterer", base.push(P_CLUSTERER), def.push(P_CLUSTERER));
	        	        
	        // Obtain de CVIs 
	        cvi1 = (HPMoonCVI) state.parameters.getInstanceForParameter(base.push(P_CVI_1), def.push(P_CVI_1), HPMoonCVI.class);
	        cvi2 = (HPMoonCVI) state.parameters.getInstanceForParameter(base.push(P_CVI_2), def.push(P_CVI_2), HPMoonCVI.class);
	        
	        // Modify the genome-size according to the dataset 
	        state.parameters.set(new Parameter("pop.subpop.0.species.genome-size"), "" + this.data.noAttributes());
	        */
                
                //PABLO
                cvi1 = new HPMoonCVI();
		}
    	catch (IOException e)
    	{
    		state.output.fatal("Could not open the dataset file: " + e.getMessage());
		}
    } 

	/**
	 * Evaluates the individual (in not already evaluated)
	 * @param state			The state of the evolutionary process
	 * @param ind           Individual to be evaluated
	 * @param subpopulation The subpopulation to which the individual belongs
	 * @param threadnum     The thread of execution
	 */
	public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation, final int threadnum)
	{
		/* Evaluate only not already evaluated individuals */
		if (ind.evaluated)
			return;

		/* Tests the individual's representation */
		if( !( ind instanceof BitVectorIndividual ) )
			state.output.fatal( "The individuals for this problem should be BitVectorIndividuals." );

		/* Tests the number of objectives */
		double[] objectives = ((MultiObjectiveFitness)ind.fitness).getObjectives();
		if( objectives.length != 2 )
			state.output.fatal( "This problem should have two objectives" );
		
		/* Clusters the data according only to the features selected by the individual's genome */
		boolean[] genome = ((BitVectorIndividual)ind).genome;

		/* Test if any feature was selected */
		boolean anySelected = false;
		for (int i=0 ; i< genome.length ; i++)
			anySelected |= genome[i];
		
		/* If no feature was selected, the worst fitness is assigned */
		if (!anySelected)
		{
			objectives[0] = Double.MAX_VALUE;
			objectives[1] = Double.MAX_VALUE;
		}
		else
		{
                   
                   Dataset projection = HPMoonDatasetTools.project(data, genome);
                   double inter = cvi1.interClusterSeparation(projection);
                   double intra = cvi1.intraClusterCompactness(projection);
                   
                   objectives[0] = intra;
                   objectives[1] = 1/inter;
                        /* PABLO: COMENTO ESTO
			// Apply a clustering algorithm to a projection of the selected features 
                            
			Dataset[] clusters = clusterer.cluster(HPMoonDatasetTools.project(data, genome));
                        
                       
			// Obtain the sorted list of distances and their mean value 
			double [] distances = cvi1.sortedDistances(clusters);
			double meanOfDistances = 0;
			for (int i = 0 ; i< distances.length ; i++)
				meanOfDistances += distances[i];
			meanOfDistances /= distances.length;
			
                        
			// Set the mean value as threshold for the CVIs 
			
                        cvi1.setThreshold(meanOfDistances);
			cvi2.setThreshold(meanOfDistances);

			// Evaluate the solution 
			objectives[0] = cvi1.score(distances);
			objectives[1] = cvi2.score(distances);
			*/
                        
                        
			/* Probamos a ver si se genera la solución buena */
			/*boolean[] elBueno = {false, true, false, true, false};
			boolean encontrado = true;
			for (int i=0 ; i<elBueno.length ; i++)
			{
				if (genome[i]!=elBueno[i])
				{
					encontrado = false;
					break;
				}
			}
			if (encontrado)
				state.output.message("Encontrado. Fitness: [" + objectives[0] + " " + objectives[1] + "]"); */
		}
		
		/* Sets the fitness */
		((MultiObjectiveFitness)ind.fitness).setObjectives(state, objectives);
		ind.evaluated = true;
	}
}