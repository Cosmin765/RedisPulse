package com.redispulse.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;
public class MainFrameController implements Initializable {
    @FXML
    private HBox connections;
    @FXML
    private ConnectionsController connectionsController;
    @FXML
    private VBox operations;
    @FXML
    private OperationsController operationsController;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        connectionsController.setOperationsController(operationsController);
    }
}
