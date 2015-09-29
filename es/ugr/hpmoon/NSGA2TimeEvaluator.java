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
import ec.multiobjective.nsga2.NSGA2Evaluator;
import ec.util.Parameter;

/**
 *
 * @author pgarcia
 */
public class NSGA2TimeEvaluator extends NSGA2Evaluator{
    public static final String P_RUNTIME = "runtime";
    public long initTime;
    long runtime;
    public void setup(final EvolutionState state, final Parameter base) {
        super.setup(state, base);
        
        runtime = state.parameters.getLong(base.push(P_RUNTIME), null,-1);
        if(runtime <=0)
            state.output.fatal("ERROR in NSGA2TimeEvaluator.java: Parameter "+P_RUNTIME+" must be set and higher than 0");
        this.initTime = -1;
        
    }

    @Override
    public boolean runComplete(final EvolutionState state) {
        if(initTime<0)
            this.initTime = System.currentTimeMillis();
        long actual = System.currentTimeMillis();
        if ((actual - initTime) > runtime) {
            return true;
        } else {
            return false;
        }
    }
    
}
