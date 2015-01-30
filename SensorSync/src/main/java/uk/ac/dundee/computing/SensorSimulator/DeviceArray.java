/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.SensorSimulator;

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
        for (int i=0; i<sensors.length;i++){
            int type=sensors[i].getType();
            Object value=sensors[i].getValue();
            switch (type){
                case 1:
                    Float fValue=(Float)value;
                    System.out.println(sensors[i].getName()+":"+fValue);
                    break;
                case 2:
                    Integer iValue=(Integer)value;
                    System.out.println(sensors[i].getName()+":"+iValue);
                    break;
                default:
                    System.out.println("Type"+type);
                    break;
            }
        }
    }

}
