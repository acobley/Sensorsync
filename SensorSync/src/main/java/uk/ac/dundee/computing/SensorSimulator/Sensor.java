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

    public void Sensor() {

        Random randomno = new Random();
        type = randomno.nextInt(2) + 1;
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
            default:
                break;
        }

    }

}
