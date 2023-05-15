package com.redispulse.controller;

import com.redispulse.util.ConnectionData;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class PopupController {
    private ConnectionsController connectionsController;
    private Stage popupStage;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField portField;
    public void setConnectionsController(ConnectionsController connectionsController) {
        this.connectionsController = connectionsController;
    }
    public void setPopupStage(Stage popupStage) {
        this.popupStage = popupStage;
    }
    @FXML
    private void initialize() {
        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                portField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }
    @FXML
    private void onAddConnectionPress() {
        String connectionName = nameField.getText();
        String address = addressField.getText();
        String port = portField.getText();
        connectionsController.addNewConnection(new ConnectionData(connectionName, address, port));
        popupStage.close();
    }
}
