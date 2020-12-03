/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.Drivers;

import static ca.mcmaster.ccacbnovember2020.Constants.ZERO;
import static ca.mcmaster.ccacbnovember2020.Parameters.PRESOLVED_MIP_FILENAME;
import ca.mcmaster.ccacbnovember2020.SubTree.Lite_VariableAndBound;
import ca.mcmaster.ccacbnovember2020.SubTree.SubTree;
import ca.mcmaster.ccacbnovember2020.SubTree.VariableAndBound;
import ca.mcmaster.ccacbnovember2020.SubTree.lca.LCA_Node;
import ca.mcmaster.ccacbnovember2020.SubTree.lca.SubTree_LCA;
import ca.mcmaster.ccacbnovember2020.rampup.RampUp;
import ca.mcmaster.ccacbnovember2020.utils.CplexUtils;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author tamvadss
 */
public class TestDriver {
    
    public static void main(String[] args) throws Exception {
        //
        //SubTree subtree = new SubTree (new HashMap<VariableAndBound , Boolean>());
        //subtree.test_Solve();
        
        //RampUp ramp = new RampUp();
        //for (LCA_Node lca : ramp.doRampUp()) {
            //lca.print();
        //}
        
        
        Map<Lite_VariableAndBound , Boolean> init =  new HashMap<Lite_VariableAndBound , Boolean>() ;
        IloCplex cplex = new IloCplex();
        cplex.importModel(   PRESOLVED_MIP_FILENAME);
        
        //Map<String, IloNumVar>  vars =  CplexUtils.getVariables(cplex);
        
        Lite_VariableAndBound vb1 = new Lite_VariableAndBound (("x453"), 0);
        Lite_VariableAndBound vb2 = new Lite_VariableAndBound (("x454"), 1);
         
        init.put (vb1, true) ;
        init.put (vb2, false);
        
        SubTree_LCA subtreeLCA = new SubTree_LCA ( init, 0 );
        subtreeLCA.test_Solve();
        
        
    }
    
}
