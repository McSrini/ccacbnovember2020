/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.Drivers;

import static ca.mcmaster.ccacbnovember2020.Constants.BILLION;
import static ca.mcmaster.ccacbnovember2020.Constants.MILLION;
import static ca.mcmaster.ccacbnovember2020.Constants.ZERO;
import ca.mcmaster.ccacbnovember2020.SubTree.Lite_VariableAndBound;
import ca.mcmaster.ccacbnovember2020.SubTree.VariableAndBound;
import ca.mcmaster.ccacbnovember2020.SubTree.lca.Lite_LCA_Node;
import ca.mcmaster.ccacbnovember2020.SubTree.lca.SubTree_LCA;
import ca.mcmaster.ccacbnovember2020.client.Client;
import ca.mcmaster.ccacbnovember2020.client.ClientRequestObject;
import ca.mcmaster.ccacbnovember2020.server.Loadbalancer;
import ca.mcmaster.ccacbnovember2020.server.Server;
import ca.mcmaster.ccacbnovember2020.server.ServerResponseObject;
import ilog.concert.IloException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author tamvadss
 */
public class ClientServer_TestDriver {

    /**
    Client - lite map preparation AND translate server response into prune list

    Server - test load balancing
     */
    
    public static void main(String[] args) throws IloException {
         testClient();
         //lca perf var shuffle lca nodes after collection
    }
     
   
    
    public static void testClient() throws IloException {
        Client.test_hook();
    }
    
    public static void testServer() {
        // TODO code application logic here
        Server server = new Server ();
        server.map_Of_IncomingRequests  = new HashMap < String, ClientRequestObject >   ();
        
        ClientRequestObject cro1= new ClientRequestObject ();
        server.map_Of_IncomingRequests.put ("client1", cro1);
        ClientRequestObject cro2= new ClientRequestObject ();
        server.map_Of_IncomingRequests.put ("client2", cro2);
        ClientRequestObject cro3= new ClientRequestObject ();
        server.map_Of_IncomingRequests.put ("client3", cro3);
        ClientRequestObject cro4= new ClientRequestObject ();
        server. map_Of_IncomingRequests.put ("client4", cro4);
        ClientRequestObject cro5= new ClientRequestObject ();
        server.map_Of_IncomingRequests.put ("client5", cro5);
        
        cro1.isIdle = false;
        cro1. clientName="client1";
        cro1.local_bestBound =1;
        cro1. local_incumbent =25;
        cro1. numNodesProcessed = ZERO;
    
        //key is lp relax, value is a list of available LCA nodes
        //public TreeMap < Double, ArrayList<Lite_LCA_Node>> availableLCANodes=null;
        
        cro1. availableLCANodes= new TreeMap < Double, ArrayList<Lite_LCA_Node>>();
        ////////
        Lite_LCA_Node n11 = new Lite_LCA_Node (new HashMap<VariableAndBound , Boolean>());
        ArrayList<Lite_LCA_Node> l1 = new ArrayList<Lite_LCA_Node> ();
        l1.add (n11) ;
        Lite_LCA_Node n12 = new Lite_LCA_Node (new HashMap<VariableAndBound , Boolean>());
        ArrayList<Lite_LCA_Node> l2 = new ArrayList<Lite_LCA_Node> ();
        l2.add (n12) ;
        l1.addAll(l2);
        cro1. availableLCANodes.put (1.0,  l1  );
        //cro1. availableLCANodes.put (1.0,l2 );
        
        //fill up n11 and n12
        n11.myID=1;
        n11.lpRelax = 1.0;
        Lite_VariableAndBound v11 = new  Lite_VariableAndBound();
        v11.bound=11;
        v11.varName="v11";
        n11.varFixings .put (v11, true );
        
        n12.myID =2;
        n12.lpRelax = 1.0;
        Lite_VariableAndBound v12 = new  Lite_VariableAndBound();
        v12.bound=12;
        v12.varName = "v12";
        n12.varFixings .put (v12, true );
        //////
        
        cro2.isIdle = false;
        cro2. clientName="client2";
        cro2.local_bestBound =0;
        cro2. local_incumbent =26;
        cro2. numNodesProcessed = ZERO;
    
        //key is lp relax, value is a list of available LCA nodes
        //public TreeMap < Double, ArrayList<Lite_LCA_Node>> availableLCANodes=null;
        
        cro2. availableLCANodes=    new TreeMap < Double, ArrayList<Lite_LCA_Node>>();
        ////////
        Lite_LCA_Node n21 = new Lite_LCA_Node (new HashMap<VariableAndBound , Boolean>());
        ArrayList<Lite_LCA_Node> L1 = new ArrayList<Lite_LCA_Node> ();
        L1.add (n21) ;
        Lite_LCA_Node n22 = new Lite_LCA_Node (new HashMap<VariableAndBound , Boolean>());
        ArrayList<Lite_LCA_Node> L2 = new ArrayList<Lite_LCA_Node> ();
        L2.add (n22) ;
        cro2. availableLCANodes.put (0.0,  L1  );
        //cro2. availableLCANodes.put (2.0,L2 );
        
        //fill up n11 and n12
        
        n21.myID=1;
        n21.lpRelax = 0.0;
        Lite_VariableAndBound v21 = new  Lite_VariableAndBound();
        v21.bound=21;
        v21.varName="v21";
        n21.varFixings .put (v21, true );
        
        n22.myID =2;
        n22.lpRelax = 2.0;
        Lite_VariableAndBound v22 = new  Lite_VariableAndBound();
        v22.bound=22;
        v22.varName = "v22";
        n22.varFixings .put (v22, true );
        //////
       
        
        cro3.isIdle = true;
        cro3. clientName="client3";
        cro3.local_bestBound =3;
        cro3. local_incumbent =25;
        cro3. numNodesProcessed = 1000;
    
        //key is lp relax, value is a list of available LCA nodes
        //public TreeMap < Double, ArrayList<Lite_LCA_Node>> availableLCANodes=null;
        
        cro3. availableLCANodes= null;


        cro4.isIdle = true;
        cro4. clientName="client4";
        cro4.local_bestBound =4;
        cro4. local_incumbent =25;
        cro4. numNodesProcessed = 5000;
    
        //key is lp relax, value is a list of available LCA nodes
        //public TreeMap < Double, ArrayList<Lite_LCA_Node>> availableLCANodes=null;
        
        cro4. availableLCANodes= null;



        cro5.isIdle = true;
        cro5. clientName="client5";
        cro5.local_bestBound =5;
        cro5. local_incumbent =20;
        cro5. numNodesProcessed = 10000;
    
        //key is lp relax, value is a list of available LCA nodes
        //public TreeMap < Double, ArrayList<Lite_LCA_Node>> availableLCANodes=null;
        
        cro5. availableLCANodes= null;

        
        Server.map_Of_IncomingRequests.put ("client1", cro1) ;
        Server.map_Of_IncomingRequests.put ("client2", cro2) ;
        Server.map_Of_IncomingRequests.put ("client3", cro3) ;
        Server.map_Of_IncomingRequests.put ("client4", cro4) ;
        Server.map_Of_IncomingRequests.put ("client5", cro5) ;

        for (Map.Entry<String, ClientRequestObject> entry : Server.map_Of_IncomingRequests.entrySet()){
            ServerResponseObject resp = new ServerResponseObject () ;  
            resp.globalIncumbent = Server.globalIncombent;                        
            Server.responseMap.put( entry.getKey(), resp);
        }

        Loadbalancer.balance ();
 
        for ( Map .Entry< String, ServerResponseObject > entry : Server.responseMap.entrySet() ){
            
        }
    }
    
}
