/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.server;

import static ca.mcmaster.ccacbnovember2020.Constants.BILLION;
import ca.mcmaster.ccacbnovember2020.SubTree.Lite_VariableAndBound; 
import ca.mcmaster.ccacbnovember2020.SubTree.lca.Lite_LCA_Node;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author tamvadss
 */
public class ServerResponseObject  implements Serializable {
    
    public double globalIncumbent = BILLION;
    public boolean haltFlag =false;
    public  HashMap<Lite_VariableAndBound , Boolean> assignment=null ;
    
    
    
    //how many LCA nodes at each LP relax value were distributed to other workers?
    //note , LCA nodes are chosen starting the end of the available list, for each lprelax
    public TreeMap < Double, Integer> pruneList=null;
    
    public String toString () {
        String result = "";
        if (assignment!=null){
            for (Map.Entry<Lite_VariableAndBound , Boolean> entry :assignment.entrySet()){
                result += entry.getKey().varName + " " + entry.getKey().bound + " "+ entry.getValue();
            }
            
        }
        if (pruneList!=null){
            result += pruneList.size();
        }
        return result;
    } 
    
}
