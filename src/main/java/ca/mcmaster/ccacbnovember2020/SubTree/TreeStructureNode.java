/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.SubTree;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import ilog.cplex.IloCplex;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author tamvadss
 */
public class TreeStructureNode {
    
    //node id available only for each leaf
    public  IloCplex.NodeId  nodeID =null;
    
    public TreeStructureNode parent = null;
    public TreeStructureNode downBranchChild = null;
    public TreeStructureNode upBranchChild = null;
    public NodeAttachment nodeAttachment;
    public double lpRelaxObjective=  BILLION ;
    
    //leaf set available for root
    public Set < TreeStructureNode> leafSet =null;
    
    public long downBranch_nonLeaf_refcount = ZERO;
    public long downBranch_Leaf_refcount = ZERO;
    public long upBranch_Leaf_refcount = ZERO;
    public long upBranch_NonLeaf_refcount = ZERO;
    
}
