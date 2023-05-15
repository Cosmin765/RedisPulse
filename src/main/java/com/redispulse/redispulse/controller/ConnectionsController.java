package com.redispulse.redispulse.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.redispulse.redispulse.RedisPulseApplication;
import com.redispulse.redispulse.util.Config;
import com.redispulse.redispulse.util.ConnectionData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConnectionsController {
    @FXML
    public ListView<HBox> listView;
    private List<ConnectionData> connections = new ArrayList<>();

    @FXML
    private void initialize() {
        loadConnections();
    }

    private void loadConnections() {
        File connectionsFile = new File(Config.CONNECTIONS_JSON_FILE);
        if(!connectionsFile.isFile()) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            connections = objectMapper.readValue(connectionsFile, new TypeReference<>() {});
            connections.forEach(this::renderConnection);
        } catch (JsonParseException | JsonMappingException ignored) {
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void renderConnection(ConnectionData connection) {
        HBox newItem = new HBox();
        newItem.getChildren().add(new Text(connection.name()));
        listView.getItems().add(newItem);
    }
    public void addNewConnection(ConnectionData connection) {
        connections.add(new ConnectionData(connection.name(), connection.address(), connection.port()));
        dumpConnections();
        renderConnection(connection);
    }

    private void dumpConnections() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            File connectionsFile = new File(Config.CONNECTIONS_JSON_FILE);
            objectMapper.writeValue(connectionsFile, connections);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    protected void onAddConnectionPress() {
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
            popupController.setConnectionsController(this);
            popupController.setPopupStage(popupStage);

            Scene scene = new Scene(root);
            popupStage.setScene(scene);

            popupStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
