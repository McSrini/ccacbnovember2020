/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.rampup;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import static ca.mcmaster.ccacbnovember2020.Parameters.PRESOLVED_MIP_FILENAME;
import ca.mcmaster.ccacbnovember2020.SubTree.lca.LCA_Node;
import ca.mcmaster.ccacbnovember2020.SubTree.lca.Lite_LCA_Node;
import ca.mcmaster.ccacbnovember2020.utils.CplexUtils;
import ilog.concert.IloException;
import ilog.cplex.IloCplex;
import ilog.cplex.IloCplex.Status;
import static java.lang.System.exit;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 *
 * @author tamvadss
 */
public class RampUp {
    private static Logger logger = Logger.getLogger(RampUp.class);
    private  static  IloCplex cplex  ;
    
    
    static {
        logger.setLevel( LOGGING_LEVEL);
        PatternLayout layout = new PatternLayout("%5p  %d  %F  %L  %m%n");     
        try {
            RollingFileAppender rfa =new  RollingFileAppender(layout,LOG_FOLDER+RampUp.class.getSimpleName()+ LOG_FILE_EXTENSION);
            rfa.setMaxBackupIndex(SIXTY);
            logger.addAppender(rfa);
            logger.setAdditivity(false);
        } catch (Exception ex) {
            ///
            System.err.println("Exit: unable to initialize logging"+ex);       
            exit(ONE);
        }
    } 
    
    public List<Lite_LCA_Node> doRampUp () throws IloException{
        List<Lite_LCA_Node> result = new ArrayList<Lite_LCA_Node>();
        
        cplex = new IloCplex();
        cplex.importModel(   PRESOLVED_MIP_FILENAME);
        CplexUtils.setCplexConfig (cplex) ;
        RampupNodecallback rn = new RampupNodecallback ();
        RampupBranchCallback bh = new RampupBranchCallback ();
        cplex.use (rn);
        cplex.use (bh );
        cplex.solve ();
        //cplex.end();
        
        for (LCA_Node lca: rn.result){
            //logger.info( lca.printMe());
        }
        for (LCA_Node lca: rn.result){
            Lite_LCA_Node lite = new Lite_LCA_Node (lca.varFixings);
            
            result.add (lite);
        }
        
        return result;
    }
    
    public double getSolutionValue() throws IloException {
        return cplex.getStatus().equals(Status.Feasible) ? cplex.getObjValue() : BILLION;
    }
    
}
