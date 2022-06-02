module com.senkinay.cloud {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires io.netty.codec;
    requires com.senkinay.geekcloudcore;

    opens com.senkinay.cloud to javafx.fxml;
    exports com.senkinay.cloud;
    opens com.senkinay.cloud.controllers to javafx.fxml;
    exports com.senkinay.cloud.controllers;

}
