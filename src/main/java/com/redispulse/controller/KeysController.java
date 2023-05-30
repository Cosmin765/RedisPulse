package com.redispulse.controller;

import com.redispulse.RedisPulseApplication;
import com.redispulse.util.KeyData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class KeysController implements Initializable {
    private final Logger logger = LogManager.getLogger(ConnectionsController.class);
    @FXML
    private ListView<Parent> keysListView;
    private OperationsController operationsController;
    private final Map<Parent, KeyController> controllers = new HashMap<>();

    public void setOperationsController(OperationsController operationsController) {
        this.operationsController = operationsController;
    }

    public void addKey(KeyData keyData) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RedisPulseApplication.class.getResource("key.fxml"));
            Parent newItem = fxmlLoader.load();
            keysListView.getItems().add(newItem);
            KeyController keyController = fxmlLoader.getController();
            keyController.setOperationsController(operationsController);
            keyController.setKeyData(keyData);

            controllers.put(newItem, keyController);
        } catch (IOException e) {
            logger.info("Error while loading the key template", e);
        }
    }

    public void clearKeys() {
        keysListView.getItems().clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        keysListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                controllers.get(newValue).handleSelect();
            }
        });
    }
}
