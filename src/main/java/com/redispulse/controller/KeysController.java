package com.redispulse.controller;

import com.redispulse.RedisPulseApplication;
import com.redispulse.util.KeyData;
import com.redispulse.util.KeyType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class KeysController {
    private final Logger logger = LogManager.getLogger(ConnectionsController.class);
    @FXML
    private ListView<Parent> keysListView;
    private final List<KeyData> keysData = new ArrayList<>();
    public void addKey(KeyData keyData) {
        keysData.add(keyData);

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RedisPulseApplication.class.getResource("key.fxml"));
            Parent newItem = fxmlLoader.load();
            keysListView.getItems().add(newItem);
            KeyController keyController = fxmlLoader.getController();
            keyController.setKeyData(keyData);
        } catch (IOException e) {
            logger.info("Error while loading the key template", e);
        }
    }

    public void clearKeys() {
        keysData.clear();
        keysListView.getItems().clear();
    }
}
