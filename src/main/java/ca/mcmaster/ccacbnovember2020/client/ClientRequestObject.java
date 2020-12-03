/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.client;

import static ca.mcmaster.ccacbnovember2020.Constants.*; 
import ca.mcmaster.ccacbnovember2020.SubTree.lca.Lite_LCA_Node;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 *
 * @author tamvadss
 */
public class ClientRequestObject  implements Serializable {
    
    public String clientName=null;
    
    public Boolean isIdle=null ;
    
    public double local_bestBound =BILLION;
    public double local_incumbent =BILLION;
    public long numNodesProcessed = ZERO;
    
    //key is lp relax, value is a list of available LCA nodes
    public TreeMap < Double, ArrayList<Lite_LCA_Node>> availableLCANodes=null;
     
    
}
