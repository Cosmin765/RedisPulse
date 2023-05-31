package com.redispulse.controller;

import com.redispulse.RedisPulseApplication;
import com.redispulse.util.ConnectionData;
import com.redispulse.util.KeyData;
import com.redispulse.util.KeyType;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConnectionController {
    private Jedis jedis;
    private ConnectionData connectionData;
    @FXML
    private Label ellipsis;
    @FXML
    private Text nameText;
    private ContextMenu contextMenu;
    private ConnectionsController connectionsController;
    private KeysController keysController;
    private final Logger logger = LogManager.getLogger(ConnectionsController.class);

    public void setKeysController(KeysController keysController) {
        this.keysController = keysController;
    }
    public void setConnectionsController(ConnectionsController connectionsController) {
        this.connectionsController = connectionsController;
    }
    public void setConnectionData(ConnectionData connectionData) {
        this.connectionData = connectionData;
    }
    @FXML
    private void initialize() {
        contextMenu = new ContextMenu();

        Map<String, EventHandler<ActionEvent>> optionData = new LinkedHashMap<>();
        optionData.put("Edit", this::handleOptionEdit);
        optionData.put("Delete", this::handleOptionDelete);

        for(String optionText : optionData.keySet()) {
            MenuItem option = new MenuItem(optionText);
            option.setOnAction(optionData.get(optionText));
            contextMenu.getItems().add(option);
        }
    }
    @FXML
    private void onEllipsisClick(MouseEvent event) {
        if(event.getButton() == MouseButton.PRIMARY) {
            if(event.getClickCount() == 1) {
                contextMenu.show(ellipsis, event.getScreenX(), event.getScreenY());
            }
        }
    }
    private boolean initializeJedis() {
        if(jedis != null && jedis.isConnected()) {
            jedis.disconnect();
        }
        int timeoutMillis = 20_000;
        JedisClientConfig config = DefaultJedisClientConfig.builder()
                .timeoutMillis(timeoutMillis)
                .connectionTimeoutMillis(timeoutMillis)
                .socketTimeoutMillis(timeoutMillis)
                .build();

        try {
            jedis = new Jedis(connectionData.address(), connectionData.port(), config);
            jedis.connect();
        } catch (JedisConnectionException e) {
            logger.error("Error while connecting to the redis server", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            String message = String.format("Connection to %s:%d failed", connectionData.address(), connectionData.port());
            alert.setContentText(message);
            alert.showAndWait();
            return false;
        }
        return true;
    }
    private void handleSelect() {
        if(!initializeJedis()) {
            return;
        }

        keysController.clearKeys();
        for(String key : jedis.keys("*")) {
            String type = jedis.type(key);

            KeyType keyType = KeyType.fromString(type);

            if(keyType == null) {
                logger.error(type + " is not implemented");
                return;
            }

            KeyData keyData = new KeyData(key, keyType, jedis);
            keysController.addKey(keyData);
        }
    }
    @FXML
    private void onConnectionClick(MouseEvent event) {
        switch (event.getButton()) {
            case PRIMARY -> {
                if (event.getClickCount() == 2) {
                    handleSelect();
                }
            }
            case SECONDARY -> {
                if (event.getClickCount() == 1) {
                    contextMenu.show(ellipsis, event.getScreenX(), event.getScreenY());
                }
            }
        }
    }
    private boolean editConnection(ConnectionData connection) {
        boolean succeeded = connectionsController.editConnection(nameText.getText(), connection);
        if(succeeded) {
            setConnectionData(connection);
        }
        return succeeded;
    }
    @FXML
    private void handleOptionEdit(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RedisPulseApplication.class.getResource("connection-popup.fxml"));

            Parent root = fxmlLoader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Edit connection");
            popupStage.setResizable(false);
            popupStage.setWidth(300);
            popupStage.setHeight(200);

            ConnectionPopupController connectionPopupController = fxmlLoader.getController();

            connectionPopupController.setConnectionData(connectionData);
            connectionPopupController.setHandler(this::editConnection);
            connectionPopupController.setPopupStage(popupStage);

            Scene scene = new Scene(root);
            popupStage.setScene(scene);

            popupStage.showAndWait();

        } catch (IOException e) {
            logger.error("Error while creating the popup", e);
        }
    }
    @FXML
    private void handleOptionDelete(ActionEvent event) {
        String connectionName = nameText.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

        alert.setTitle("Delete");
        String message = String.format("Are you sure you want to delete \"%s\"?", connectionName);
        alert.setHeaderText(message);

        alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

        ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

        if(result == ButtonType.OK) {
            connectionsController.deleteConnection(connectionData.id());
        }
    }
}
