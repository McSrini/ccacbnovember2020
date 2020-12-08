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
import ca.mcmaster.ccacbnovember2020.Parameters;
import static ca.mcmaster.ccacbnovember2020.Parameters.*;
import ca.mcmaster.ccacbnovember2020.SubTree.Lite_VariableAndBound;
import ca.mcmaster.ccacbnovember2020.SubTree.controlCallbacks.SolveBranchHandler;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
    
    public int iterationsCompleted ;
     
    public TreeStructureNode root;
    
    public  Map < TreeStructureNode ,  LCA_Node  > collectedPerfectLCANodes = 
            new HashMap < TreeStructureNode ,  LCA_Node  >();
    
    public SubTree_LCA ( Map<Lite_VariableAndBound , Boolean> varFixings, int iterationsCompleted ) throws IloException {
        super (varFixings) ;
        this. iterationsCompleted  =   iterationsCompleted ;
    }
     
    @Override
    public void solve (double cutoff, long time_used_up_for_pruning_millisec) throws IloException{
        
        logger.info (" MIP emphasis is " + Parameters.MIP_EMPHASIS_TO_USE + " "+ USE_BARRIER_FOR_SOLVING_LP+
                 " epsilon " + EPSILON) ;
        
        long solveTimeRemaining_seconds = THOUSAND*SOLUTION_CYCLE_TIME_SECONDS-  time_used_up_for_pruning_millisec ;
        solveTimeRemaining_seconds = solveTimeRemaining_seconds /THOUSAND;
        
        if (solveTimeRemaining_seconds <= SIXTY){
            //set to 1 minute
            solveTimeRemaining_seconds = SIXTY;
        }
         
        cplex.setParam( IloCplex.Param.Threads, MAX_CPLEX_THREADS);
        cplex.clearCallbacks();
        cplex.use ( new SolveBranchHandler ( ) );
        cplex.setParam( IloCplex.Param.TimeLimit,  solveTimeRemaining_seconds );
        if (cutoff < BILLION) cplex.setParam(IloCplex.Param.MIP.Tolerances.UpperCutoff, cutoff);
         
        
        cplex.solve();              
        bestBoundAchieved= cplex.getBestObjValue();
        if (cplex.getStatus().equals( IloCplex.Status.Feasible ) || cplex.getStatus().equals( IloCplex.Status.Optimal )) 
                bestSolutionFound =cplex.getObjValue();
        this.numNodesProcessed = cplex.getNnodes64();
          
        log_statistics(  ++ iterationsCompleted );
            
        if (isCompletelySolved(cutoff)) {
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
        cplex.setParam( IloCplex.Param.TimeLimit,  90);
        
        cplex.solve();
        
        
        
        
        root = getTreeStructure ( );
        
        System.out.println("printing leafs " );
        for (TreeStructureNode tsNode : root.leafSet){
            //
            System.out.println(tsNode.nodeID + ", ");
        }
        
        getPerfectLCANodes();
        
        for (LCA_Node lca : collectedPerfectLCANodes.values()){
            lca.getLiteVersion().printMe();
        }
        
        /*
        
        //prune some leafs
        //get tree sturcture again
        //get perfect LCA nodes again
        List<LCA_Node> lcaNodes = new ArrayList<LCA_Node>();
        lcaNodes.addAll(collectedPerfectLCANodes.values() );
        LCA_Node lca_node = lcaNodes.get(ZERO);
        
        List< IloCplex.NodeId >  migratedLeafs  = new ArrayList< IloCplex.NodeId >  () ;
        for (IloCplex.NodeId leafNodeID : lca_node.leafSet){
            migratedLeafs.add (leafNodeID);
        }
        
        migratedLeafs.remove(migratedLeafs.size()-ONE );
        migratedLeafs.remove(migratedLeafs.size()-ONE );
        System.out.println("\n\n pruning ...");
        for (IloCplex.NodeId nd:  migratedLeafs ){
            System.out.println("prune target " + nd) ;
        }
        Set< IloCplex.NodeId >  migratedLeafsSet  = new HashSet< IloCplex.NodeId >  () ;
        migratedLeafsSet.addAll( migratedLeafs);
        this.prune(migratedLeafsSet );
        
        
        
        root = getTreeStructure ( );
        System.out.println("printing leafs after prune " );
        for (TreeStructureNode tsNode : root.leafSet){
            //
            System.out.println(tsNode.nodeID + ", ");
        }
        
        System.out.println("\n\n Solving for few more seconds ") ;
        
        cplex.setParam( IloCplex.Param.Threads, MAX_CPLEX_THREADS);
        cplex.clearCallbacks();
        cplex.use ( new SolveBranchHandler ( ) );
        cplex.setParam( IloCplex.Param.TimeLimit,  5);
        
        cplex.solve();
        
        root = getTreeStructure ( );
        System.out.println("\n\nprinting leafs after second solve " );
        if (null!=root){
            for (TreeStructureNode tsNode : root.leafSet){
                //
                System.out.println(tsNode.nodeID + ", ");
            }
        }
                
        getPerfectLCANodes();
        
        for (LCA_Node lca : collectedPerfectLCANodes.values()){
            lca.getLiteVersion().printMe();
        }
        
        this.collectedPerfectLCANodes.clear();
        
        return;
        
        */
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
                        long nonLeafRefcount = current.downBranch_nonLeaf_refcount + current.upBranch_NonLeaf_refcount;
                        if (current.nodeAttachment.am_I_The_Down_Branch_Child){
                            parent.downBranch_nonLeaf_refcount = ONE + nonLeafRefcount;
                        }else {
                            parent.upBranch_NonLeaf_refcount = ONE + nonLeafRefcount;
                        }
                        
                        //add to leaf set on both sides
                        if (current.nodeAttachment.am_I_The_Down_Branch_Child){
                            parent.downBranch_Leaf_refcount =current.downBranch_Leaf_refcount +  current.upBranch_Leaf_refcount;
                                    
                        }else {
                            parent.upBranch_Leaf_refcount =  current.downBranch_Leaf_refcount +  current.upBranch_Leaf_refcount;
                          
                        }
                        
                    } else {
                        
                        //add to leaf set on both sides
                        if (current.nodeAttachment.am_I_The_Down_Branch_Child){
                            parent.downBranch_Leaf_refcount = ONE;
                        }else {
                            parent.upBranch_Leaf_refcount = ONE;
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
        
        //for all the collected perfect LCA nodes,populate the leafset and var fixings
        for (Map.Entry<TreeStructureNode, LCA_Node> entry:  collectedPerfectLCANodes.entrySet()){
            entry.getValue().leafSet = getLeafSetForLCANode (entry.getKey() );
            entry.getValue().varFixings = getVarFixings (entry.getKey() ) ;
        }
        
        //clear all the refcounts
        if (null!=root) clearSubtreeRefcounts (root);
        
    }
    
    private void clearSubtreeRefcounts(TreeStructureNode subtreeRoot){
        if (subtreeRoot.downBranchChild!= null || subtreeRoot.upBranchChild!= null){
            subtreeRoot.downBranch_Leaf_refcount = ZERO;
            subtreeRoot.downBranch_nonLeaf_refcount = ZERO;
            subtreeRoot.upBranch_Leaf_refcount = ZERO;
            subtreeRoot.upBranch_NonLeaf_refcount= ZERO;
            
            if (subtreeRoot.downBranchChild!= null){
                clearSubtreeRefcounts(subtreeRoot.downBranchChild);
            }
            if (subtreeRoot.upBranchChild!= null){
                clearSubtreeRefcounts (subtreeRoot.upBranchChild) ; 
            }
        }        

    }
    
    private Set < IloCplex.NodeId> getLeafSetForLCANode (TreeStructureNode lcaNode){
        Set < IloCplex.NodeId> result = new HashSet < IloCplex.NodeId>();
        if (lcaNode.upBranchChild==null && lcaNode.downBranchChild ==null){
            //collect node ID of leaf
            result.add (lcaNode.nodeID ) ;            
        }else {
            if (null!=lcaNode.downBranchChild) result.addAll( getLeafSetForLCANode(lcaNode.downBranchChild));
            if (null!=lcaNode.upBranchChild) result.addAll(getLeafSetForLCANode(lcaNode.upBranchChild) );
        }
        return result;        
    }
    
    private void  collectIfPerfect (TreeStructureNode nonLeafNode){
         
        final long LEAF_COUNT= nonLeafNode.downBranch_Leaf_refcount + nonLeafNode.upBranch_Leaf_refcount;
        final long NONLEAF_COUNT =ONE + nonLeafNode.downBranch_nonLeaf_refcount + nonLeafNode.upBranch_NonLeaf_refcount;
        if (NONLEAF_COUNT + ONE == LEAF_COUNT){
            
            
            
            //perfect, collect it and remove child LCA nodes
            LCA_Node perfectLCA = new LCA_Node();
            perfectLCA.lpRelax= nonLeafNode.lpRelaxObjective;
            //perfectLCA.leafSet .addAll( nonLeafNode.downBranch_Leaf_set);
            //perfectLCA.leafSet .addAll( nonLeafNode.upBranch_Leaf_set);
            //perfectLCA .varFixings=getVarFixings (nonLeafNode) ;
            
            //System.out.println("perfect LCA "+ perfectLCA.myID + " N and L "+  NONLEAF_COUNT + ", " +LEAF_COUNT );
            
            this.collectedPerfectLCANodes.put (  nonLeafNode , perfectLCA) ;
            
            if (null != nonLeafNode.downBranchChild) collectedPerfectLCANodes.remove (nonLeafNode.downBranchChild);
            if (null != nonLeafNode.upBranchChild) collectedPerfectLCANodes.remove (nonLeafNode.upBranchChild);
            
                    
        }
         
    }
    
    private  HashMap<VariableAndBound , Boolean> getVarFixings (TreeStructureNode nonLeafNode) {
        HashMap<VariableAndBound , Boolean> result = new HashMap<VariableAndBound , Boolean>();
        
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
