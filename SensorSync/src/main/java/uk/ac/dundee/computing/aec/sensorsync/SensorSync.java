/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;

import com.datastax.driver.core.Cluster;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
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
 * @author andycobley 
 */
public class SensorSync {

    /**
     * @param args the command line arguments
     */
    static int PORT = 19877;
    private static String ListenAddress = "127.0.0.1";

    //private static final int PORT = 9123;

    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        InetAddress address = InetAddress.getLocalHost();
        System.out.println("Address = " + address.getHostAddress());
        ListenAddress = address.getHostAddress();
        IoAcceptor acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter());
        TextLineCodecFactory tc = new TextLineCodecFactory(Charset.forName("UTF-8"));
        tc.setDecoderMaxLineLength(16048);
        ProtocolCodecFilter pc = new ProtocolCodecFilter(tc);

        acceptor.getFilterChain().addLast("codec", pc);
        acceptor.setHandler(new SensorHandler());

        acceptor.getSessionConfig().setReadBufferSize(16048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);
        acceptor.bind(new InetSocketAddress(ListenAddress, PORT));;
    }

}
