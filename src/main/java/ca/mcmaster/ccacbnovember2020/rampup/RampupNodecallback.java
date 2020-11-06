/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.rampup;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import static ca.mcmaster.ccacbnovember2020.Parameters.*;
import ca.mcmaster.ccacbnovember2020.SubTree.lca.LCA_Node;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tamvadss
 */
public class RampupNodecallback extends IloCplex.NodeCallback {
    
    public List<LCA_Node> result = new ArrayList<LCA_Node>();

    @Override
    protected void main() throws IloException {
        //
       
        if (getNremainingNodes64()==NUM_WORKERS){
            //prepare leafs
            for (int leafNum = ZERO; leafNum < NUM_WORKERS ; leafNum ++){
                //
                RampupAttachment attach = (RampupAttachment) getNodeData (leafNum) ;
                LCA_Node lca = new LCA_Node ();
                lca .varFixings = attach.varFixings;
                result.add (lca );
            }

            abort();
        }
    }
    
}
