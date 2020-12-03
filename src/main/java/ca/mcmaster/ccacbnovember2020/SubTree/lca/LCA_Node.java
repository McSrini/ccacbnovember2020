/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.SubTree.lca;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import ca.mcmaster.ccacbnovember2020.SubTree.TreeStructureNode;
import ca.mcmaster.ccacbnovember2020.SubTree.VariableAndBound;
import ilog.cplex.IloCplex;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author tamvadss
 */
public class LCA_Node {
    
    private static int START_ID = ZERO;
    
    public int myID;
    public double lpRelax;
    
    //public Set < TreeStructureNode> leafSet  ;
    public Set < IloCplex.NodeId  > leafSet  ;
    
    //bound and isUpperBound ?
    public HashMap<VariableAndBound , Boolean> varFixings = new HashMap<VariableAndBound , Boolean>();
    
    public LCA_Node (){
        myID = ++ START_ID;
    }
    
    public Lite_LCA_Node getLiteVersion (){
        Lite_LCA_Node lcaLite = new Lite_LCA_Node (varFixings);
        lcaLite.lpRelax =lpRelax;
        lcaLite.myID = myID;
        return lcaLite;
    }
    
    public String printMe (){
        
        String varfixings = "";
        
        System.out.println("lpRelax "+ lpRelax + " Id " + myID) ;
        System.out.println("contained leafs "  ) ;
        for (IloCplex.NodeId leafID : leafSet){
            //
            System.out.print(leafID + ", ") ;            
        }
        System.out.println("lca var fixings");
        for (Map.Entry<VariableAndBound , Boolean> fixing : varFixings.entrySet()){
            varfixings += ("fixing " + fixing.getKey().getVar().getName() + 
                    " bound " + fixing.getKey().getBound() + " "+ fixing.getValue() + "\n");
        }
        System.out.println(varfixings);
        
        return varfixings;
    }
    
}
