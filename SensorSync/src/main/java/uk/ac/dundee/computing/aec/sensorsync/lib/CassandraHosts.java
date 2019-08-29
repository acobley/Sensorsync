package uk.ac.dundee.computing.aec.sensorsync.lib;

import com.datastax.oss.driver.api.core.*;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
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

        SimpleStatement statement= SimpleStatement.newInstance("select cluster_name from system.local");
        ResultSet rs = null;
        
        try {
        rs = session.execute(statement);
        }catch(Exception et){
            System.out.println("can't execute statement select cluster_name from system.local"+et);
        }
        int Num=rs.getAvailableWithoutFetching();
        if (Num==0) {
            System.out.println("No cluster_name");
            
        } else {
            for (Row row : rs) {
                
                String peer=row.getString("cluster_name");
                System.out.println(peer);
               
            }
        }
        
        
   
        return session;

    }

  

}
