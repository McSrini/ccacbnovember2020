/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.client;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import ca.mcmaster.ccacbnovember2020.Parameters;
import static ca.mcmaster.ccacbnovember2020.Parameters.*;
import ca.mcmaster.ccacbnovember2020.SubTree.Lite_VariableAndBound;
import ca.mcmaster.ccacbnovember2020.SubTree.TreeStructureNode;
import ca.mcmaster.ccacbnovember2020.SubTree.VariableAndBound;
import ca.mcmaster.ccacbnovember2020.SubTree.lca.LCA_Node;
import ca.mcmaster.ccacbnovember2020.SubTree.lca.Lite_LCA_Node;
import ca.mcmaster.ccacbnovember2020.SubTree.lca.SubTree_LCA;
import ca.mcmaster.ccacbnovember2020.server.ServerResponseObject;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import static java.lang.System.exit;
import static java.lang.Thread.sleep;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 *
 * @author tamvadss
 */
public class Client {
    
    
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Client.class); 
    private static   String clientname =null;
    
    //used for communication to server
    private static TreeMap < Double, ArrayList<Lite_LCA_Node>> map_of_Lite_LCA_Nodes = 
            new TreeMap < Double, ArrayList<Lite_LCA_Node>> ();
    
    //job I will work on, null to begin
    private static SubTree_LCA mySubTree= null;
    
    private static boolean willBeIdleForWholeCycle = false;
    
    static {
        logger.setLevel( LOGGING_LEVEL);
        PatternLayout layout = new PatternLayout("%5p  %d  %F  %L  %m%n");     
        try {
            RollingFileAppender rfa =new  RollingFileAppender(layout,LOG_FOLDER+ Client.class.getSimpleName()+ LOG_FILE_EXTENSION);
            rfa.setMaxBackupIndex(SIXTY);
            logger.addAppender(rfa);
            logger.setAdditivity(false);
        } catch (Exception ex) {
            ///
            System.err.println("Exit: unable to initialize logging"+ex);       
            exit(ONE);
        }
    } 
    
    public static void main(String[] args) throws Exception {
        
        clientname =  InetAddress.getLocalHost(). getHostName() ;
        
        try (
            Socket workerSocket = new Socket(SERVER_NAME, SERVER_PORT_NUMBER);                
            ObjectOutputStream  outputStream = new ObjectOutputStream(workerSocket.getOutputStream());
            ObjectInputStream inputStream =  new ObjectInputStream(workerSocket.getInputStream());
            
        ){
            
            logger.info ("Client is starting ... "+ clientname) ;
           
            for (int iteration = ZERO ; iteration <  MAX_SOLUTION_CYCLES ; iteration  ++){
                
                //get work from server
                ClientRequestObject request = prepareRequest(iteration) ;
                logger.info (" sending request "   );
                outputStream.writeObject(request);
               
                ServerResponseObject response = (ServerResponseObject) inputStream.readObject();
                
                
                if (  response.haltFlag) break;
                long pruneTime_milliSeconds = processResponse(response) ;
                
                logger.info ("processresponse and prune took seconds" + pruneTime_milliSeconds/THOUSAND);
                
                //solve for SOLUTION_CYCLE_TIME
                long  startTime = System.currentTimeMillis();                 
                if (!mySubTree.isCompletelySolved) mySubTree.solve(response.globalIncumbent,pruneTime_milliSeconds );
                long idleTime = THOUSAND * SOLUTION_CYCLE_TIME_SECONDS  - (System.currentTimeMillis() -startTime);
                if ( idleTime > ZERO) {
                    logger.info ("Client "+ clientname + " will be idle for  sec " + idleTime/THOUSAND);
                    sleep(idleTime) ;
                } 
                            
                           
            }//end for iterations
            
            
             
            workerSocket.close();
            logger.info ("Client is stopping ... "+ clientname) ;
             
        } catch (Exception ex) {
             System.err.println(ex);
             ex.printStackTrace();
        }
        
    }
    
    public static void test_hook () throws IloException{
        Map<Lite_VariableAndBound , Boolean> init =  new HashMap<Lite_VariableAndBound , Boolean>() ;
       
        Lite_VariableAndBound vb1 = new Lite_VariableAndBound (("x453"), 0);
        Lite_VariableAndBound vb2 = new Lite_VariableAndBound (("x454"), 1);
         
        init.put (vb1, true) ;
        init.put (vb2, false);
        
        mySubTree   = new SubTree_LCA (  new HashMap<Lite_VariableAndBound , Boolean>(), 0 );
        mySubTree.test_Solve();
        
        ClientRequestObject cro = prepareRequest (1);
        
        ServerResponseObject responseFromServer = new ServerResponseObject ();
        responseFromServer.pruneList =   new TreeMap < Double, Integer> ();
        for (Map.Entry < Double, ArrayList<Lite_LCA_Node>> entry : cro.availableLCANodes.entrySet()){
            responseFromServer.pruneList.put( entry.getKey(),  entry.getValue().size()>1?2:1);
        }
        processResponse (responseFromServer );
         
    }
    
    private static long  processResponse(ServerResponseObject responseFromServer ) throws IloException {
        logger.info(" processing Response " );
        
        long result = ZERO;
        willBeIdleForWholeCycle =false;
        
        if (null != responseFromServer.pruneList){
            //prune leafs for LCA nodes that were migrated to other workers   
            long  prune_startTime = System.currentTimeMillis();
            mySubTree.prune(getNodeIDs_ForPruning (responseFromServer.pruneList));
            result = System.currentTimeMillis() - prune_startTime;
            
        }
        if (null!=responseFromServer.assignment){
            //
            logger.info(" creating SubTree_LCA ... " );
            int  iterationsCompleted = mySubTree == null ? ZERO: mySubTree.iterationsCompleted;
            mySubTree = new SubTree_LCA ( responseFromServer.assignment , iterationsCompleted);
            logger.info(" SubTree_LCA created " );
            
        }else if (mySubTree.isCompletelySolved){
            logger.warn ("idle worker got no assignent") ;
            willBeIdleForWholeCycle= true;
        }
        return result;
    }
    
    private static    ClientRequestObject  prepareRequest( int iteration) {
        ClientRequestObject req = new ClientRequestObject ();
        req.clientName=clientname;
        if (ZERO==iteration) {
            //just starting, get assignment of ramp up
            req.isIdle= true;            
        }else {
            if (mySubTree.isCompletelySolved){
                req.isIdle = true;
                req.local_bestBound = mySubTree.bestBoundAchieved;
                req.local_incumbent= mySubTree.bestSolutionFound;
                if (! willBeIdleForWholeCycle) req.numNodesProcessed= mySubTree.numNodesProcessed;
            }else {
                req.isIdle = false;
                req.local_bestBound = mySubTree.bestBoundAchieved;
                req.local_incumbent= mySubTree.bestSolutionFound;
                //req.numNodesProcessed= mySubTree.numNodesProcessed;
                //get the perfect LCA nodes
                mySubTree.getPerfectLCANodes();
                if (mySubTree.collectedPerfectLCANodes != null && mySubTree.collectedPerfectLCANodes.size()>ZERO){
                    prepare_Map_of_Lite_LCA_Nodes(mySubTree.collectedPerfectLCANodes);
                    req.availableLCANodes = map_of_Lite_LCA_Nodes;
                }                
            }
        }
        return req;
    }
    
    private static void prepare_Map_of_Lite_LCA_Nodes ( Map < TreeStructureNode ,  LCA_Node  > collectedPerfectLCANodes){
        map_of_Lite_LCA_Nodes.clear();
        for (  LCA_Node lcaNode :  collectedPerfectLCANodes.values()){
            double lpRealx = lcaNode.lpRelax;
            if (!map_of_Lite_LCA_Nodes.containsKey( lpRealx)) {
                map_of_Lite_LCA_Nodes.put (lpRealx, new  ArrayList<Lite_LCA_Node>() );
            }
            ArrayList<Lite_LCA_Node> thisList = map_of_Lite_LCA_Nodes.get( lpRealx);
            thisList.add (lcaNode.getLiteVersion());
            map_of_Lite_LCA_Nodes.put (lpRealx, thisList);
        }
        
        map_of_Lite_LCA_Nodes = trim_Lite_LCA_Node_map ( );
        
        //print lite map
        /*for (Map.Entry < Double, ArrayList<Lite_LCA_Node>> entry : map_of_Lite_LCA_Nodes.entrySet()){
            System.out.print(entry.getKey()+ " : " );
            for (Lite_LCA_Node litenode : entry.getValue()){
                System.out.print(litenode.myID + " ") ;
            }
        }*/
        
        //randomize order
        //for (Map.Entry < Double, ArrayList<Lite_LCA_Node>> entry : map_of_Lite_LCA_Nodes.entrySet()){
            //Collections.shuffle( entry.getValue(), new Random(RANDOM_SEED) );
        //}
        
    }
    
    //only need NUM_WORKS -1 entries in map
    private static  TreeMap < Double, ArrayList<Lite_LCA_Node>> trim_Lite_LCA_Node_map  (){
        TreeMap < Double, ArrayList<Lite_LCA_Node>> trimmedMap =             
                new TreeMap < Double, ArrayList<Lite_LCA_Node>> ();
        
        int occupancyCount = ZERO;
        for (Map.Entry < Double, ArrayList<Lite_LCA_Node>> entry : map_of_Lite_LCA_Nodes.entrySet()){
            if (occupancyCount >= Parameters.NUM_WORKERS - ONE){
                break;
            }else {
                ArrayList<Lite_LCA_Node> thisList = entry.getValue();
                int spaceReamining = Parameters.NUM_WORKERS - ONE - occupancyCount;
                while (spaceReamining < thisList.size()){
                    thisList.remove(ZERO);
                }
                trimmedMap.put ( entry.getKey(), thisList);
                occupancyCount += thisList.size();                 
            }
        }
        
        return trimmedMap;
        
    }
    
    private static Set< IloCplex.NodeId >  getNodeIDs_ForPruning (TreeMap < Double, Integer> pruneList){
        Set< IloCplex.NodeId > result = new HashSet< IloCplex.NodeId >()        ;
        Set<Integer> migrated_lca_Node_IDs = getLCANOdesChosenForMigration (pruneList);
        
        /*System.out.println ("\n getNodeIDs_ForPruning") ;
        for (Integer liteID : migrated_lca_Node_IDs){
            System.out.println (liteID);
        }*/
         
        for ( Map.Entry < TreeStructureNode ,  LCA_Node  > entry : mySubTree.collectedPerfectLCANodes.entrySet()) {
            //
            if (migrated_lca_Node_IDs.size()==ZERO) break;
            if (migrated_lca_Node_IDs.remove( entry.getValue().myID)){
                
                
                
                result.addAll( entry.getValue().leafSet);
            }
        }
         
        return result;
    }
    
    private static Set<Integer>  getLCANOdesChosenForMigration (TreeMap < Double, Integer> pruneList){
        Set<Integer>   idList = new HashSet<Integer>  ();
        for (Map.Entry < Double, Integer> entry: pruneList.entrySet()){
            ArrayList<Lite_LCA_Node> thisList = map_of_Lite_LCA_Nodes.remove( entry.getKey());  
            for (int index = ZERO; index < entry.getValue(); index ++){
                Lite_LCA_Node lcaLite = thisList.remove(thisList.size()-ONE);
                idList.add (lcaLite.myID );
            }
        }
        return idList ;
    }
    
}
