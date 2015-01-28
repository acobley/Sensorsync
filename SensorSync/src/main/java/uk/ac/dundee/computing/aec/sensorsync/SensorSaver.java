/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;

import com.datastax.driver.core.Cluster;
import java.util.Iterator;
import org.json.JSONArray;
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
        String InsertionTime=obj.getJSONObject("SensorData").getString("insertion_time");
        System.out.println("Sensor Name "+SensorName);
        System.out.println("Insertion Time "+InsertionTime);
        JSONArray arr = obj.getJSONArray("sensors");
        for (int i = 0; i < arr.length(); i++){
            JSONObject objA =arr.getJSONObject(i);
            String [] names=JSONObject.getNames(objA);
            
            for (int j=0; j<names.length;j++){
                System.out.print("Name "+ names[j]+" ");
                System.out.println(objA.getString(names[i]));
                
            }
            /*
            Iterator keys=objA.keys();
            while (keys.hasNext()){
                System.out.println("Keys "+keys.toString());
                keys.next();
            }
                    */
            
        }
        
    }
            
    
}
