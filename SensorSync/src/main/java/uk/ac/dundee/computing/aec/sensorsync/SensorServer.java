/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
 *
 * @author Administrator
 * Based upon http://www.tutorialspoint.com/java/java_networking.htm
 */
public class SensorServer extends Thread{
    private ServerSocket serverSocket;
    
    public SensorServer(int port) throws IOException{
        serverSocket = new ServerSocket(port);
      serverSocket.setSoTimeout(50000);
      
    }
   
    
   public void run() 
   {
      while(true)
      {
         try
         {
            System.out.println("Waiting for client on port " +
            serverSocket.getLocalPort() + "...");
            Socket server = serverSocket.accept();
            System.out.println("Just connected to "+ server.getRemoteSocketAddress());
            DataInputStream in = new DataInputStream(server.getInputStream());
            System.out.println(in.readUTF());

            server.close();
         }catch(SocketTimeoutException s)
         {   
            try{
               serverSocket.close();
            }catch(Exception et){
                System.out.println ("Can't close on timeout");
            }
            System.out.println("Socket timed out!");
            
            
         }catch(IOException e)
         {
            e.printStackTrace();
            break;
         }
      }
    
   }
}
