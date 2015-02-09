/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.dundee.computing.SensorSimulator;

import java.util.Random;

/**
 *
 * @author Administrator
 */
public class Sensor {

    int type = 0;
    boolean addAccuracy = false;
    int iMin = 0;
    int iMax = 100;
    float fMin = (float) 0.0;
    float fMax = (float) 100.0;
    Random randomno = new Random();
    String name;
    String sValues[][]={{"High","Low"},{"Ok","Warning","Danger"},
        {"Slow","OK","Fast"},{"Slip","Spill","Misaligned"}
    };
    int sValueType;
    public Sensor() {
        //See http://www.tutorialspoint.com/java/util/random_nextboolean.htm

        type = randomno.nextInt(3) + 1;
        addAccuracy = randomno.nextBoolean();
        switch (type) {
            case 1:
                fMin = (float) randomno.nextInt(50);
                fMax = (float) randomno.nextInt(1000) + fMin;
                break;
            case 2:
                iMin = randomno.nextInt(50);
                iMax = randomno.nextInt(1000) + iMin;
                break;
            case 3:
                int iL=sValues.length;
                sValueType=randomno.nextInt(iL);
                break;
            default:
                break;
        }
    }

    
    public void setName(String Name){
        this.name=Name;
    }
    public int getType() {
        return type;
    }

    public Object getValue() {
        switch (type) {
            case 1:
                float fvalue = fMin + (float) randomno.nextInt((int) fMax);
                Float fValue = new Float(fvalue);
                return fValue;

            case 2:
                int ivalue = iMin + randomno.nextInt(iMax);
                Integer iValue = new Integer(ivalue);
                return iValue;
            case 3:
                int iL= sValues[sValueType].length;
                int riL=randomno.nextInt(iL);
                return sValues[sValueType][riL];
            default:
                break;
        }
        return null;
    }
    
    public String getName(){
        return name;
    }

}
