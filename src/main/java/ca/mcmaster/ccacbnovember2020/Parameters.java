/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ca.mcmaster.ccacbnovember2020;

import static ca.mcmaster.ccacbnovember2020.Constants.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author tamvadss
 */
public class Parameters {
    
    //public static final String PRESOLVED_MIP_FILENAME = "huahum.pre.sav";  
    public static final String PRESOLVED_MIP_FILENAME = "mip.pre.sav";  
    //public static final String PRESOLVED_MIP_FILENAME = "F:\\temporary files here recovered\\neosnidda.pre.sav";  
        
    public static  int MAX_CPLEX_THREADS =   System.getProperty("os.name").toLowerCase().contains("win") ? 1 : 32;
    public static final int FILE_STRATEGY_DISK_COMPRESSED= 3;    
    public static boolean DISABLE_HEURISTICS= true;
    public static final int HUGE_WORKMEM =(64)*THOUSAND ;
    public static final int MIP_EMPHASIS_TO_USE = 2;
    //public static final long RANDOM_SEED = 1;
    
    public static   int NUM_WORKERS= 5;
    public static final String SERVER_NAME = "miscan-head";
    public static final int SERVER_PORT_NUMBER = 4444;
    public static final int SOLUTION_CYCLE_TIME_SECONDS= 5*360;    //half hour
    public static final int MAX_SOLUTION_CYCLES = BILLION;
    
        
    public static List<String> mipsWithBarrier = new ArrayList<String> (
        Arrays.asList( "roi5alpha.pre.sav","splice1k1.pre.sav", "sing326.pre.sav", "sing44.pre.sav", 
                "supportcase3.pre", "huahum.pre.sav"));
    public static boolean USE_BARRIER_FOR_SOLVING_LP =  mipsWithBarrier.contains(PRESOLVED_MIP_FILENAME);

   
}
