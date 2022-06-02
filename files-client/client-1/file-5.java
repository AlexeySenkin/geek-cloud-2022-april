package com.senkinay.cloud.controllers;

import com.senkinay.cloud.network.Net;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private Net net;

    public ListView<String> view;

    public TextField input;

    private void readListFiles() {
        try {
            view.getItems().clear();
            Long filesCount = net.readLong();
            for (int i = 0; i < filesCount; i++) {
                String fileName = net.readUtf();
                view.getItems().addAll(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void read() {
        try {
            while (true) {
                String command = net.readUtf();
                if (command.equals("#list#")) {
                    Platform.runLater(this::readListFiles);
                    //readListFiles();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private Label welcomeText;

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Client application");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            net = new Net("localhost", 8189);
            Thread readThread = new Thread(this :: read);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}