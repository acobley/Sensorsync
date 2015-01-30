/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.SensorSimulator;

import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class DeviceArray {
    Device dd=null;
    public static void main(String[] args) {
        // TODO code application logic here
        DeviceArray main = new DeviceArray();
        main.createArray();
        main.getArray();
    }
    public void createArray(){
        dd=new Device(); 
    }
    
    public void getArray(){
        System.out.println("DeviceName"+dd.getDevice());
        System.out.println("Insertion_time"+dd.getInsertion_time());
        Sensor sensors[]=dd.getSensors();
        JSONArray jsonSensors = new JSONArray();
        JSONObject Record=null;
        for (int i=0; i<sensors.length;i++){
            int type=sensors[i].getType();
            Object value=sensors[i].getValue();
            Record = new JSONObject();
            Record.put("name",sensors[i].getName());
            switch (type){
                case 1:
                    Float fValue=(Float)value;
                    System.out.println(sensors[i].getName()+":"+fValue);
                    
                    Record.put("fValue",fValue);
                    break;
                case 2:
                    Integer iValue=(Integer)value;
                    System.out.println(sensors[i].getName()+":"+iValue);
                       Record.put("iValue",iValue);
                    break;
                default:
                    System.out.println("Type"+type);
                    break;
            }
             jsonSensors.put(Record);
        }
       
        JSONObject jsonDevice= new JSONObject();
        jsonDevice.put("device", dd.getDevice());
        jsonDevice.put("Insertion_time",dd.getInsertion_time());
        JSONObject json=new JSONObject();
         json.put("sensors", jsonSensors);
        json.put("SensorData", jsonDevice);
       
        
        System.out.println(json.toString());
        Socket sc=null;
        try{
           sc = new Socket("127.0.0.1",19877);
            OutputStream os = sc.getOutputStream();
            PrintWriter out = new PrintWriter(os);
            out.print(json);
            out.close();
            sc.close();
        }catch (Exception et){
            System.out.println("No Host");
        }
       
        
    }

}
