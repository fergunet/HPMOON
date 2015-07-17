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
import ec.util.Parameter;

/**
 *
 * @author pgarcia
 */
public class HPMOONUtils {
    
    public static int[] getIslandIdAndNumIslands(EvolutionState state, int subpopulation, int thread){
        
        int[] info = new int[2];
         //Parameter paramIslandId = this.mybase.push("islandId"); (this would search ec.subpopulation.blahblah
        Parameter hpmoonNumIslands = new Parameter("hpmoon.num-islands");
        Parameter hpmoonIslandId = new Parameter("hpmoon.island-id");
        
        if(state.parameters.exists(hpmoonNumIslands, null)){
            //Exchanger 
            info[1] = state.parameters.getInt(hpmoonNumIslands, null); //number of islands
            info[0] = state.parameters.getInt(hpmoonIslandId, null); //islandId
            
            
        }else{
            Parameter exchanger = new Parameter("exch");
            String islandExc = state.parameters.getString(exchanger, null);
            if(islandExc.equals("ec.exchange.InterPopulationExchange") || islandExc.equals("es.ugr.hpmoon.InterPopulationRandomExchange")){
                info[0] = subpopulation;
                info[1] = state.population.subpops.length;
            }else{
                state.output.fatal("ERROR: Not number of islands specified for Island Exchanger, nor InternalIslandExchanger used");
            }
            
              
            
        }
        
        return info;
    
    }
    
}
