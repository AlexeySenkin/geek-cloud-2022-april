package com.senkinay.cloud.controllers;

import com.senkinay.cloud.CopyFileToServer;
import com.senkinay.cloud.network.Net;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    private Net net;

    @FXML
    public TextField inputClient;
    @FXML
    public Button copyToServer;

    @FXML
    public ListView<String> viewServer;

    @FXML
    public ListView<String> viewClient;

    @FXML
    public TextField inputServer;



    private void readListFiles() {
        try {
            this.viewServer.getItems().clear();
            Long filesCount = this.net.readLong();
            for (int i = 0; i < filesCount; i++) {
                String fileName = this.net.readUtf();
                this.viewServer.getItems().addAll(fileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        getFilesFromClientDir();

    }

    public void getFilesFromClientDir() {
        File dir = new File("files-client/client-1");
        String[] files = dir.list();
        for (int i = 0; i < files.length; i++) {
            this.viewClient.getItems().addAll(files[i]);
        }
    }

    public File getFileToCopyOnServer() {
        return new File("files-client/client-1",this.viewClient.getSelectionModel().getSelectedItem());
    }

    private void read() {
        try {
            while (true) {
                String command = this.net.readUtf();
                if (command.equals("#list#")) {
                    //Platform.runLater(this::readListFiles);
                    readListFiles();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void copyFileOnServer(File file) {
        try {
            System.err.println(file);
            new Thread(new CopyFileToServer(this.net,file)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    protected void onCopyToServerClick() {
        copyFileOnServer(getFileToCopyOnServer());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.net = new Net("localhost", 8189);
            Thread readThread = new Thread(this :: read);
            readThread.setDaemon(true);
            readThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}