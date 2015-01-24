/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.aec.sensorsync.lib;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Administrator
 */
public class RenderJSON {

    protected JSONObject getJSON(Object thing) {
        // TODO Auto-generated method stub
        Object temp = thing;
        Class c = temp.getClass();
        String className = c.getName();
        if (className.compareTo("java.util.LinkedList") == 0) { //Deal with a linked list
            List Data = (List) thing;
            Iterator iterator;
            JSONObject JSONObj = new JSONObject();
            JSONArray Parts = new JSONArray();
            iterator = Data.iterator();
            while (iterator.hasNext()) {
                Object Value = iterator.next();
                JSONObject obj = ProcessObject(Value);
                try {
                    Parts.put(obj);
                } catch (Exception JSONet) {
                    System.out.println("JSON Fault" + JSONet);
                }
            }
            try {
                JSONObj.put("Data", Parts);
            } catch (Exception JSONet) {
                System.out.println("JSON Fault" + JSONet);
            }
            if (JSONObj != null) {
                return (JSONObj);
            }

        } else {
            Object Data = thing;
            JSONObject obj = ProcessObject(Data);
            if (obj != null) {
                return (obj);
            }
        }
        return null;
    }


    private JSONObject ProcessObject(Object Value) {
        JSONObject Record = new JSONObject();

        try {
            Class c = Value.getClass();
            Method methlist[] = c.getDeclaredMethods();
            for (int i = 0; i < methlist.length; i++) {
                Method m = methlist[i];
                //System.out.println(m.toString());
                String mName = m.getName();

                if (mName.startsWith("get") == true) {
                    String Name = mName.replaceFirst("get", "");
                	 //Class pvec[] = m.getParameterTypes(); //Get the Parameter types
                    //for (int j = 0; j < pvec.length; j++)
                    //   System.out.println("param #" + j + " " + pvec[j]);
                    //System.out.println(mName+" return type = " +  m.getReturnType());
                    Class partypes[] = new Class[0];
                    Method meth = c.getMethod(mName, partypes);

                    Object rt = meth.invoke(Value);
                    if (rt != null) {
                        System.out.println(Name + " Return " + rt);
                        try {
                            Record.put(Name, rt);
                        } catch (Exception JSONet) {
                            System.out.println("JSON Fault" + JSONet);
                            return null;
                        }

                    }
                }
            }

        } catch (Throwable e) {
            System.err.println(e);
        }
        return Record;
    }
}
