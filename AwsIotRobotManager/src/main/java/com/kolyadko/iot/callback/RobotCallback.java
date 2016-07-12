package com.kolyadko.iot.callback;

import com.kolyadko.iot.view.RobotManagerForm;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class RobotCallback implements MqttCallback {
    private RobotManagerForm form;

    public RobotCallback(RobotManagerForm form) {
        this.form = form;
    }

    @Override
    public void connectionLost(Throwable cause) {
        form.addLog("\nConnection Lost: " + cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        form.addLog(String.format("{%s}: message {%s}\n", topic, new String(message.getPayload())));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        form.addLog("===Successfully  sent===\n");
    }
}
