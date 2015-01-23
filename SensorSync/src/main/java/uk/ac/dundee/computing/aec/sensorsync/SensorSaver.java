/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;

import com.datastax.driver.core.Cluster;
import org.json.JSONObject;
import uk.ac.dundee.computing.aec.sensorsync.lib.CassandraHosts;

/**
 *
 * @author andycobley
 */
public class SensorSaver {
    Cluster cluster=null;
    void SensorSaver(){
         cluster = CassandraHosts.getCluster();
    }
    
    public void Save (StringBuffer jsonstring){
        String sBuff= jsonstring.toString();
        JSONObject obj = new JSONObject(sBuff);
        String SensorName=obj.getJSONObject("SensorData").getString("name");
        System.out.println("Sensor Name "+SensorName);
    }
            
    
}
