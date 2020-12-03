/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.SubTree.lca;
 
import ca.mcmaster.ccacbnovember2020.SubTree.Lite_VariableAndBound;
import ca.mcmaster.ccacbnovember2020.SubTree.VariableAndBound;
import ilog.cplex.IloCplex;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author tamvadss
 * 
 * a serializable version of the LCA node
 */
public class Lite_LCA_Node implements Serializable {
    
    public int myID;
    public double lpRelax;
    
    //bound and isUpperBound ?
    public HashMap<Lite_VariableAndBound , Boolean> varFixings = new HashMap<Lite_VariableAndBound , Boolean>();
    
    public Lite_LCA_Node (HashMap<VariableAndBound , Boolean> var_Fixings) {
        for (Map.Entry<VariableAndBound , Boolean> entry :var_Fixings.entrySet()){
            Lite_VariableAndBound vb = new Lite_VariableAndBound ();
            vb.bound = entry.getKey().getBound();
            vb.varName= entry.getKey().getVar().getName();
            varFixings.put (vb, entry.getValue());
        }
    }
    
    public String printMe (){
        
        String varfixings = "";
        
        System.out.println("lpRelax "+ lpRelax + " Id " + myID) ;
          
        System.out.println("lca var fixings");
        for (Map.Entry<Lite_VariableAndBound , Boolean> fixing : varFixings.entrySet()){
            varfixings += ("fixing " + fixing.getKey().varName + 
                    " bound " + fixing.getKey().bound + " "+ fixing.getValue() + "\n");
        }
        System.out.println(varfixings);
        
        return varfixings;
    }
}
