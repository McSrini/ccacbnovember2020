/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.SubTree;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import java.util.Map;

/**
 *
 * @author tamvadss
 */
public class NodeAttachment {
    
    //private boolean isMarkedForpruning = false;
    public NodeAttachment parentNode = null;
    //am I the down child of my parent ?
    public boolean am_I_The_Down_Branch_Child = false;
    
    //condition used to create the down branch child at this node
    public VariableAndBound down_Branch_Condition= null;
    
    
    public NodeAttachment (  NodeAttachment parent, VariableAndBound downBranch_Condition, 
                             boolean am_I_The_Down_Bramch_Child) {
        //isMarkedForpruning= markForPrune;
        parentNode = parent;
        down_Branch_Condition=downBranch_Condition ;
        this.am_I_The_Down_Branch_Child = am_I_The_Down_Bramch_Child;
    }
    
    public VariableAndBound getUpBranch_Condition (){
        VariableAndBound vb = new VariableAndBound(down_Branch_Condition.getVar(), ONE + down_Branch_Condition.getBound());
        
        return vb;
    }
        
   
    
    /*public boolean isMarkedForPrune(){
        return isMarkedForpruning;
    }
    
    public void markThisLeafForPruning () {
        isMarkedForpruning= true;
    }*/
    
     
    
}
