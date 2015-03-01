/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import uk.ac.dundee.computing.aec.sensorsync.lib.CassandraHosts;
/**
 *
 * @author Administrator
 */
public class SensorHandler extends IoHandlerAdapter{

    SensorSaver sv =null;
    public SensorHandler(){
        super();
        Cluster cluster = CassandraHosts.getCluster();

        Session session = cluster.connect();
         sv = new SensorSaver(cluster, session);
    }
    @Override
    public void exceptionCaught( IoSession session, Throwable cause ) throws Exception
    {
        cause.printStackTrace();
    }
    @Override
    public void messageReceived( IoSession session, Object message ) throws Exception
    {
        String str = message.toString();
        if( str.trim().equalsIgnoreCase("quit") ) {
            session.close();
            return;
        }
        sv.Save(str);
        System.out.println("Message"+str);
    }

    @Override
    public void sessionIdle( IoSession session, IdleStatus status ) throws Exception
    {
        System.out.println( "IDLE " + session.getIdleCount( status ));
    }
}
