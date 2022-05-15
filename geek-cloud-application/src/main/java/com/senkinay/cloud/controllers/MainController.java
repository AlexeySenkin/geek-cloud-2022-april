package com.senkinay.cloud.controllers;

import com.senkinay.cloud.model.AbstractMessage;
import com.senkinay.cloud.model.FileDownloadMessage;
import com.senkinay.cloud.model.FileUploadMessage;
import com.senkinay.cloud.model.ListMessage;
import com.senkinay.cloud.network.Net;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    public Label captionClient;
    private Net net;

    @FXML
    public Label captionServer;

    @FXML
    public ListView<String> viewServer;

    @FXML
    public ListView<String> viewClient;

    @FXML
    public Button upload;

    @FXML
    public Button download;

    private Path clientDir;

    private List<Path> clientFiles;
    private List<Path> currentClientDir;

    public MainController() {
    }


    public class ClientFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {
            currentClientDir.add(path);
            return FileVisitResult.CONTINUE;
        }
    }



    private void read() {
        try {
            while (true) {
                AbstractMessage message = net.read();
                if (message instanceof ListMessage lm) {
                    Platform.runLater(()-> {
                        this.viewServer.getItems().clear();
                        this.viewServer.getItems().addAll(lm.getFiles());
                    });
                }
                if (message instanceof FileDownloadMessage fdm) {
                    Files.write(clientDir.resolve(fdm.getName()),fdm.getBytes());
                    Platform.runLater(()->{
                        viewClient.getItems().clear();
                        try {
                            viewClient.getItems().addAll(getClientFiles());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }



    private void uploadClientFiles() {
        while (true) {
            try {

                Thread.sleep(1000);

                if (!clientFiles.isEmpty()) {
                    clientFiles.clear();
                    clientFiles.addAll(currentClientDir);
                    currentClientDir.clear();
                }

                Files.walkFileTree(clientDir,new ClientFileVisitor());

                if (clientFiles.isEmpty()) {
                    clientFiles.addAll(currentClientDir);
                }


                if (!currentClientDir.equals(clientFiles)) {
                    for (Path path : currentClientDir) {
                        net.write(new FileUploadMessage(path));
                    }
                }



            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    private List<String> getClientFiles() throws IOException {
        return Files.list(clientDir)
                .map(Path::getFileName)
                .map(Path::toString)
                .toList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            clientDir = Path.of("files-client/client-1");

            clientFiles = new ArrayList<>();
            currentClientDir= new ArrayList<>();

            viewClient.getItems().clear();
            viewClient.getItems().addAll(getClientFiles());
            this.net = new Net("localhost", 8189);

            Thread.sleep(300);
            Thread readThread = new Thread(this :: read);
            readThread.setDaemon(true);
            readThread.start();

            Thread.sleep(300);
            Thread updateThread = new Thread(this :: uploadClientFiles);
            updateThread.setDaemon(true);
            updateThread.start();

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    @FXML
    public void upload(ActionEvent actionEvent) throws IOException {
        String fileName = viewClient.getSelectionModel().getSelectedItem();
        net.write(new FileUploadMessage(clientDir.resolve(fileName)));
        viewClient.getItems().clear();
        viewClient.getItems().addAll(getClientFiles());
    }

    @FXML
    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = viewServer.getSelectionModel().getSelectedItem();
        net.write(new FileDownloadMessage(fileName, null));
    }
}