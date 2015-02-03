/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.SensorSimulator;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 *
 * @author Administrator
 */
public class Device {
    int numberofsensors=0;
    Sensor sensors[];
    UUID Devicename= UUID.randomUUID();
    Map<String,String> meta=new HashMap<String,String>();
    
    public Device(){
        Random randomno = new Random();
        numberofsensors=randomno.nextInt(20);
        sensors = new Sensor[numberofsensors];
        for (int i=0; i<sensors.length;i++){
            sensors[i]= new Sensor();
            sensors[i].setName("Sensor"+i);
        }
        int numMeta=randomno.nextInt(10);
        //to do finish writing metadata
    }
    
    public UUID getDevice(){
        return Devicename;
    }
    
    public Date getInsertion_time(){
        return new Date();
    }
    
    public Sensor[] getSensors(){
        return sensors;
    }
}
    

