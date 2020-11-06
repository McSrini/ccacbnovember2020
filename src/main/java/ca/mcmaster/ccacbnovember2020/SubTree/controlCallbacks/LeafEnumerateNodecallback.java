/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.SubTree.controlCallbacks;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import ca.mcmaster.ccacbnovember2020.SubTree.NodeAttachment;
import ca.mcmaster.ccacbnovember2020.SubTree.TreeStructureNode;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author tamvadss
 */
public class LeafEnumerateNodecallback extends IloCplex.NodeCallback {
    
    public Set<TreeStructureNode> leafNodeAttahments = new HashSet <TreeStructureNode> (  );
    
    protected void main() throws IloException {
        //
        final long LEAFCOUNT =getNremainingNodes64();
        if (LEAFCOUNT>ZERO) {
            for (long leafNum = ZERO; leafNum < LEAFCOUNT; leafNum ++){
                TreeStructureNode treeNode = new TreeStructureNode ();
                treeNode.nodeID=getNodeId(leafNum) ;
                treeNode.nodeAttachment =(NodeAttachment)getNodeData(  leafNum );
                leafNodeAttahments.add (treeNode );
                treeNode.lpRelaxObjective = getObjValue (leafNum) ;
            }
        }
        abort();
    }
    
}
