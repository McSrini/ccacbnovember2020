/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.SubTree.controlCallbacks;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import ca.mcmaster.ccacbnovember2020.SubTree.NodeAttachment;
import ca.mcmaster.ccacbnovember2020.SubTree.SubTree;
import ca.mcmaster.ccacbnovember2020.SubTree.VariableAndBound;
import ilog.concert.IloException;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author tamvadss
 * 
 * solve the MIP attaching node attachments as needed 
 * 
 */
public class SolveBranchHandler extends IloCplex.BranchCallback {
    
   
    
    protected void main() throws IloException {
        if ( getNbranches()> ZERO ){  
            String thisNodeID=getNodeId().toString();
                                   
            
            if (SubTree.pruneSet .size()> ZERO  && SubTree.pruneSet.remove( getNodeId())){
                //pruneSet is always empty in multi threaded solve mode
                //System.out.println("pruning "+ getNodeId()) ;
                prune ();                      

            }else {
                //branch
                //get the branches about to be created
                IloNumVar[][] vars = new IloNumVar[TWO][] ;
                double[ ][] bounds = new double[TWO ][];
                IloCplex.BranchDirection[ ][]  dirs = new  IloCplex.BranchDirection[ TWO][];
                getBranches(  vars, bounds, dirs);


                if (thisNodeID.equals( MIPROOT_NODE_ID)){
                    //root node
                    NodeAttachment attachment = new   NodeAttachment (null,  null , false);
                    setNodeData (attachment );
                } 

                NodeAttachment thisNodesAttachment = null;
                try {
                    thisNodesAttachment  = (NodeAttachment) getNodeData () ;
                }        catch (Exception ex){
                    //stays null
                }
                       

                //now allow  both kids to spawn
                for (int childNum = ZERO ;childNum<getNbranches();  childNum++) {   

                    IloNumVar var = vars[childNum][ZERO];
                    double bound = bounds[childNum][ZERO];
                    IloCplex.BranchDirection dir =  dirs[childNum][ZERO];     

                    boolean isDownBranch = dir.equals(   IloCplex.BranchDirection.Down);
                    
                    IloCplex.NodeId  kid = null;
                    if (null==thisNodesAttachment){
                        //default
                        kid = makeBranch(var,bound, dir ,getObjValue());
                    }else {
                        if (isDownBranch){
                            VariableAndBound vb = new VariableAndBound (var, bound) ;
                            thisNodesAttachment.down_Branch_Condition= vb;
                        }

                        NodeAttachment attach = new NodeAttachment (thisNodesAttachment, null, isDownBranch);

                        //create the kid
                        kid = makeBranch(var,bound, dir ,getObjValue(), attach); 
                    }

                    

                    //System.out.println("Node " + getNodeId() + " created " + kid + " isdown " + isDownBranch + " var " + var.getName()) ;

                }  
                
            }//end else
            
        }
    }
    
}
