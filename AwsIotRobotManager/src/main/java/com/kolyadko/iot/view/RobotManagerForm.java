package com.kolyadko.iot.view;

import com.amazonaws.services.iot.client.AWSIotDevice;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.kolyadko.iot.callback.RobotCallback;
import com.kolyadko.iot.entity.RobotState;
import com.kolyadko.iot.enumeration.Protocol;
import com.kolyadko.iot.util.IoTConfig;
import com.kolyadko.iot.util.SampleUtil;
import com.kolyadko.iot.util.SslUtil;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.net.ssl.SSLSocketFactory;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * Created by DaryaKolyadko on 07.07.2016.
 */
public class RobotManagerForm extends JFrame {
    private JButton buttonGo;
    private JButton buttonStop;
    private JButton buttonTurnLeft;
    private JButton buttonTurnRight;
    private JTextPane logTextPane;
    private JPanel mainPanel;
    private MqttClient client;
    private AWSIotDevice device;
    private String topic;

    public RobotManagerForm(String title) throws HeadlessException {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setContentPane(mainPanel);
        DefaultCaret caret = (DefaultCaret) logTextPane.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        setSize(1200, 900);
        buttonGo.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                publish(Protocol.GO.name());
            }
        });

        buttonStop.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                publish(Protocol.STOP.name());
            }
        });

        buttonTurnLeft.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                publish(Protocol.LEFT.name());
            }
        });

        buttonTurnRight.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                publish(Protocol.RIGHT.name());
            }
        });
    }

    public void startInteraction(final String configFile, final String topic, final String qosLevel) {
        final JDialog dlgProgress = new JDialog(this, "Please wait...", true);
        dlgProgress.setLocationRelativeTo(null);
        JLabel lblStatus = new JLabel("Working...");
        JProgressBar pbProgress = new JProgressBar(0, 100);
        pbProgress.setIndeterminate(true);
        dlgProgress.add(BorderLayout.NORTH, lblStatus);
        dlgProgress.add(BorderLayout.CENTER, pbProgress);
        dlgProgress.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dlgProgress.pack();

        SwingWorker<Void, Void> swingWorker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                establishConnectionToThing(configFile);
                establishConnectionToTopic(configFile, topic, qosLevel);
                return null;
            }

            @Override
            protected void done() {
                dlgProgress.dispose();
            }
        };

        swingWorker.execute();
        dlgProgress.setVisible(true);
        setLocationRelativeTo(null);
        getRobotState();
        setVisible(true);
    }

    private void establishConnectionToTopic(String configFile, String topic, String qosLevel) {
        try {
            IoTConfig config = new IoTConfig(configFile);
            this.topic = topic;
            SSLSocketFactory sslSocketFactory = SslUtil.getSocketFactory(
                    config.get(IoTConfig.ConfigFields.AWS_IOT_ROOT_CA_FILENAME),
                    config.get(IoTConfig.ConfigFields.AWS_IOT_CERTIFICATE_FILENAME),
                    config.get(IoTConfig.ConfigFields.AWS_IOT_PRIVATE_KEY_FILENAME));
            MqttConnectOptions options = new MqttConnectOptions();
            options.setSocketFactory(sslSocketFactory);
            options.setCleanSession(true);
            String serverUrl = "ssl://" + config.get(IoTConfig.ConfigFields.AWS_IOT_MQTT_HOST) + ":" + config.get(IoTConfig.ConfigFields.AWS_IOT_MQTT_PORT);
            String clientId = config.get(IoTConfig.ConfigFields.AWS_IOT_MQTT_CLIENT_ID);
            client = new MqttClient(serverUrl, clientId);
            client.setCallback(new RobotCallback(this));
            client.connect(options);
            client.subscribe(topic, Integer.parseInt(qosLevel));
        } catch (Exception e) {
            error(e.getMessage());
        }
    }

    private void establishConnectionToThing(String configFile) {
        try {
            IoTConfig config = new IoTConfig(configFile);
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null);
            SampleUtil.KeyStorePasswordPair pair = SampleUtil.getKeyStorePasswordPair(config.get(IoTConfig.ConfigFields.
                    AWS_IOT_CERTIFICATE_FILENAME), config.get(IoTConfig.ConfigFields.AWS_IOT_PRIVATE_KEY_FILENAME));
            AWSIotMqttClient client = new AWSIotMqttClient(config.get(IoTConfig.ConfigFields.AWS_IOT_MQTT_HOST),
                    config.get(IoTConfig.ConfigFields.AWS_IOT_MQTT_CLIENT_ID_THING_CONNECTION),
                    pair.keyStore, pair.keyPassword);
            device = new AWSIotDevice(config.get(IoTConfig.ConfigFields.AWS_IOT_MY_THING_NAME));
            client.attach(device);
            client.connect();
//            device.delete();
//            String init = "{\"state\":{\"reported\":{\"robotState\": \"STOP\", \"sensorState\": \"NONE\"}}}";
//            device.update(init);
        } catch (IOException | CertificateException | NoSuchAlgorithmException | AWSIotException |
                KeyStoreException e) {
            error(e.getMessage());
        }
    }

    private void getRobotState() {
        try {
            String currentState = device.get();
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(currentState);
            JSONObject state = (JSONObject) obj.get("state");
            JSONObject reported = (JSONObject) state.get("reported");
            RobotState robotState = new RobotState(reported);
            addLog(robotState.toString());
        } catch (AWSIotException | ParseException e) {
            error(e.getMessage());
        }
    }

    private void publish(String message) {
        try {
            client.publish(topic, new MqttMessage(message.getBytes()));
        } catch (MqttException e) {
            error(e.getMessage());
        }
    }

    public void addLog(String log) {
        String text = logTextPane.getText();
        text += log;
        logTextPane.setText(text);
    }

    public static void error(String message) {
        JOptionPane.showMessageDialog(null, message, "Error occurred", JOptionPane.ERROR_MESSAGE);
        System.exit(-1);
    }
}