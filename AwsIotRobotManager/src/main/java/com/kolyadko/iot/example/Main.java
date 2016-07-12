package com.kolyadko.iot.example;

import com.kolyadko.iot.view.RobotManagerForm;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        if (args.length < 3) {
            showHelp();
        }

        RobotManagerForm frame = new RobotManagerForm("Robot Manager");
        frame.startInteraction(args[0], args[1], args[2]);
    }

    private static void showHelp() {
        JOptionPane.showMessageDialog(null,
                "Usage: java -jar AwsIotRobotManager.jar <config-file> <topic-name> <QOS level>",
                "Runtime error",
                JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }
}