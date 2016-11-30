package uk.ac.dundee.computing.aec.sensorsync.lib;

import com.datastax.driver.core.*;

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
 * To run a cassandra node in Docker                         
 * docker run -publish 9042:9042 --name node1 -d -v "$PWD/data:/var/lib/cassandra/data" cassandra:latest
 * Data is stored in $PWD/Data
 * to connect to the node (via docker)
 * docker exec -i -t node1 cqlsh
 * 
 * tomcat on docker
 * docker run -it -d -p 8080:8080 --name tomcat --link node1:node1 -v "$PWD/conf/tomcat-users.xml:/usr/local/tomcat/conf/tomcat-users.xml:ro" tomcat:8.0
 * 
 */
public final class CassandraHosts {

    private static Cluster cluster;
    //static String Host = "127.0.0.1";  //at least one starting point to talk to
    static String Host = "node1";  //at least one starting point to talk to

    public CassandraHosts() {

    }

    public static String getHost() {
        return (Host);
    }

    public static String[] getHosts(Cluster cluster) {

        if (cluster == null) {
            System.out.println("Creating cluster connection");
            cluster = Cluster.builder().addContactPoint(Host).build();
        }
        System.out.println("Cluster Name " + cluster.getClusterName());
        Metadata mdata = cluster.getMetadata();
        Set<Host> hosts = mdata.getAllHosts();
        String sHosts[] = new String[hosts.size()];

        Iterator<Host> it = hosts.iterator();
        int i = 0;
        while (it.hasNext()) {
            Host ch = it.next();
            sHosts[i] = (String) ch.getAddress().toString();

            System.out.println("Hosts" + ch.getAddress().toString());
            i++;
        }

        return sHosts;
    }

    public static Cluster getCluster() {
        System.out.println("getCluster");
        cluster = Cluster.builder()
                .addContactPoint(Host).build();
       
        Keyspaces.SetUpKeySpaces(cluster);
 getHosts(cluster);
        return cluster;

    }

    public void close() {
        cluster.close();
    }

}
