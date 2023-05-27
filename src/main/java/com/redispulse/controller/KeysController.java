package com.redispulse.controller;

import com.redispulse.RedisPulseApplication;
import com.redispulse.util.KeyData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;

public class KeysController {
    private final Logger logger = LogManager.getLogger(ConnectionsController.class);
    @FXML
    private ListView<Parent> keysListView;
    public OperationsController operationsController;
//    private final List<KeyData> keysData = new ArrayList<>();

    public void addKey(KeyData keyData) {
//        keysData.add(keyData);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RedisPulseApplication.class.getResource("key.fxml"));
            Parent newItem = fxmlLoader.load();
            keysListView.getItems().add(newItem);
            KeyController keyController = fxmlLoader.getController();
            keyController.operationsController = operationsController;
            keyController.setKeyData(keyData);
        } catch (IOException e) {
            logger.info("Error while loading the key template", e);
        }
    }

    public void clearKeys() {
//        keysData.clear();
        keysListView.getItems().clear();
    }
}
