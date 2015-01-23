/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import uk.ac.dundee.computing.aec.sensorsync.lib.CassandraHosts;

/**
 *
 * @author andycobley
 */
public class SensorSync {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SensorSync main = new SensorSync();
        main.start();
    }

    private void start() {
        
        try {
            
            Thread t = new SensorServer(19877);
            t.run();
          

        } catch (IOException et) {
            et.printStackTrace();
            
        }
        System.exit(0);
    }

}
