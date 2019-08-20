package uk.ac.dundee.computing.aec.sensorsync.lib;

import com.datastax.oss.driver.api.core.*;
import java.net.InetSocketAddress;

import java.util.Iterator;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

/**
 * ********************************************************
 *
 *
 * @author administrator
 *
 * Hosts are 192.168.2.10 Seed for Vagrant hosts
 *
 *
 *
 *
 */
public final class CassandraHosts {

    //static String Host = "127.0.0.1";  //at least one starting point to talk to
    static String Host = "172.17.0.2";  //at least one starting point to talk to
    public CassandraHosts() {

    }

    public static String getHost() {
        return (Host);
    }

    

    public static CqlSession getCluster() {
        System.out.println("getCluster");
        CqlSession session = CqlSession.builder()
                .addContactPoint(new InetSocketAddress(Host,9042))
                .withLocalDatacenter("datacenter1")
                .build();
        
       
        Keyspaces.SetUpKeySpaces(session);

        return session;

    }

  

}
