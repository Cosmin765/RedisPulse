package com.redispulse.controller;

import com.redispulse.RedisPulseApplication;
import com.redispulse.util.ConnectionData;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class ConnectionController {
    @FXML
    private Label ellipsis;
    @FXML
    private Text nameText;
    private ContextMenu contextMenu;
    private ConnectionsController connectionsController;
    private final Logger logger = LogManager.getLogger(ConnectionsController.class);
    public void setConnectionsController(ConnectionsController connectionsController) {
        this.connectionsController = connectionsController;
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
        if (event.getButton() == MouseButton.PRIMARY) {
            if (event.getClickCount() == 1) {
                contextMenu.show(ellipsis, event.getScreenX(), event.getScreenY());
            }
        }
    }
    @FXML
    private void handleOptionEdit(ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RedisPulseApplication.class.getResource("connection-popup.fxml"));

            Parent root = fxmlLoader.load();

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Add connection");
            popupStage.setResizable(false);
            popupStage.setWidth(300);
            popupStage.setHeight(200);

            PopupController popupController = fxmlLoader.getController();

            Optional<ConnectionData> connectionData = connectionsController.getConnections().stream().filter(connection ->
                    nameText.getText().equals(connection.name())).findAny();

            if(connectionData.isEmpty()) {
                logger.error("Connection was not found while editing");
                return;
            }

            popupController.setConnectionData(connectionData.get());
            popupController.setHandler(connection -> connectionsController.editConnection(nameText.getText(), connection));
            popupController.setPopupStage(popupStage);

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
        connectionsController.deleteConnection(connectionName);
    }
}
