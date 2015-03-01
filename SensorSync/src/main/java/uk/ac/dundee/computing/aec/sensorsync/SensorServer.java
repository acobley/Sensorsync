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
import java.nio.charset.Charset;

import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;
import uk.ac.dundee.computing.aec.sensorsync.lib.CassandraHosts;

/**
 *
 * @author Administrator Based upon
 * https://mina.apache.org/mina-project/quick-start-guide.html
 */
public class SensorServer  {

    private ServerSocket serverSocket;
    private int port = 0;
    private String ListenAddress = "127.0.0.1";

    public SensorServer(int port) throws IOException {
        //serverSocket = new ServerSocket(port);
        this.port = port;
        InetAddress address = InetAddress.getLocalHost();
        System.out.println("Address = " + address.getHostAddress());
        ListenAddress = address.getHostAddress();

        //serverSocket.setSoTimeout(50000);
    }

    public void run() throws IOException {
        Cluster cluster = CassandraHosts.getCluster();

        Session session = cluster.connect();
        SensorSaver sv = new SensorSaver(cluster, session);
        IoAcceptor acceptor = new NioSocketAcceptor();

        acceptor.getFilterChain().addLast( "logger", new LoggingFilter() );
        acceptor.getFilterChain().addLast( "codec", new ProtocolCodecFilter( new TextLineCodecFactory( Charset.forName( "UTF-8" ))));
        acceptor.setHandler(  new SensorHandler() );
        acceptor.getSessionConfig().setReadBufferSize( 2048 );
        acceptor.getSessionConfig().setIdleTime( IdleStatus.BOTH_IDLE, 10 );
 
            acceptor.bind(new InetSocketAddress(ListenAddress, port));

    }
}
