package com.redispulse.controller;

import com.redispulse.util.ConnectionData;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.UUID;
import java.util.function.Function;

public class ConnectionPopupController {
    private ConnectionData connectionData;
    private Function<ConnectionData, Boolean> handler;
    private Stage popupStage;
    @FXML
    private TextField nameField;
    @FXML
    private TextField addressField;
    @FXML
    private TextField portField;
    public void setHandler(Function<ConnectionData, Boolean> handler) {
        this.handler = handler;
    }
    public void setConnectionData(ConnectionData connectionData) {
        this.connectionData = connectionData;
        nameField.setText(connectionData.name());
        addressField.setText(connectionData.address());
        portField.setText(connectionData.port().toString());
    }
    public void setPopupStage(Stage popupStage) {
        this.popupStage = popupStage;
    }
    @FXML
    private void initialize() {
        portField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                portField.setText(newValue.replaceAll("\\D", ""));
            }
        });

        nameField.textProperty().addListener((observable, oldValue, newValue) -> nameField.setStyle("-fx-border-color: none;"));
    }
    @FXML
    private void onAddConnectionPress() {
        String connectionName = nameField.getText().trim();
        if(connectionName.equals("")) {
            nameField.setStyle("-fx-border-color: red;");
            return;
        }

        String address = addressField.getText().trim();
        String port = portField.getText().trim();
        if(address.equals("")) {
            address = addressField.getPromptText();
        }
        if(port.equals("")) {
            port = portField.getPromptText();
        }
        UUID connectionId = connectionData == null ? null : connectionData.id();
        boolean complete = handler.apply(new ConnectionData(connectionId, connectionName, address, Integer.parseInt(port)));

        if(complete) {
            popupStage.close();
        }
    }
}
