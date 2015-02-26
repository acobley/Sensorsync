/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import uk.ac.dundee.computing.aec.sensorsync.lib.CassandraHosts;

/**
 *
 * @author Administrator Based upon
 * http://www.studytrails.com/java-io/non-blocking-io-multiplexing.jsp
 */
public class SensorServer extends Thread {

    private ServerSocket serverSocket;
    private int port = 0;
    private String ListenAddress = "127.0.0.1";

    private static String clientChannel = "clientChannel";
    private static String serverChannel = "serverChannel";
    private static String channelType = "channelType";

    public SensorServer(int port) throws IOException {
        //serverSocket = new ServerSocket(port);
        this.port = port;
        InetAddress address = InetAddress.getLocalHost();
        System.out.println("Address = " + address.getHostAddress());
        ListenAddress = address.getHostAddress();

        //serverSocket.setSoTimeout(50000);
    }

    public void run() {
        Cluster cluster = CassandraHosts.getCluster();

        Session session = cluster.connect();
        SensorSaver sv = new SensorSaver(cluster, session);
        ServerSocketChannel server = null;
        StringBuffer sb = null;
        // create a new serversocketchannel. The channel is unbound.

        Selector selector = null;
        ServerSocketChannel channel = null;
        // bind the channel to an address. The channel starts listening to
        // incoming connections.
        try {
            channel = ServerSocketChannel.open();
            channel.bind(new InetSocketAddress(ListenAddress, port));
            // mark the serversocketchannel as non blocking
            channel.configureBlocking(false);
            // create a selector that will by used for multiplexing. The selector
            // registers the socketserverchannel as
            // well as all socketchannels that are created
            selector = Selector.open();
        } catch (IOException et) {
            System.out.println("Can't bind to" + ListenAddress + ":" + port);
            return;
        }

        // register the serversocketchannel with the selector. The OP_ACCEPT
        // option marks
        // a selection key as ready when the channel accepts a new connection.
        // When the
        // socket server accepts a connection this key is added to the list of
        // selected keys of the selector.
        // when asked for the selected keys, this key is returned and hence we
        // know that a new connection has been accepted.
        SelectionKey socketServerSelectionKey = null;
        try {
            socketServerSelectionKey = channel.register(selector, SelectionKey.OP_ACCEPT);
        } catch (Exception et) {
            System.out.println("Can't register channel");
            return;
        }
        // set property in the key that identifies the channel
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(channelType, serverChannel);
        socketServerSelectionKey.attach(properties);
        // wait for the selected keys
        for (;;) {

            // the select method is a blocking method which returns when atleast
            // one of the registered
            // channel is selected. In this example, when the socket accepts a
            // new connection, this method
            // will return. Once a socketclient is added to the list of
            // registered channels, then this method
            // would also return when one of the clients has data to be read or
            // written. It is also possible to perform a nonblocking select
            // using the selectNow() function.
            // We can also specify the maximum time for which a select function
            // can be blocked using the select(long timeout) function.
            try {
                if (selector.select() == 0) {
                    continue;
                }

                // the select method returns with a list of selected keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    // the selection key could either by the socketserver informing
                    // that a new connection has been made, or
                    // a socket client that is ready for read/write
                    // we use the properties object attached to the channel to find
                    // out the type of channel.
                    if (((Map<?, ?>) key.attachment()).get(channelType).equals(
                            serverChannel)) {
                        // a new connection has been obtained. This channel is
                        // therefore a socket server.
                        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
                                .channel();
                        // accept the new connection on the server socket. Since the
                        // server socket channel is marked as non blocking
                        // this channel will return null if no client is connected.
                        SocketChannel clientSocketChannel = serverSocketChannel
                                .accept();

                        if (clientSocketChannel != null) {
                            // set the client connection to be non blocking
                            clientSocketChannel.configureBlocking(false);
                            SelectionKey clientKey = clientSocketChannel.register(
                                    selector, SelectionKey.OP_READ,
                                    SelectionKey.OP_WRITE);
                            Map<String, String> clientproperties = new HashMap<String, String>();
                            clientproperties.put(channelType, clientChannel);
                            clientKey.attach(clientproperties);

                            // write something to the new created client
                            CharBuffer buffer = CharBuffer.wrap("Hello client");
                            while (buffer.hasRemaining()) {
                                clientSocketChannel.write(Charset.defaultCharset()
                                        .encode(buffer));
                            }
                            buffer.clear();
                            //System.out.println("Start ?");
                            sb = new StringBuffer();
                        }

                    } else {
                        // data is available for read
                        // buffer for reading
                        ByteBuffer buffer = ByteBuffer.allocate(20);
                        SocketChannel clientChannel = (SocketChannel) key.channel();
                        int bytesRead = 0;
                        if (key.isReadable()) {
                            
                            // the channel is non blocking so keep it open till the
                            // count is >=0
                            if ((bytesRead = clientChannel.read(buffer)) > 0) {
                                buffer.flip();
                                String out = Charset.defaultCharset().decode(buffer).toString();
                                //System.out.println(Charset.defaultCharset().decode(buffer));
                                sb.append(out);
                                System.out.print(key.hashCode()+" : ");
                                buffer.clear();
                            }
                            if (bytesRead < 0) {
                            // the key is automatically invalidated once the
                                // channel is closed

                                //System.out.println("Done");
                                //System.out.println(sb.toString());
                                if (sv.Save(sb) == false) {
                                    System.out.println("Didn't save" + sb.toString() + " Length" + sb.length());
                                }
                                clientChannel.close();
                            }
                        }

                    }

                    // once a key is handled, it needs to be removed
                    iterator.remove();

                }
            } catch (Exception et) {
                System.out.println("general Exception" + et);
                return;
            }
        }
    }
}
