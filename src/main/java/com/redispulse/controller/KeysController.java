package com.redispulse.controller;

import com.redispulse.RedisPulseApplication;
import com.redispulse.util.KeyData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class KeysController implements Initializable {
    private final Logger logger = LogManager.getLogger(ConnectionsController.class);
    @FXML
    private Button addButton;
    @FXML
    private ListView<Parent> keysListView;
    private OperationsController operationsController;
    private final Map<Parent, KeyController> controllers = new HashMap<>();
    private Jedis connection;
    private final List<KeyData> keysData = new ArrayList<>();

    public void setOperationsController(OperationsController operationsController) {
        this.operationsController = operationsController;
    }

    public void setConnection(Jedis connection) {
        this.connection = connection;
        addButton.setVisible(true);
    }

    public void addKey(KeyData keyData) {
        keysData.add(keyData);
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
        keysData.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        keysListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null) {
                return;
            }
            controllers.get(newValue).handleSelect();
        });

        keysListView.setOnContextMenuRequested(event -> {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem reloadOption = new MenuItem("Reload");
            MenuItem deleteOption = new MenuItem("Delete");
            MenuItem renameOption = new MenuItem("Rename");

            reloadOption.setOnAction(e -> {
                int selectedIndex = keysListView.getSelectionModel().getSelectedIndex();
                Parent selectedElement = keysListView.getItems().get(selectedIndex);
                controllers.get(selectedElement).handleSelect();
            });

            deleteOption.setOnAction(e -> {
                int selectedIndex = keysListView.getSelectionModel().getSelectedIndex();
                Parent selectedElement = keysListView.getItems().get(selectedIndex);
                KeyController keyController = controllers.get(selectedElement);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.setTitle("Delete");
                String message = String.format("Are you sure you want to delete \"%s\"?", keyController.getKeyData().name());
                alert.setHeaderText(message);

                alert.getButtonTypes().setAll(ButtonType.OK, ButtonType.CANCEL);

                ButtonType result = alert.showAndWait().orElse(ButtonType.CANCEL);

                if(result == ButtonType.OK) {
                    keyController.deleteKey();
                }
            });

            renameOption.setOnAction(e -> {
                int selectedIndex = keysListView.getSelectionModel().getSelectedIndex();
                Parent selectedElement = keysListView.getItems().get(selectedIndex);
                KeyController keyController = controllers.get(selectedElement);

                keyController.textField.setVisible(true);
                keyController.textField.requestFocus();
                keyController.keyLabel.setVisible(false);

                keyController.textField.setText(keyController.keyLabel.getText());
            });

            contextMenu.getItems().addAll(reloadOption, deleteOption, renameOption);
            contextMenu.show(keysListView, event.getScreenX(), event.getScreenY());
        });
    }

    public void onAddKeyPress(ActionEvent ignored) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RedisPulseApplication.class.getResource("key-popup.fxml"));

            Parent root = fxmlLoader.load();
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Add key");
            popupStage.setResizable(false);
            popupStage.setWidth(300);
            popupStage.setHeight(150);

            KeyPopupController keyPopupController = fxmlLoader.getController();
            keyPopupController.setHandler(this::addNewKey);
            keyPopupController.setPopupStage(popupStage);

            Scene scene = new Scene(root);
            scene.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    popupStage.close();
                }
            });
            popupStage.setScene(scene);

            popupStage.showAndWait();

        } catch (IOException e) {
            logger.error("Error while creating the popup", e);
        }
    }

    private boolean addNewKey(KeyData keyData) {
        if(keysData.stream().filter(data -> data.name().equals(keyData.name())).toList().size() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Duplicate key");
            String message = String.format("A key with the name \"%s\" already exists", keyData.name());
            alert.setContentText(message);
            alert.showAndWait();
            return false;
        }
        addKey(new KeyData(keyData.name(), keyData.type(), connection));
        return true;
    }

    public void delete() {
        keysListView.getItems().clear();
        operationsController.delete();
    }
}
