/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.utils;

import static ca.mcmaster.ccacbnovember2020.Constants.ONE;
import static ca.mcmaster.ccacbnovember2020.Parameters.HUGE_WORKMEM;
import static ca.mcmaster.ccacbnovember2020.Parameters.USE_BARRIER_FOR_SOLVING_LP;
import ilog.concert.IloException;
import ilog.concert.IloLPMatrix;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author tamvadss
 */
public class CplexUtils {
    
    public static void setCplexConfig (IloCplex cplex) throws IloException {
        //cplex.setParam( IloCplex.Param.MIP.Strategy.File,  );
        cplex.setParam( IloCplex.Param.MIP.Strategy.HeuristicFreq , -ONE);
        cplex.setParam( IloCplex.Param.WorkMem, HUGE_WORKMEM) ;
        if (USE_BARRIER_FOR_SOLVING_LP) {
            cplex.setParam( IloCplex.Param.NodeAlgorithm  ,  IloCplex.Algorithm.Barrier);
            cplex.setParam( IloCplex.Param.RootAlgorithm  ,  IloCplex.Algorithm.Barrier);
        }
    }
    
        
    /**
     * 
     *  Update variable bounds as specified    
    */
    public static   void updateVariableBounds(IloNumVar var, double newBound, boolean isUpperBound   )      throws IloException{
 
        if (isUpperBound){
            if ( var.getUB() > newBound ){
                //update the more restrictive upper bound
                var.setUB( newBound );
                //System.out.println(" var " + var.getName() + " set upper bound " + newBound ) ;
            }
        }else{
            if ( var.getLB() < newBound){
                //update the more restrictive lower bound
                var.setLB(newBound);
                //System.out.println(" var " + var.getName() + " set lower bound " + newBound ) ;
            }
        }  

    } 
    
    public static Map<String, IloNumVar> getVariables (IloCplex cplex) throws IloException{
        Map<String, IloNumVar> result = new HashMap<String, IloNumVar>();
        IloLPMatrix lpMatrix = (IloLPMatrix)cplex.LPMatrixIterator().next();
        IloNumVar[] variables  =lpMatrix.getNumVars();
        for (IloNumVar var :variables){
            result.put(var.getName(),var ) ;
        }
        return result;
    }
}
