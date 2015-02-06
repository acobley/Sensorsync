/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.SensorSimulator;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class DeviceThread extends Thread {

    Device dd = new Device();
    boolean running =false;
    String ip="127.0.0.1";
    public DeviceThread(String ip) {
        super();
        this.ip=ip;
        running=true;
    
    }
    
    public void setIP(String ip){
        this.ip=ip;
    }
    public void interupt(){
        
        running =false;
    }
    public void run()  {
        //while (running) {
        for (int Count=0; Count<100;Count++){
            if (Count %10 ==0){
                System.out.println(dd.getDevice()+" : "+Count+ " :: "+ip);
            }
            Sensor sensors[] = dd.getSensors();
            JSONArray jsonSensors = new JSONArray();
            JSONObject Record = null;
            for (int i = 0; i < sensors.length; i++) {
                int type = sensors[i].getType();
                Object value = sensors[i].getValue();
                Record = new JSONObject();
                Record.put("name", sensors[i].getName());
                switch (type) {
                    case 1:
                        Float fValue = (Float) value;
                        //System.out.println(sensors[i].getName() + ":" + fValue);

                        Record.put("fValue", fValue.toString());
                        break;
                    case 2:
                        Integer iValue = (Integer) value;
                        //System.out.println(sensors[i].getName() + ":" + iValue);
                        Record.put("iValue", iValue.toString());
                        break;
                    default:
                        //System.out.println("Type" + type);
                        break;
                }
                jsonSensors.put(Record);
            }

            JSONObject jsonDevice = new JSONObject();
            jsonDevice.put("device", dd.getDevice());
            jsonDevice.put("insertion_time", dd.getInsertion_time());
            JSONObject jsonMeta=new JSONObject();
            Map<String,String> meta = dd.getMeta();
            for (Map.Entry<String,String> entry : meta.entrySet()) {
                jsonMeta.put(entry.getKey(),entry.getValue());
            }


            JSONObject json = new JSONObject();
            json.put("sensors", jsonSensors);
            json.put("SensorData", jsonDevice);
            json.put("meta",jsonMeta);

            //System.out.println(dd.getDevice());
            Socket sc = null;
            boolean sent=false;
            while (sent == false){
            try {
                sc = new Socket(ip, 19877);
                OutputStream os = sc.getOutputStream();
                PrintWriter out = new PrintWriter(os);
                out.print(json);
                out.close();
                sc.close();
                sent = true;
            } catch (Exception et) {
                System.out.println("No Host "+dd.getDevice() +" : "+ip);
                try{
                 Thread.sleep((long)1);
                }catch(Exception et1){
                    System.out.println("Cant sleep "+et);
                }
            }
            }
            try{
                //System.out.println("Sleeping");
                Thread.sleep((long)0,100);
                Thread.yield();
            }catch (Exception et){
                System.out.println("Sleep went wrong");
            }
            
        }
        


    }

}
