package uk.ac.dundee.computing.aec.sensorsync.lib;

import java.util.ArrayList;
import java.util.List;

import com.datastax.oss.driver.api.core.*;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;

public final class Keyspaces {

    public Keyspaces() {

    }

    public static void SetUpKeySpaces(CqlSession session) {
        try {
            //Add some keyspaces here
            String createkeyspace = "create keyspace if not exists sensorsync  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
            String CreateSensorTable = "Create table if not exists sensorsync.Sensors(\n" +
"	name uuid,\n" +
"	insertion_time timestamp,\n" +
"	metadata map <text,text>,\n" +
"	reading map <text,frozen<SensorReading>>,\n" +
"	Primary Key (name,insertion_time)\n" +
")WITH CLUSTERING ORDER BY (insertion_time DESC);";
            
            String CreateSensorType = "CREATE TYPE if not exists sensorsync.SensorReading (\n" +
"	\n" +
"	fValue	float,\n" +
"	sValue	text,\n" +
"	iValue  int,\n" +
        "Accuracy float\n"+            
");";
            
            
            try {
                PreparedStatement statement = session
                        .prepare(createkeyspace);
                BoundStatement boundStatement = statement.bind();
                ResultSet rs = session
                        .execute(boundStatement);
                System.out.println("created Sensorsync keyspace ");
            } catch (Exception et) {
                System.out.println("Can't create Sensorsync keyspace  " + et);
            }
System.out.println("" + CreateSensorType);
            try {
                SimpleStatement cqlQuery = SimpleStatement.newInstance(CreateSensorType);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create sensortype " + et);
            }
            //now add some column families 
            System.out.println("" + CreateSensorTable);

            try {
                SimpleStatement cqlQuery = SimpleStatement.newInstance(CreateSensorTable);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create sensor  table " + et);
            }

            
            
            session.close();

        } catch (Exception et) {
            System.out.println("Other keyspace or coulm definition error" + et);
        }

    }
}
