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
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class DeviceArray {
    Thread t[]= new Thread[100];
    static String ip="127.0.0.1";
    static int Threads=100;
    public static void main(String[] args) {
        // TODO code application logic here
        final DeviceArray  main = new DeviceArray();
        System.out.println(args.length);

        if (args.length!=0){
            ip=args[0];
            
        }
        
        //http://stackoverflow.com/questions/2541475/capture-sigint-in-java
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                System.out.println("Shutdown hook ran!");
                main.destroyThreads();
            }
        });
        main.createThreads();

    }
    
    public void destroyThreads() {
        for (int i = 0; i < Threads; i++) {
            try {

               
                t[i].interrupt();
                

            } catch (Exception et) {
                et.printStackTrace();

            }
        }
    }
    
    public void createThreads() {
        for (int i = 0; i < Threads; i++) {
            try {
                t[i] = new DeviceThread(ip);
                
            } catch (Exception et) {
                et.printStackTrace();

            }
        }
        for (int i = 0; i < Threads; i++) {
            try {
                //t[i].run();
                t[i].start();
            } catch (Exception et) {
                et.printStackTrace();

            }
        }
    }

}
