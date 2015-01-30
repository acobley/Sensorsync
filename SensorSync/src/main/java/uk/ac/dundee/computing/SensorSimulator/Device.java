/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.SensorSimulator;

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
    
    public void Device(){
        Random randomno = new Random();
        numberofsensors=randomno.nextInt(100);
        sensors = new Sensor[numberofsensors];
        for (int i=0; i<sensors.length;i++){
            sensors[i]= new Sensor();
        }
    }
    
}
