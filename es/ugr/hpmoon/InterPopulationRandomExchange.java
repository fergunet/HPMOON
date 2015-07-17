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
import ec.Exchanger;
import ec.Individual;
import ec.Population;
import ec.SelectionMethod;
import ec.util.Parameter;
import java.io.Serializable;


/**
 * InterPopulationRandomExchange is a modification of InterPopulationExchange.
 * By default it considers ALL the subpopulation as a possible destination, if no specific parameters for destination are set.
 * @author pgarcia
 */

public class InterPopulationRandomExchange extends Exchanger
    {
    private static final long serialVersionUID = 1;

    // static inner classes don't need SerialVersionUIDs
    static class IPEInformation implements Serializable
        {
        // the selection method
        SelectionMethod immigrantsSelectionMethod;

        // the selection method
        SelectionMethod indsToDieSelectionMethod;

        // the number of destination subpopulations
        int numDest;

        // the subpopulations where individuals need to be sent
        int[] destinations;

        // the modulo
        int modulo;

        // the start (offset)
        int offset;

        // the size
        int size;
        }


    /** The subpopulation delimiter */
    public static final String P_SUBPOP = "subpop";

    /** The parameter for the modulo (how many generations should pass between consecutive sendings of individuals */
    public static final String P_MODULO = "mod";

    /** The number of emigrants to be sent */
    public static final String P_SIZE = "size";

    /** How many generations to pass at the beginning of the evolution before the first
        emigration from the current subpopulation */
    public static final String P_OFFSET = "start";

    /** The number of destinations from current island */
    public static final String P_DEST_FOR_SUBPOP = "num-dest";

    /** The prefix for destinations */
    public static final String P_DEST = "dest";

    /** The selection method for sending individuals to other islands */
    public static final String P_SELECT_METHOD = "select";

    /** The selection method for deciding individuals to be replaced by immigrants */
    public static final String P_SELECT_TO_DIE_METHOD = "select-to-die";
    
    /** Whether or not we're chatty */
    public static final String P_CHATTY = "chatty";

    /** My parameter base -- I need to keep this in order to help the server
        reinitialize contacts */
    // SERIALIZE
    public Parameter base;
    
    //NEW PARAMETERS BY PABLO GARCIA
    /** If we are using a panmictic connection */
    public static final String P_ALL = "all";
    
    public static final String P_DEFAULT_SUBPOP = "default-subpop";
    
    IPEInformation[] exchangeInformation;

    //  storage for the incoming immigrants: 2 sizes:
    //    the subpopulation and the index of the emigrant
    // this is virtually the array of mailboxes
    Individual[][] immigrants;

    // the number of immigrants in the storage for each of the subpopulations
    int[] nImmigrants;

    int nrSources;
    
    public boolean chatty;
    
 
    private int checkDefaultSubpopInt(String param, EvolutionState state, int def, int subpop){
        //Default-pop then?
                Parameter p = base.push(P_DEFAULT_SUBPOP);
                int defaultSubpop = state.parameters.getInt(p, null, def); 
                if ( defaultSubpop >= 0){
                    state.output.warning("Using subpopulation " + defaultSubpop + " as the default for subpopulation " + subpop);
                    p = base.push(P_SUBPOP).push(""+defaultSubpop);
                    return state.parameters.getInt( p.push( param ), base.push(param ), def );
            
                }else{
                    state.output.fatal( "Invalid parameter.",  p.push( param ), base.push(param) );
                }
        return -1;
    }

    // sets up the Island Exchanger
    public void setup( final EvolutionState state, final Parameter _base )
        {
        base = _base;

        Parameter p_numsubpops = new Parameter( ec.Initializer.P_POP ).push( ec.Population.P_SIZE );
        int numsubpops = state.parameters.getInt(p_numsubpops,null,1);
        if ( numsubpops == 0 )
            {
            // later on, Population will complain with this fatally, so don't
            // exit here, just deal with it and assume that you'll soon be shut
            // down
            }

        // how many individuals (maximally) would each of the mailboxes have to hold
        int[] incoming = new int[ numsubpops ];

        // allocate some of the arrays
        exchangeInformation = new IPEInformation[ numsubpops ];
        for( int i = 0 ; i < numsubpops ; i++ )
            exchangeInformation[i] = new IPEInformation();
        nImmigrants = new int[ numsubpops ];

        Parameter p;

        Parameter localBase = base.push( P_SUBPOP );

        chatty = state.parameters.getBoolean(base.push(P_CHATTY), null, true);

        for( int i = 0 ; i < numsubpops ; i++ ){

            // update the parameter for the new context
            p = localBase.push( "" + i );

            // read the selection method
            exchangeInformation[i].immigrantsSelectionMethod = (SelectionMethod)
                state.parameters.getInstanceForParameter( p.push( P_SELECT_METHOD ), base.push(P_SELECT_METHOD), ec.SelectionMethod.class );
            if( exchangeInformation[i].immigrantsSelectionMethod == null )
                state.output.fatal( "Invalid parameter.",  p.push( P_SELECT_METHOD ), base.push(P_SELECT_METHOD) );
            exchangeInformation[i].immigrantsSelectionMethod.setup( state, p.push(P_SELECT_METHOD) );

            // read the selection method
            if( state.parameters.exists( p.push( P_SELECT_TO_DIE_METHOD ), base.push(P_SELECT_TO_DIE_METHOD ) ) )
                exchangeInformation[i].indsToDieSelectionMethod = (SelectionMethod)
                    state.parameters.getInstanceForParameter( p.push( P_SELECT_TO_DIE_METHOD ), base.push( P_SELECT_TO_DIE_METHOD ), ec.SelectionMethod.class );
            else // use RandomSelection
                exchangeInformation[i].indsToDieSelectionMethod = new ec.select.RandomSelection();
            exchangeInformation[i].indsToDieSelectionMethod.setup( state, p.push(P_SELECT_TO_DIE_METHOD));

            // get the modulo
            exchangeInformation[i].modulo = state.parameters.getInt( p.push( P_MODULO ), base.push(P_MODULO ), 1 );
            if( exchangeInformation[i].modulo == 0 )
                state.output.fatal( "Parameter not found, or it has an incorrect value.", p.push( P_MODULO ), base.push( P_MODULO ) );

            
            // get the offset
            exchangeInformation[i].offset = state.parameters.getInt( p.push( P_OFFSET ), base.push( P_OFFSET ), 0 );
            if( exchangeInformation[i].offset == -1 )
                state.output.fatal( "Parameter not found, or it has an incorrect value.", p.push( P_OFFSET ), base.push( P_OFFSET ) );
            
            // get the size
            exchangeInformation[i].size = state.parameters.getInt( p.push( P_SIZE ), base.push( P_SIZE ), 1 );
            if( exchangeInformation[i].size == 0 )
                state.output.fatal( "Parameter not found, or it has an incorrect value.", p.push( P_SIZE ), base.push( P_SIZE ) );
            
            // get the number of destinations
            /*exchangeInformation[i].numDest = state.parameters.getInt( p.push( P_DEST_FOR_SUBPOP ), null, 0 );
            if( exchangeInformation[i].numDest == -1 )
                state.output.fatal( "Parameter not found, or it has an incorrect value.", p.push( P_DEST_FOR_SUBPOP ) );*/

            //Pablo
            exchangeInformation[i].numDest = numsubpops-1; //TODO modify this if other topologies used
            
            //End Pablo
            
            exchangeInformation[i].destinations = new int[ exchangeInformation[i].numDest ];
           
            
            // read the destinations
            /*for( int j = 0 ; j < exchangeInformation[i].numDest ; j++ )
                {
                exchangeInformation[i].destinations[j] =
                    state.parameters.getInt( p.push( P_DEST ).push( "" + j ), null, 0 );
                if( exchangeInformation[i].destinations[j] == -1 ||
                    exchangeInformation[i].destinations[j] >= numsubpops )
                    state.output.fatal( "Parameter not found, or it has an incorrect value.", p.push( P_DEST ).push( "" + j ) );
                // update the maximum number of incoming individuals for the destination island
                incoming[ exchangeInformation[i].destinations[j] ] += exchangeInformation[i].size;
                }*/
            
            //SETTING ALL AS POSSIBLE DESTINATIONS
            int c = 0;
            for(int j = 0; j< numsubpops; j++){
                if(j != i){
                    exchangeInformation[i].destinations[c] = j;
                    incoming[ exchangeInformation[i].destinations[c] ] += exchangeInformation[i].size;
                    c++;
                }                 
                    
            }

        }
            
            
            
        // calculate the maximum number of incoming individuals to be stored in the mailbox
        int max = -1;

        for( int i = 0 ; i < incoming.length ; i++ )
            if( max == - 1 || max < incoming[i] )
                max = incoming[i];

        // set up the mailboxes
        immigrants = new Individual[ numsubpops ][ max ];

        }    


    /**
       Initializes contacts with other processes, if that's what you're doing.
       Called at the beginning of an evolutionary run, before a population is set up.
       It doesn't do anything, as this exchanger works on only 1 computer.
    */
    public void initializeContacts(EvolutionState state)
        {
        }

    /**
       Initializes contacts with other processes, if that's what you're doing.  Called after restarting from a checkpoint.
       It doesn't do anything, as this exchanger works on only 1 computer.
    */
    public void reinitializeContacts(EvolutionState state)
        {
        }



    public Population preBreedingExchangePopulation(EvolutionState state)
        {
        // exchange individuals between subpopulations
        // BUT ONLY if the modulo and offset are appropriate for this
        // generation (state.generation)
        // I am responsible for returning a population.  This could
        // be a new population that I created fresh, or I could modify
        // the existing population and return that.

        // for each of the islands that sends individuals
        for( int i = 0 ; i < exchangeInformation.length ; i++ )
            {

            // else, check whether the emigrants need to be sent
            if( ( state.generation >= exchangeInformation[i].offset ) &&
                    ( ( exchangeInformation[i].modulo == 0 ) ||
                    ( ( ( state.generation - exchangeInformation[i].offset ) % exchangeInformation[i].modulo ) == 0 ) ) )
                {

                // send the individuals!!!!

                // for each of the islands where we have to send individuals
                //for( int x = 0 ; x < exchangeInformation[i].numDest ; x++ )
                //    {
                //ADDED: Select random island to send
                    int x = state.random[0].nextInt(exchangeInformation[i].numDest);
                    if (chatty) state.output.message( "Sending the emigrants from subpopulation " +
                        i + " to subpopulation " +
                        exchangeInformation[i].destinations[x] );

                    // select "size" individuals and send then to the destination as emigrants
                    exchangeInformation[i].immigrantsSelectionMethod.prepareToProduce( state, i, 0 );
                    for( int y = 0 ; y < exchangeInformation[i].size ; y++ ) // send all necesary individuals
                        {
                        // get the index of the immigrant
                        int index = exchangeInformation[i].immigrantsSelectionMethod.produce( i, state, 0 );
                        // copy the individual to the mailbox of the destination subpopulation
                        immigrants[ exchangeInformation[i].destinations[x] ]
                            [ nImmigrants[ exchangeInformation[i].destinations[x] ] ] =
                            process(state, 0, null, exchangeInformation[i].destinations[x], (Individual) state.population.subpops[ i ].individuals[ index ].clone());
                        // increment the counter with the number of individuals in the mailbox
                        nImmigrants[ exchangeInformation[i].destinations[x] ]++;
                        }
                    exchangeInformation[i].immigrantsSelectionMethod.finishProducing( state, i, 0 ); // end the selection step
                    }
                //}
            }

        return state.population;

        }
        

    public Population postBreedingExchangePopulation(EvolutionState state)
        {
        // receiving individuals from other islands
        // same situation here of course.

        for( int x = 0 ; x < nImmigrants.length ; x++ )
            {

            if( nImmigrants[x] > 0 && chatty )
                {
                state.output.message( "Immigrating " +  nImmigrants[x] +
                    " individuals from mailbox for subpopulation " + x );
                }
                
            int len = state.population.subpops[x].individuals.length;
            // double check that we won't go into an infinite loop!
            if ( nImmigrants[x] >= state.population.subpops[x].individuals.length )
                state.output.fatal("Number of immigrants ("+nImmigrants[x] +
                    ") is larger than subpopulation #" + x + "'s size (" +
                    len +").  This would cause an infinite loop in the selection-to-die procedure.");

            boolean[] selected = new boolean[ len ];
            int[] indices = new int[ nImmigrants[x] ];
            for( int i = 0 ; i < selected.length ; i++ )
                selected[i] = false;
            exchangeInformation[x].indsToDieSelectionMethod.prepareToProduce( state, x, 0 );
            for( int i = 0 ; i < nImmigrants[x] ; i++ )
                {
                do {
                    indices[i] = exchangeInformation[x].indsToDieSelectionMethod.produce( x, state, 0 );
                    }
                while( selected[indices[i]] );
                selected[indices[i]] = true;
                }
            exchangeInformation[x].indsToDieSelectionMethod.finishProducing( state, x, 0 );

            for( int y = 0 ; y < nImmigrants[x] ; y++ )
                {
                // read the individual
                state.population.subpops[x].individuals[ indices[y] ] = immigrants[x][y];

                // reset the evaluated flag (the individuals are not evaluated in the current island */
                state.population.subpops[x].individuals[ indices[y] ].evaluated = false;

                }

            // reset the number of immigrants in the mailbox for the current subpopulation
            // this doesn't need another synchronization, because the thread is already synchronized
            nImmigrants[x] = 0;
            }

        return state.population;

        }



    /** Called after preBreedingExchangePopulation(...) to evaluate whether or not
        the exchanger wishes the run to shut down (with ec.EvolutionState.R_FAILURE).
        This would happen for two reasons.  First, another process might have found
        an ideal individual and the global run is now over.  Second, some network
        or operating system error may have occurred and the system needs to be shut
        down gracefully.
        This function does not return a String as soon as it wants to exit (another island found
        the perfect individual, or couldn't connect to the server). Instead, it sets a flag, called
        message, to remember next time to exit. This is due to a need for a graceful
        shutdown, where checkpoints are working properly and save all needed information. */
    public String runComplete(EvolutionState state)
        {
        return null;
        }

    /** Closes contacts with other processes, if that's what you're doing.  Called at the end of an evolutionary run. result is either ec.EvolutionState.R_SUCCESS or ec.EvolutionState.R_FAILURE, indicating whether or not an ideal individual was found. */
    public void closeContacts(EvolutionState state, int result)
        {
        }

    }
