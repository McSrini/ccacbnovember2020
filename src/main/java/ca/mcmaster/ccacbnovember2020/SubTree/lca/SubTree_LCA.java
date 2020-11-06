/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.SubTree.lca;

import ca.mcmaster.ccacbnovember2020.SubTree.SubTree;
import ca.mcmaster.ccacbnovember2020.SubTree.TreeStructureNode;
import ca.mcmaster.ccacbnovember2020.SubTree.VariableAndBound;
import static ca.mcmaster.ccacbnovember2020.Constants.*;
import static ca.mcmaster.ccacbnovember2020.Parameters.*;
import ca.mcmaster.ccacbnovember2020.SubTree.controlCallbacks.SolveBranchHandler;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author tamvadss
 * 
 * solves subtree for some time and  then offers perfect LCA nodes
 * 
 */
public class SubTree_LCA extends SubTree {
    
    private int numCyclesAlready = ZERO;
    public TreeStructureNode root;
    
    public  Map < TreeStructureNode ,  LCA_Node  > collectedPerfectLCANodes = 
            new HashMap < TreeStructureNode ,  LCA_Node  >();
    
    public SubTree_LCA ( Map<VariableAndBound , Boolean> varFixings) throws IloException {
        super (varFixings) ;
    }
     
    @Override
    public void solve () throws IloException{
         
        cplex.setParam( IloCplex.Param.Threads, MAX_CPLEX_THREADS);
        cplex.clearCallbacks();
        cplex.use ( new SolveBranchHandler ( ) );
        cplex.setParam( IloCplex.Param.TimeLimit,  SOLUTION_CYCLE_TIME_SECONDS);
        
        cplex.solve();              
          
        log_statistics(  ++numCyclesAlready );
            
        if (isCompletelySolved()) {
            end();
            root = null;
        } else {
            root = getTreeStructure ( );
        }
           
    }
    
    public void test_Solve () throws IloException {
         
        cplex.setParam( IloCplex.Param.Threads, MAX_CPLEX_THREADS);
        cplex.clearCallbacks();
        cplex.use ( new SolveBranchHandler ( ) );
        cplex.setParam( IloCplex.Param.TimeLimit,  30);
        
        cplex.solve();
        root = getTreeStructure ( );
        
        System.out.println("printing leafs " );
        for (TreeStructureNode tsNode : root.leafSet){
            //
            System.out.println(tsNode.nodeID + ", ");
        }
        
        getPerfectLCANodes();
        
        for (LCA_Node lca : collectedPerfectLCANodes.values()){
            lca.print();
        }
        
        //prune some leafs
        //get tree sturcture again
        //get perfect LCA nodes again
        List<LCA_Node> lcaNodes = new ArrayList<LCA_Node>();
        lcaNodes.addAll(collectedPerfectLCANodes.values() );
        LCA_Node lca_node = lcaNodes.get(ZERO);
        
        List< IloCplex.NodeId >  migratedLeafs  = new ArrayList< IloCplex.NodeId >  () ;
        for (TreeStructureNode leaf : lca_node.leafSet){
            migratedLeafs.add (leaf.nodeID);
        }
        
        for (IloCplex.NodeId nd:  migratedLeafs ){
            System.out.println("prune target " + nd) ;
        }
        
        this.prune(migratedLeafs );
        
        root = getTreeStructure ( );
        System.out.println("printing leafs after prune " );
        for (TreeStructureNode tsNode : root.leafSet){
            //
            System.out.println(tsNode.nodeID + ", ");
        }
        
        System.out.println("Solving for few more seconds ") ;
        
        cplex.setParam( IloCplex.Param.Threads, MAX_CPLEX_THREADS);
        cplex.clearCallbacks();
        cplex.use ( new SolveBranchHandler ( ) );
        cplex.setParam( IloCplex.Param.TimeLimit,  3);
        
        cplex.solve();
        
        root = getTreeStructure ( );
        System.out.println("printing leafs after second solve " );
        for (TreeStructureNode tsNode : root.leafSet){
            //
            System.out.println(tsNode.nodeID + ", ");
        }
        
        getPerfectLCANodes();
        
        for (LCA_Node lca : collectedPerfectLCANodes.values()){
            lca.print();
        }
        
        return;
        
        
    }
    
    public void  getPerfectLCANodes (){
        
        this.collectedPerfectLCANodes.clear();
        
        if (null!=root){
            Set < TreeStructureNode> leafSet=root.leafSet;
            for (TreeStructureNode leaf : leafSet){
                //
                boolean isLeaf = true;
                TreeStructureNode current = leaf;
                TreeStructureNode parent = current.parent;
                
                while (null!= parent){
                    
                    if (!isLeaf){
                        int nonLeafRefcount = current.downBranch_nonLeaf_refcount + current.upBranch_NonLeaf_refcount;
                        if (current.nodeAttachment.am_I_The_Down_Branch_Child){
                            parent.downBranch_nonLeaf_refcount = ONE + nonLeafRefcount;
                        }else {
                            parent.upBranch_NonLeaf_refcount = ONE + nonLeafRefcount;
                        }
                        
                        //add to leaf set on both sides
                        if (current.nodeAttachment.am_I_The_Down_Branch_Child){
                            parent.downBranch_Leaf_set.addAll( current.downBranch_Leaf_set );
                            parent.downBranch_Leaf_set.addAll( current.upBranch_Leaf_set );
                        }else {
                            parent.upBranch_Leaf_set.addAll ( current.downBranch_Leaf_set);
                            parent.upBranch_Leaf_set.addAll ( current.upBranch_Leaf_set);
                        }
                        
                    } else {
                        
                        //add to leaf set on both sides
                        if (current.nodeAttachment.am_I_The_Down_Branch_Child){
                            parent.downBranch_Leaf_set.add( leaf);
                        }else {
                            parent.upBranch_Leaf_set.add (leaf);
                        }
                        
                    }
                    
                    //climb up
                    current = parent;
                    parent= parent.parent;
                    isLeaf = false;
                    
                    //check if current nonleaf node is a perfect LCA node
                    //roo tnode is never collected 
                    if (parent!= null){
                        collectIfPerfect (current);
                    }
                    
                }//end while
                
            }//end for
        }        
        
    }
    
    private void  collectIfPerfect (TreeStructureNode nonLeafNode){
         
        final int LEAF_COUNT= nonLeafNode.downBranch_Leaf_set.size() + nonLeafNode.upBranch_Leaf_set.size();
        final int NONLEAF_COUNT =ONE + nonLeafNode.downBranch_nonLeaf_refcount + nonLeafNode.upBranch_NonLeaf_refcount;
        if (NONLEAF_COUNT + ONE == LEAF_COUNT){
            
            //perfect, collect it and remove child LCA nodes
            LCA_Node perfectLCA = new LCA_Node();
            perfectLCA.lpRelax= nonLeafNode.lpRelaxObjective;
            perfectLCA.leafSet .addAll( nonLeafNode.downBranch_Leaf_set);
            perfectLCA.leafSet .addAll( nonLeafNode.upBranch_Leaf_set);
            perfectLCA .varFixings=getVarFixings (nonLeafNode) ;
            
            this.collectedPerfectLCANodes.put (  nonLeafNode , perfectLCA) ;
            
            if (null != nonLeafNode.downBranchChild) collectedPerfectLCANodes.remove (nonLeafNode.downBranchChild);
            if (null != nonLeafNode.upBranchChild) collectedPerfectLCANodes.remove (nonLeafNode.upBranchChild);
            
                    
        }
         
    }
    
    private  Map<VariableAndBound , Boolean> getVarFixings (TreeStructureNode nonLeafNode) {
        Map<VariableAndBound , Boolean> result = new HashMap<VariableAndBound , Boolean>();
        
        TreeStructureNode current = nonLeafNode;
        TreeStructureNode parent = nonLeafNode.parent;
        while (parent != null) {
            if (current.nodeAttachment.am_I_The_Down_Branch_Child) {
                result.put( parent.nodeAttachment.down_Branch_Condition,true  );
            }else {
                result.put (parent.nodeAttachment.getUpBranch_Condition(),false) ;
            }
            current = parent;
            parent = parent.parent;
        }
        
        
        //add the var fixings with which this subtree was created
        for ( Map.Entry<VariableAndBound , Boolean> entry : this.myRoot_VarFixings.entrySet()){
            result.put (entry.getKey(), entry.getValue());
        }
        
        return result;
    }
    
}
