/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Set;
import uk.ac.dundee.computing.aec.sensorsync.lib.CassandraHosts;

/**
 *
 * @author Administrator Based upon
 * http://www.tutorialspoint.com/java/java_networking.htm
 */
public class SensorServer extends Thread {

    private ServerSocket serverSocket;
    private int port = 0;
    private String ListenAddress ="127.0.0.1";

    public SensorServer(int port) throws IOException {
        //serverSocket = new ServerSocket(port);
        this.port = port;
        InetAddress address=InetAddress.getLocalHost();
        System.out.println("Address = "+address.getHostAddress());
        ListenAddress=address.getHostAddress();
        
        //serverSocket.setSoTimeout(50000);

    }

    public void run() {
        Cluster cluster = CassandraHosts.getCluster();

        Session session = cluster.connect();
        ServerSocketChannel server = null;
        //http://www.onjava.com/pub/a/onjava/2002/09/04/nio.html?page=2
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(false);
        } catch (Exception et) {
            System.out.println("Can't open server socket channel");
            return;
        }
        
        try {
            server.socket().bind(new java.net.InetSocketAddress(ListenAddress, port));

        } catch (Exception et) {
            System.out.println("Can't bind to port " + port);
            return;
        }
        Selector selector = null;
        try {
            selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception et) {
            System.out.println("Can't open Selector ");
            return;
        }

        while (true) {
            StringBuffer buff=new StringBuffer();
            try{
                selector.select();
                // Get keys
                Set keys = selector.selectedKeys();
                Iterator i = keys.iterator();
                while (i.hasNext()) {
                    SelectionKey key = (SelectionKey) i.next();

                    // Remove the current key
                    i.remove();

    // if isAccetable = true
                    // then a client required a connection
                    if (key.isAcceptable()) {
                        // get client socket channel
                        SocketChannel client = server.accept();
                        // Non Blocking I/O
                        client.configureBlocking(false);
                        // recording to the selector (reading)
                        client.register(selector, SelectionKey.OP_READ);
                        continue;
                    }

    // if isReadable = true
                    // then the server is ready to read 
                    if (key.isReadable()) {

                        SocketChannel client = (SocketChannel) key.channel();

                        // Read byte coming from the client
                        int BUFFER_SIZE = 22254;
                        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
                        try {
                            client.read(buffer);
                        } catch (Exception e) {
                            // client is no longer active
                            e.printStackTrace();
                            continue;
                        }

                        // Show bytes on the console
                        buffer.flip();
                        Charset charset = Charset.forName("ISO-8859-1");
                        CharsetDecoder decoder = charset.newDecoder();
                        CharBuffer charBuffer = decoder.decode(buffer);
                        //System.out.print(charBuffer.toString());
                        /*SensorSaver sv = new SensorSaver(cluster, session);
                        if (charBuffer.length() >=2){
                        if (sv.Save(charBuffer.toString()) == false) {
                            this.stop();
                        }
                        }*/
                        buff.append(charBuffer);
                        if (buff.length()>2){
                             //System.out.println(buff);
                             SensorSaver sv = new SensorSaver(cluster, session);
                             if (sv.Save(buff) == false) {
                            this.stop();
                        }
                        }
                        continue;
                    }
                }

            }catch(Exception et){
                System.out.println("Opps somethign went wrong !"+et);
            }
        }
    }
}