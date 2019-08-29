/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync;


import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.SimpleStatement;
import com.datastax.oss.driver.api.core.cql.Statement;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.datastax.oss.driver.api.querybuilder.*;

import static com.datastax.oss.driver.api.querybuilder.QueryBuilder.*;
import com.datastax.oss.driver.api.querybuilder.select.Select;
import com.datastax.oss.driver.api.querybuilder.insert.Insert;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import uk.ac.dundee.computing.aec.sensorsync.lib.Convertors;

/**
 *
 * @author andycobley
 */
public class SensorSaver {

   
    CqlSession  session = null;
    UserDefinedType SensorReadingType = null;
    int DataCount = 0;
    private final HashMap CommandsMap = new HashMap();
PreparedStatement PreparedInsert =null;
    public SensorSaver(CqlSession session) {
        this.session = session;
        SensorReadingType = session.getMetadata().getKeyspace("sensorsync").flatMap(sensorsync -> sensorsync.getUserDefinedType("SensorReading")).orElseThrow(() -> new IllegalArgumentException("Missing UDT definition"));;
        CommandsMap.put("fValue", 1);
        CommandsMap.put("iValue", 2);
        CommandsMap.put("sValue", 3);
        CommandsMap.put("Accuracy", 4);
        CommandsMap.put("name", 5);
        PreparedInsert = getSession().prepare("insert into sensorsync.Sensors (name,insertion_time,metadata,reading) values (?,?,?,?)");
 
    }

    public CqlSession getSession() {
        return this.session;
    }

    public boolean Save(String sBuff) {
        //String sBuff = jsonstring.toString();
        JSONObject obj;
        System.out.println("Saving to Cassandra");
        System.out.println(sBuff);
        System.out.flush();
        //System.out.println(sBuff);
        try {
            obj = new JSONObject(sBuff);
        } catch (JSONException et) {
            System.out.println("JSON Parse error  "+et);
            return false;
        }
        String DeviceName = obj.getJSONObject("SensorData").getString("device");
        //UUID dUuid = java.util.UUID.fromString(DeviceName);
        
        String InsertionTime = obj.getJSONObject("SensorData").getString("insertion_time");
        Date dd = null;
        try {
            dd=Convertors.StringToDate(InsertionTime);
            
        } catch (IllegalArgumentException | ParseException et) {
            //Must not be Java format, try python
            String pDateFormat = "yyyy-MM-dd HH:mm:ss";
            //int dot = InsertionTime.lastIndexOf(".");
            //String trimmed = InsertionTime.substring(0, dot);
            SimpleDateFormat formatter = new SimpleDateFormat(pDateFormat);
            try {
                dd = formatter.parse(InsertionTime);
                //System.out.println("Parsed as Python time");
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
        /*  Not Flowerpower
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
        */
        if (jsonMeta != null) {
            Meta = new HashMap<String, String>();
            String[] metaNames = JSONObject.getNames(jsonMeta);
            //System.out.println("" + metaNames);
            for (int j = 0; j < metaNames.length; j++) {
                String Name = metaNames[j];
                //System.out.println("name "+Name);
                String Value =null;
                try{
                   Value = jsonMeta.getString(Name);
                }catch(Exception et){
                    try {
                       double dValue =jsonMeta.getDouble(Name);
                       Value= Double.toString(dValue);
                    }catch (Exception notDoubleET){
                        try{
                        boolean bValue=jsonMeta.getBoolean(Name);
                        Value=Boolean.toString(bValue);
                        }catch(Exception notBooleanET){
                            Value="Not Known";
                        }
                    }
                    
                }

                Meta.put(Name, Value);
                //System.out.println(Name + ":" + Value);

            }
        }
        
        JSONArray arr = obj.getJSONArray("sensors");
        Map<String, UdtValue> mp = new HashMap<String, UdtValue>();
        for (int i = 0; i < arr.length(); i++) {
            JSONObject objA = arr.getJSONObject(i);
            String[] names = JSONObject.getNames(objA);
            //System.out.println("Sensor: ");

            UdtValue sr;
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
        
        BoundStatement bound = PreparedInsert.bind(DeviceName, dd.toInstant(),Meta,mp);

        /*Insert insert = insertInto("sensorsync", "Sensors")
                .value("name", literal(dUuid))
                .value("insertion_time", literal(dd.toInstant()))
                .value("metadata", literal(Meta))
                .value("reading", literal(mp));
        SimpleStatement statement = insert.build();
        Statement statement = QueryBuilder.insertInto("sensorsync", "Sensors")
                .value("name", dUuid)
                .value("insertion_time", dd)
                .value("metadata", Meta)
                .value("reading", mp);
*/
        
        //System.out.println("Insetion Statement "+dUuid+" : "+dd );
        getSession().execute(bound);
        DataCount++;
        if (DataCount == 100) {
            System.out.println(DataCount);
            DataCount = 0;
        }
        System.out.print(".");
        return true;
    }

    private boolean addFloat(String Value, UdtValue sr) {

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

    private boolean addFloat(double Value, UdtValue sr) {

        sr.setFloat("fValue", (float) Value);

        return true;

    }

    private boolean addAccuracy(String Value, UdtValue sr) {

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

    private boolean addInt(String Value, UdtValue sr) {
        int value;
        try {
            value = Integer.parseInt(Value);

        } catch (NumberFormatException et) {
            return false;
        }

        sr.setInt("iValue", value);

        return true;

    }

    private boolean addString(String Value, UdtValue sr) {

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
