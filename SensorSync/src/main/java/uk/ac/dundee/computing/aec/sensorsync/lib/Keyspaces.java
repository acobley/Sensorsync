package uk.ac.dundee.computing.aec.sensorsync.lib;

import java.util.ArrayList;
import java.util.List;

import com.datastax.driver.core.*;

public final class Keyspaces {

    public Keyspaces() {

    }

    public static void SetUpKeySpaces(Cluster c) {
        try {
            //Add some keyspaces here
            String createkeyspace = "create keyspace if not exists sensorsync  WITH replication = {'class':'SimpleStrategy', 'replication_factor':1}";
            String CreateSensorTable = "Create table if not exists sensorsync.Sensors(\n" +
"	name uuid,\n" +
"	insertion_time timestamp,\n" +
"	reading map <text,frozen<SensorReading>>,\n" +
"	Primary Key (name,insertion_time)\n" +
");";
            
            String CreateSensorType = "CREATE TYPE if not exists sensorsync.SensorReading (\n" +
"	\n" +
"	fValue	float,\n" +
"	sValue	text,\n" +
"	iValue  int\n" +
");";
            
            Session session = c.connect();
            try {
                PreparedStatement statement = session
                        .prepare(createkeyspace);
                BoundStatement boundStatement = new BoundStatement(
                        statement);
                ResultSet rs = session
                        .execute(boundStatement);
                System.out.println("created Sensorsync keyspace ");
            } catch (Exception et) {
                System.out.println("Can't create Sensorsync keyspace  " + et);
            }
System.out.println("" + CreateSensorType);
            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateSensorType);
                session.execute(cqlQuery);
            } catch (Exception et) {
                System.out.println("Can't create sensortype " + et);
            }
            //now add some column families 
            System.out.println("" + CreateSensorTable);

            try {
                SimpleStatement cqlQuery = new SimpleStatement(CreateSensorTable);
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
