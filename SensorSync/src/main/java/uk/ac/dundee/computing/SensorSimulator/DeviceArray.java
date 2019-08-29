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

    static int Threads = 1;
    static Thread t[];
    static String ip = "127.0.0.1";
    static long delay = 1;
    static int ReadingCount=10;
    public static void main(String[] args) {
        // TODO code application logic here
        final DeviceArray main = new DeviceArray();
        System.out.println(args.length);

        switch (args.length) {
            
            case 1:
                ip = args[0];
                break;
            case 2:
                ip = args[0];
                try {
                    Threads = Integer.parseInt(args[1]);

                } catch (Exception et) {
                    System.out.println("Incorrect input format for Threads (arg 2)");
                    System.exit(-1);
                }
                break;
            case 3:
                ip = args[0];
                try {
                    Threads = Integer.parseInt(args[1]);

                } catch (Exception et) {
                    System.out.println("Incorrect input format for Threads (arg 2)");
                    System.exit(-1);
                }
                try {
                    delay = Integer.parseInt(args[2]);

                } catch (Exception et) {
                    System.out.println("Incorrect input format for delay (arg 3)");
                    System.exit(-1);
                }
                break;
                case 4:
                ip = args[0];
                try {
                    Threads = Integer.parseInt(args[1]);

                } catch (Exception et) {
                    System.out.println("Incorrect input format for Threads (arg 2)");
                    System.exit(-1);
                }
                try {
                    delay = Integer.parseInt(args[2]);

                } catch (Exception et) {
                    System.out.println("Incorrect input format for delay (arg 3)");
                    System.exit(-1);
                }
                try {
                   ReadingCount = Integer.parseInt(args[3]);

                } catch (Exception et) {
                    System.out.println("Incorrect input format for readingcount (arg 4)");
                    System.exit(-1);
                }
                break;
            default:
                System.out.println("Usage java uk.ac.dundee.computing.SensorSimulator.DeviceArray");
                System.out.println("\t IP Address of server");
                System.out.println("\t Thread Count (number of Devices");
                System.out.println("\t Delay in Milliseconds between readings");
                System.out.println("\t Number of readings to send");
                break;
        }
        t = new Thread[Threads];
        //http://stackoverflow.com/questions/2541475/capture-sigint-in-java
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
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
                t[i] = new DeviceThread(ip, delay,ReadingCount);

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
