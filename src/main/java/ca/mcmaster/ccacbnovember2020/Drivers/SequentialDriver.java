/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020.Drivers;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import ca.mcmaster.ccacbnovember2020.SubTree.SubTree;
import ca.mcmaster.ccacbnovember2020.SubTree.VariableAndBound;
import ilog.cplex.IloCplex;
import static java.lang.System.exit;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

/**
 *
 * @author tamvadss
 */
public class SequentialDriver {
    
    private static Logger logger;
        
    static {
        logger=Logger.getLogger(SequentialDriver.class);
        logger.setLevel(LOGGING_LEVEL);
        PatternLayout layout = new PatternLayout("%5p  %d  %F  %L  %m%n");     
        try {
            RollingFileAppender rfa =new  
                RollingFileAppender(layout,LOG_FOLDER+SequentialDriver.class.getSimpleName()+ LOG_FILE_EXTENSION);
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
        
        SubTree subtree = new SubTree (new HashMap<VariableAndBound , Boolean>());
        
        logger.info ("seqeuntial solver starting") ;
        subtree.solve ( );
        
       
        
        logger.info ("seqeuntial solver completed") ;
          
    }
}
