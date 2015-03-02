/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.UDTValue;
import com.datastax.driver.core.UserType;
import com.datastax.driver.core.querybuilder.QueryBuilder;
import java.text.SimpleDateFormat;

import static java.time.Instant.now;
import java.util.AbstractMap;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.dundee.computing.aec.sensorsync.lib.CassandraHosts;

/**
 *
 * @author andycobley
 */
public class SensorSaver {

    Cluster cluster = null;
    Session session = null;
    UserType SensorReadingType = null;
    int DataCount = 0;
    private final HashMap CommandsMap = new HashMap();

    public SensorSaver(Cluster cluster, Session session) {
        this.session = session;
        SensorReadingType = cluster.getMetadata().getKeyspace("sensorsync").getUserType("SensorReading");
        CommandsMap.put("fValue", 1);
        CommandsMap.put("iValue", 2);
        CommandsMap.put("sValue", 3);
        CommandsMap.put("Accuracy", 4);
        CommandsMap.put("name", 5);
    }

    public Session getSession() {
        return this.session;
    }

    public boolean Save(String sBuff) {
        //String sBuff = jsonstring.toString();
        JSONObject obj;
        //System.out.println(sBuff);
        try {
            obj = new JSONObject(sBuff);
        } catch (JSONException et) {
            System.out.println("JSON Parse error in " + sBuff);
            return false;
        }
        String DeviceName = obj.getJSONObject("SensorData").getString("device");
        UUID dUuid = java.util.UUID.fromString(DeviceName);

        String InsertionTime = obj.getJSONObject("SensorData").getString("insertion_time");
        Date dd = null;
        try {
            dd = new Date(InsertionTime);
        } catch (IllegalArgumentException et) {
            //Must not be Java format, try python
            String pDateFormat = "yyyy-MM-dd HH:mm:ss";
            int dot = InsertionTime.lastIndexOf(".");
            String trimmed = InsertionTime.substring(0, dot);
            SimpleDateFormat formatter = new SimpleDateFormat(pDateFormat);
            try {
                dd = formatter.parse(InsertionTime);
            } catch (Exception etp) {
                System.out.println("Can't parse Python Date " + etp);
                return false;
            }
        }
        //System.out.println("Device Name " + DeviceName);
        //System.out.println("Insertion Time " + InsertionTime);
        JSONObject jsonMeta = null;
        try {
            jsonMeta = obj.getJSONObject("meta");
        } catch (Exception et) {
            jsonMeta = null;
        }
        Map<String, String> Meta = null;
        if (jsonMeta != null) {
            Meta = new HashMap<String, String>();
            String[] metaNames = JSONObject.getNames(jsonMeta);
            //System.out.println("" + metaNames);
            for (int j = 0; j < metaNames.length; j++) {
                String Name = metaNames[j];
                String Value = jsonMeta.getString(Name);

                Meta.put(Name, Value);
                //System.out.println(Name + ":" + Value);

            }
        }
        JSONArray arr = obj.getJSONArray("sensors");
        Map<String, UDTValue> mp = new HashMap<String, UDTValue>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject objA = arr.getJSONObject(i);
            String[] names = JSONObject.getNames(objA);
            //System.out.println("Sensor: ");

            UDTValue sr;
            sr = SensorReadingType.newValue();
            String Name = "";
            for (int j = 0; j < names.length; j++) {
                //System.out.print("Name " + names[j] + " ");
                //System.out.println(objA.getString(names[j]));

                int command;
                try {
                    command = (Integer) CommandsMap.get(names[j]);
                } catch (Exception et) {
                    error("Bad Operator " + names[j]);
                    return false;
                }
                switch (command) {
                    case 1:
                        try {
                            addFloat(objA.getString(names[j]), sr);
                        } catch (JSONException et) {
                            addFloat(objA.getDouble(names[j]), sr);
                        }
                        break;
                    case 2:
                        addInt(objA.getString(names[j]), sr);
                        break;
                    case 3:
                        addString(objA.getString(names[j]), sr);
                        break;
                    case 4:
                        addAccuracy(objA.getString(names[j]), sr);
                        break;
                    case 5:
                        Name = objA.getString(names[j]);
                        break;
                    default:
                        error("Bad Operator");
                }

            }
            mp.put(Name, sr);

        }
        Statement statement = QueryBuilder.insertInto("sensorsync", "Sensors")
                .value("name", dUuid)
                .value("insertion_time", dd)
                .value("metadata", Meta)
                .value("reading", mp);

        //System.out.println("Insetion Statement "+dUuid+" : "+dd );
        getSession().execute(statement);
        DataCount++;
        if (DataCount == 100) {
            System.out.println(DataCount);
            DataCount = 0;
        }
        System.out.print(".");
        return true;
    }

    private boolean addFloat(String Value, UDTValue sr) {

        String sFloat = Value;
        float value;
        try {
            value = Float.parseFloat(sFloat);
        } catch (NumberFormatException et) {
            return false;
        }

        sr.setFloat("fValue", value);

        return true;

    }

    private boolean addFloat(double Value, UDTValue sr) {

        sr.setFloat("fValue", (float) Value);

        return true;

    }

    private boolean addAccuracy(String Value, UDTValue sr) {

        String sFloat = Value;
        float value;
        try {
            value = Float.parseFloat(sFloat);
        } catch (NumberFormatException et) {
            return false;
        }

        sr.setFloat("Accuracy", value);

        return true;

    }

    private boolean addInt(String Value, UDTValue sr) {
        int value;
        try {
            value = Integer.parseInt(Value);

        } catch (NumberFormatException et) {
            return false;
        }

        sr.setInt("iValue", value);

        return true;

    }

    private boolean addString(String Value, UDTValue sr) {

        sr.setString("sValue", Value);

        return true;

    }

    private void error(String mess) {

        System.out.println("You have a na error in your input");
        System.out.println("" + mess + "");
        System.out.close();
        return;
    }

}
