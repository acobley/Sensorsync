/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import static java.time.Instant.now;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONObject;
import uk.ac.dundee.computing.aec.sensorsync.lib.CassandraHosts;

/**
 *
 * @author andycobley
 */
public class SensorSaver {

    Cluster cluster = null;
    Session session = null;
    UserType SensorReadingType = null;
    private final HashMap CommandsMap = new HashMap();

    public SensorSaver() {
        cluster = CassandraHosts.getCluster();

        session = cluster.connect();
        SensorReadingType = cluster.getMetadata().getKeyspace("sensorsync").getUserType("SensorReading");
        CommandsMap.put("fValue", 1);
        CommandsMap.put("iValue", 2);
        CommandsMap.put("sValue", 3);
        CommandsMap.put("Accuracy", 4);
        CommandsMap.put("name", 5);
    }

    public Session getSession() {
        return this.session;
    }

    public boolean Save(StringBuffer jsonstring) {
        String sBuff = jsonstring.toString();
        JSONObject obj = new JSONObject(sBuff);
        String DeviceName = obj.getJSONObject("SensorData").getString("device");
        UUID dUuid = java.util.UUID.fromString(DeviceName);

        String InsertionTime = obj.getJSONObject("SensorData").getString("insertion_time");
        Date dd = new Date(InsertionTime);

        System.out.println("Device Name " + DeviceName);
        System.out.println("Insertion Time " + InsertionTime);
        JSONArray arr = obj.getJSONArray("sensors");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject objA = arr.getJSONObject(i);
            String[] names = JSONObject.getNames(objA);
            System.out.println("Sensor: ");

            for (int j = 0; j < names.length; j++) {
                System.out.print("Name " + names[j] + " ");
                System.out.println(objA.getString(names[j]));

                int command;
                try {
                    command = (Integer) CommandsMap.get(names[j]);
                } catch (Exception et) {
                    error("Bad Operator " + names[j]);
                    return false;
                }
                switch (command) {
            case 1:
                
                addFloat(names,j,SensorReadingType);
                break;
            
            default:
                error("Bad Operator");
        }
            }

            Statement statement = QueryBuilder.insertInto("sensorsync", "Sensors")
                    .value("name", dUuid)
                    .value("insertion_time", dd);
            getSession().execute(statement);
            /*
             Iterator keys=objA.keys();
             while (keys.hasNext()){
             System.out.println("Keys "+keys.toString());
             keys.next();
             }
             */

        }
        return true;
    }
 
    private void addFloat(String[] names, int j,UserType SensorReadingType) {
        if ((j+1) < names.length){ 
            j++;
            String sFloat=names[j];
            float value;
            value = Float.parseFloat(sFloat);
            
            
        }
        
    }
    
    private void error(String mess) {

        System.out.println("<h1>You have a na error in your input</h1>");
        System.out.println("<h2>" + mess + "</h2>");
        System.out.close();
        return;
    }

}
