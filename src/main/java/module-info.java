module com.textapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.graphics;
    requires javafx.media;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;

    requires com.almasb.fxgl.all;
    requires jbcrypt;
    requires mysql.connector.j;

    requires org.apache.poi.ooxml;

    // iText modules - with correct names
    requires kernel;
    requires layout;
    requires io;

    // Logging modules
    requires org.slf4j;


    // Guava
    requires com.google.common;
    requires java.desktop;
    requires org.apache.logging.log4j;
    requires java.sql;

    opens com.textapp to javafx.fxml;
    exports com.textapp;
    exports com.textapp.controller;
    opens com.textapp.controller to javafx.fxml;

    // For your model classes
    opens com.textapp.models to javafx.base;
}