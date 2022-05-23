package com.senkinay.cloud.controllers;

import com.senkinay.cloud.model.*;
import com.senkinay.cloud.network.Net;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    public TreeView clientTreeDir;

    @FXML
    public ListView<String> viewServer;

    @FXML
    public TableView tableViewClient;

    @FXML
    public Button upload;

    @FXML
    public Button download;



    private Path clientDir;

    private Net net;

    private List<Path> clientFiles;

    private List<Path> currentClientDir;

    private List<FileAttribute> fileAttributes;



    public MainController() {
    }

    public void closeClientApplication(ActionEvent actionEvent) throws IOException {
        Runtime.getRuntime().exec("explorer.exe /select, " + clientDir.toString());

        //Platform.exit();
        //System.exit(0);
    }


    public class ClientFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {

            if (fileAttributes.isEmpty()) {
                fileAttributes.add(new FileAttribute("client_root",
                        path.getParent().toString(),
                        false,
                        true,
                        false,
                        path.getParent().toString(),
                        null,
                        0));
                currentClientDir.add(path.getParent());
            }
            fileAttributes.add(new FileAttribute("client",
                    path.toString(),
                    attributes.isRegularFile(),
                    attributes.isDirectory(),
                    attributes.isSymbolicLink(),
                    path.getFileName().toString(),
                    attributes.lastModifiedTime(),
                    attributes.size()));

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

                        //viewClient.getItems().clear();
                        //try {
                        //    viewClient.getItems().addAll(getClientFiles());
                        //} catch (IOException e) {
                        //    e.printStackTrace();
                       // }

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

                fileAttributes.clear();

                if (!clientFiles.isEmpty()) {
                    clientFiles.clear();
                    clientFiles.addAll(currentClientDir);
                    currentClientDir.clear();

                }

                Files.walkFileTree(clientDir,new ClientFileVisitor());



                if (clientFiles.isEmpty()) {
                    clientFiles.addAll(currentClientDir);
                    updateTreeDir(clientDir);
                }


                if (!currentClientDir.equals(clientFiles)) {

                    updateTreeDir(clientDir);

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

    private void updateTreeDir(Path pathDir) {

        if (pathDir.equals(clientDir)) {
            List<TreeItem<FileAttribute>> fileAttributeTreeItem = new ArrayList<>();

            for (FileAttribute fileAttribute : fileAttributes) {
                fileAttributeTreeItem.add(new TreeItem<FileAttribute>(fileAttribute));
            }

            fileAttributeTreeItem.get(0).setExpanded(true);

            for (int i = 1; i < fileAttributes.size(); i++) {
                if (fileAttributeTreeItem.get(i).getValue().getDir()) {
                    //TODO допилить дерево файлов

                }
                fileAttributeTreeItem.get(0).getChildren().addAll(fileAttributeTreeItem.get(i));
            }

            Platform.runLater(()->{
                clientTreeDir.setRoot(fileAttributeTreeItem.get(0));
                clientTreeDir.setShowRoot(true);



                updateTableView(clientDir,fileAttributes);
            });

        } else {

        }
    }

    private void updateTableView(Path pathDir, List<FileAttribute> fileAttributes) {
        if (pathDir.equals(clientDir)) {
            TableColumn<FileAttribute, String> nameCol =
                    new TableColumn<FileAttribute,String>("Имя");
            TableColumn<FileAttribute, FileTime> fileModificationDateCol =
                    new TableColumn<FileAttribute, FileTime>("Дата изменения");
            TableColumn<FileAttribute, String> typeCol =
                    new TableColumn<FileAttribute, String>("Тип");
            TableColumn<FileAttribute, Long> sizeCol =
                    new TableColumn<FileAttribute,Long>("Размер");

            nameCol.setCellValueFactory(new PropertyValueFactory<>("type"));
            fileModificationDateCol.setCellValueFactory(new PropertyValueFactory<>("fileModificationDate"));

            typeCol.setCellValueFactory(new PropertyValueFactory<>("fileType"));
            sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));

            nameCol.setSortType(TableColumn.SortType.DESCENDING);

            Platform.runLater(()->{
                tableViewClient.getColumns().clear();
                tableViewClient.setItems(FXCollections.observableArrayList(fileAttributes.subList(1,fileAttributes.size())));
                tableViewClient.getColumns().addAll(nameCol,fileModificationDateCol,typeCol,sizeCol);
            });

        } else {

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            clientDir = Path.of("files-client/senkinay");

            clientFiles = new ArrayList<>();
            currentClientDir = new ArrayList<>();
            fileAttributes = new ArrayList<>();

            //viewClient.getItems().clear();
            //viewClient.getItems().addAll(getClientFiles());

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
        /*
        String fileName = viewClient.getSelectionModel().getSelectedItem();
        net.write(new FileUploadMessage(clientDir.resolve(fileName)));
        viewClient.getItems().clear();
        viewClient.getItems().addAll(getClientFiles());

         */
    }

    @FXML
    public void download(ActionEvent actionEvent) throws IOException {
        String fileName = viewServer.getSelectionModel().getSelectedItem();
        net.write(new FileDownloadMessage(fileName, null));
    }
}