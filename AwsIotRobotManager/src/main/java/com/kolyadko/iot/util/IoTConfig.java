package com.kolyadko.iot.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Properties helper class with additional cleanup/trim
 */
public class IoTConfig extends Properties {

    public enum ConfigFields {
        AWS_IOT_MQTT_HOST,
        AWS_IOT_MQTT_PORT,
        AWS_IOT_MQTT_CLIENT_ID,
        AWS_IOT_MQTT_CLIENT_ID_THING_CONNECTION,
        AWS_IOT_MY_THING_NAME,
        AWS_IOT_ROOT_CA_FILENAME,
        AWS_IOT_CERTIFICATE_FILENAME,
        AWS_IOT_PRIVATE_KEY_FILENAME
    }


    public IoTConfig(final String configFileName) throws IOException {
        this.load(new FileInputStream(configFileName));
    }

    public String get(final ConfigFields field, String defaultValue) {
        String value = getProperty(field.name(), defaultValue);
        if (value != null) {
            value = value.replace("\"", "");
            value = value.trim();
        }
        return value;
    }

    public String get(final ConfigFields field) {
        String value = getProperty(field.name());
        if (value != null) {
            value = value.replace("\"", "");
            value = value.trim();
        }
        return value;
    }
}
