package com.kolyadko.iot.example;

import com.kolyadko.iot.view.RobotManagerForm;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        if (args.length < 1) {
            showHelp();
        }

        RobotManagerForm frame = new RobotManagerForm("Robot Manager");
        frame.startInteraction(args[0]);
    }

    private static void showHelp() {
        JOptionPane.showMessageDialog(null,
                "Usage: java -jar AwsIotRobotManager.jar <config-file>",
                "Runtime error",
                JOptionPane.ERROR_MESSAGE);
        System.exit(0);
    }
}