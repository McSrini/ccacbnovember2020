/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.SubTree;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import ilog.concert.IloNumVar;

/**
 *
 * @author tamvadss
 */
public class VariableAndBound {
    
    private IloNumVar variable=null ;
    private double bound = BILLION;
   
    public VariableAndBound (IloNumVar var  ,    double var_bound   ){
        this .variable = var ;
        this .bound=var_bound;
    }
    
    public IloNumVar getVar (){
        return variable;
    }
    
    public double getBound (){
        return bound;
    }
}
