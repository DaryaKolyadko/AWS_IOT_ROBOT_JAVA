package com.kolyadko.iot.entity;

import com.kolyadko.iot.enumeration.DirectionType;
import com.kolyadko.iot.enumeration.SensorType;
import com.kolyadko.iot.enumeration.StateType;
import org.json.simple.JSONObject;

/**
 * Created by DaryaKolyadko on 08.07.2016.
 */
public class RobotState {
    private StateType robotState;
    private SensorType sensorState;
    private DirectionType direction;
    private Position currentPosition;

    public RobotState(JSONObject reported) {
        robotState = StateType.valueOf(reported.get("robotState").toString());
        sensorState = SensorType.valueOf(reported.get("sensorState").toString());
        direction = DirectionType.valueOf(reported.get("direction").toString());
        currentPosition  = new Position();
        currentPosition.setI(Integer.parseInt(reported.get("i").toString()));
        currentPosition.setJ(Integer.parseInt(reported.get("j").toString()));
    }

    @Override
    public String toString() {
        return "Current state{" +
                "\n\trobotState=" + robotState +
                ",\n\tsensorState=" + sensorState +
                ",\n\tdirection=" + direction +
                ",\n\ti=" + currentPosition.getI() +
                ",\n\tj=" + currentPosition.getJ() +
                "}\n";
    }
}