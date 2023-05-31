package com.redispulse.controller;

import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
public class MainFrameController implements Initializable {
    public ConnectionsController connectionsController;
    public OperationsController operationsController;
    public HBox connections;
    public VBox operations;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectionsController.setOperationsController(operationsController);
    }
}
