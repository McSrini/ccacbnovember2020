/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.SubTree.lca;

import ca.mcmaster.ccacbnovember2020.SubTree.TreeStructureNode;
import ca.mcmaster.ccacbnovember2020.SubTree.VariableAndBound;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author tamvadss
 */
public class LCA_Node {
    
    public double lpRelax;
    public Set < TreeStructureNode> leafSet = new HashSet <TreeStructureNode> ();
    
    //bound and isUpperBound ?
    public Map<VariableAndBound , Boolean> varFixings = new HashMap<VariableAndBound , Boolean>();
    
    public void print (){
        System.out.println("lpRelax "+ lpRelax) ;
        System.out.println("contained leafs "  ) ;
        for (TreeStructureNode leaf : leafSet){
            //
            System.out.print(leaf.nodeID + ", ") ;            
        }
        System.out.println("lca var fixings");
        for (Map.Entry<VariableAndBound , Boolean> fixing : varFixings.entrySet()){
            System.out.println ("fixing " + fixing.getKey().getVar().getName() + 
                    " bound " + fixing.getKey().getBound() + " "+ fixing.getValue());
        }
    }
    
}
