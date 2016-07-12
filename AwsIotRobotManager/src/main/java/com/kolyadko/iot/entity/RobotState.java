package com.kolyadko.iot.entity;

import com.kolyadko.iot.enumeration.SensorType;
import com.kolyadko.iot.enumeration.StateType;
import org.json.simple.JSONObject;

/**
 * Created by DaryaKolyadko on 08.07.2016.
 */
public class RobotState {
    private StateType robotState;
    private SensorType sensorState;

    public RobotState(JSONObject reported) {
        robotState = StateType.valueOf(reported.get("robotState").toString());
        sensorState = SensorType.valueOf(reported.get("sensorState").toString());
    }

    @Override
    public String toString() {
        return "Current state{" +
                "robotState=" + robotState +
                ", sensorState=" + sensorState +
                "}\n";
    }
}