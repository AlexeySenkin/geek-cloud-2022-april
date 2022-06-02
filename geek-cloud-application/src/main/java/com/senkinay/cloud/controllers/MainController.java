package com.senkinay.cloud.controllers;

import com.senkinay.cloud.model.*;
import com.senkinay.cloud.network.Net;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.*;

public class MainController implements Initializable {

    @FXML
    public TreeView clientTreeDir;

    @FXML
    public TableView tableViewClient;

    @FXML
    public Button uploadClient;


    @FXML
    public Button downloadServer;

    @FXML
    public Button removeServer;

    @FXML
    public TableView tableViewServer;

    @FXML
    public TreeView serverTreeDir;

    @FXML
    public Button openDir;


    private Path clientDir;

    private Net net;

    private List<FileAttribute> fileAttributes;

    private List<FileAttribute> fileAttributesCurrent;

    private List<FileAttribute> serverFileAttributes;


    public MainController() {
    }

    public void closeClientApplication(ActionEvent actionEvent) {
        Platform.exit();
        System.exit(0);
    }

    public void clientTreeDirOnClicked(MouseEvent mouseEvent) {
        Platform.runLater(()->{
            MultipleSelectionModel<TreeItem<FileAttribute>> selectionModel = clientTreeDir.getSelectionModel();
            if (fileAttributes.get(0).equals(selectionModel.getSelectedItem().getValue())) {
                updateTableView("client",fileAttributes);
            } else {
                List<FileAttribute> selectFileAttributes = new ArrayList<>();
                selectFileAttributes.add(fileAttributes.get(0));
                selectFileAttributes.add(selectionModel.getSelectedItem().getValue());
                updateTableView("client",selectFileAttributes);
            }
        });
    }

    public void serverTreeDirOnClicked(MouseEvent mouseEvent) {
        Platform.runLater(()->{
            MultipleSelectionModel<TreeItem<FileAttribute>> selectionModel = serverTreeDir.getSelectionModel();
            if (serverFileAttributes.get(0).equals(selectionModel.getSelectedItem().getValue())) {
                updateTableView("server",serverFileAttributes);
            } else {
                List<FileAttribute> selectFileAttributes = new ArrayList<>();
                selectFileAttributes.add(serverFileAttributes.get(0));
                selectFileAttributes.add(selectionModel.getSelectedItem().getValue());
                updateTableView("server",selectFileAttributes);
            }
        });
    }

    public void openClientDir(ActionEvent actionEvent) throws IOException {
        Runtime.getRuntime().exec("explorer.exe /select, " + clientDir.toString());
    }


    public class ClientFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile(Path path, BasicFileAttributes attributes) {

            if (fileAttributesCurrent.isEmpty()) {
                fileAttributesCurrent.add(new FileAttribute("client_root",
                        path.getParent().toString(),
                        false,
                        true,
                        false,
                        path.getParent().toString(),
                        new Date(attributes.lastModifiedTime().toMillis()),
                        0));
            }
            fileAttributesCurrent.add(new FileAttribute("client",
                    path.toString(),
                    attributes.isRegularFile(),
                    attributes.isDirectory(),
                    attributes.isSymbolicLink(),
                    path.getFileName().toString(),
                    new Date(attributes.lastModifiedTime().toMillis()),
                    attributes.size()));
            return FileVisitResult.CONTINUE;
        }
    }



    private void read() {
        try {
            while (true) {
                AbstractMessage message = net.read();
                if (message instanceof ListMessage lm) {
                    Platform.runLater(()-> {
                        serverFileAttributes.clear();
                        serverFileAttributes.addAll(lm.getFiles());
                        updateTreeDir("server");
                    });
                }
                if (message instanceof FileDownloadMessage fdm) {
                    Files.write(clientDir.resolve(fdm.getName()),fdm.getBytes());
                    Platform.runLater(()-> updateTreeDir("client"));
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private void uploadClientFiles() {
        while (true) {
            try {
                fileAttributesCurrent.clear();
                Files.walkFileTree(clientDir,new ClientFileVisitor());
                if (!fileAttributesCurrent.equals(fileAttributes)) {
                    fileAttributes.clear();
                    fileAttributes.addAll(fileAttributesCurrent);
                    updateTreeDir("client");
                }
                Thread.sleep(1500);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateTreeDir(String dir) {
        List<TreeItem<FileAttribute>> fileAttributeTreeItem = new ArrayList<>();
        if (dir.equals("client")) {
            for (FileAttribute fileAttribute : fileAttributes) {
                fileAttributeTreeItem.add(new TreeItem<>(fileAttribute));
            }
            fileAttributeTreeItem.get(0).setExpanded(true);
            for (int i = 1; i < fileAttributes.size(); i++) {
                if (fileAttributeTreeItem.get(i).getValue().getDir()) {
                    //TODO структура папки клиента
                }
                fileAttributeTreeItem.get(0).getChildren().addAll(fileAttributeTreeItem.get(i));
            }
            Platform.runLater(()->{
                clientTreeDir.setRoot(fileAttributeTreeItem.get(0));
                clientTreeDir.setShowRoot(true);
                updateTableView("client",fileAttributes);
            });
        } else if (dir.equals("server")) {
            for (FileAttribute fileAttribute : serverFileAttributes) {
                fileAttributeTreeItem.add(new TreeItem<>(fileAttribute));
            }
            fileAttributeTreeItem.get(0).setExpanded(true);
            for (int i = 1; i < serverFileAttributes.size(); i++) {
                if (fileAttributeTreeItem.get(i).getValue().getDir()) {
                    //TODO структура папки сервера
                }
                fileAttributeTreeItem.get(0).getChildren().addAll(fileAttributeTreeItem.get(i));
            }
            Platform.runLater(()->{
                serverTreeDir.setRoot(fileAttributeTreeItem.get(0));
                serverTreeDir.setShowRoot(true);
                updateTableView("server",serverFileAttributes);
            });
        }
    }

    private void updateTableView(String dir, List<FileAttribute> fileAttributes) {
        TableColumn<FileAttribute, String> nameCol =
                new TableColumn<>("Имя");

        TableColumn<FileAttribute, String> fileModificationDateCol =
                new TableColumn<>("Дата изменения");

        TableColumn<FileAttribute, String> typeCol =
                new TableColumn<>("Тип");
        TableColumn<FileAttribute, Long> sizeCol =
                new TableColumn<>("Размер");

        nameCol.setCellValueFactory(new PropertyValueFactory<>("type"));

        fileModificationDateCol.setCellValueFactory(new PropertyValueFactory<>("fileModificationDate"));

        typeCol.setCellValueFactory(new PropertyValueFactory<>("fileType"));
        sizeCol.setCellValueFactory(new PropertyValueFactory<>("size"));

        nameCol.setSortType(TableColumn.SortType.DESCENDING);

        Platform.runLater(() -> {
            if (dir.equals("client")) {
                tableViewClient.getColumns().clear();
                tableViewClient.setItems(FXCollections.observableArrayList(fileAttributes.subList(1, fileAttributes.size())));
                tableViewClient.getColumns().addAll(nameCol, fileModificationDateCol, typeCol, sizeCol);

            } else if (dir.equals("server")) {
                tableViewServer.getColumns().clear();
                tableViewServer.setItems(FXCollections.observableArrayList(fileAttributes.subList(1, fileAttributes.size())));
                tableViewServer.getColumns().addAll(nameCol, fileModificationDateCol, typeCol, sizeCol);
            }
        });
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            clientDir = Path.of("files-client/senkinay");
            fileAttributes = new ArrayList<>();
            fileAttributesCurrent = new ArrayList<>();
            serverFileAttributes = new ArrayList<>();

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
    public void upload(ActionEvent actionEvent) {
        Platform.runLater(()-> {
            MultipleSelectionModel<TreeItem<FileAttribute>> selectionModel = clientTreeDir.getSelectionModel();
            if (fileAttributes.get(0).equals(selectionModel.getSelectedItem().getValue())) {
                for (int i = 1; i < fileAttributes.size(); i++) {
                    try {
                        net.write(new FileUploadMessage(Path.of(fileAttributes.get(i).getName())));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    net.write(new FileUploadMessage(Path.of(selectionModel.getSelectedItem().getValue().getName())));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    public void download(ActionEvent actionEvent) {
        Platform.runLater(()-> {
            try {
                MultipleSelectionModel<TreeItem<FileAttribute>> selectionModel = serverTreeDir.getSelectionModel();
                net.write(new FileDownloadMessage(Path.of(selectionModel.getSelectedItem().getValue().getName()), null));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }
}