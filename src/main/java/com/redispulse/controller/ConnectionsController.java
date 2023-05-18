package com.redispulse.controller;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.redispulse.RedisPulseApplication;
import com.redispulse.util.Config;
import com.redispulse.util.ConnectionData;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ConnectionsController {
    @FXML
    private HBox connectionsRoot;
    @FXML
    private ListView<Parent> connectionsListView;
    private final Map<UUID, ConnectionData> connections = new LinkedHashMap<>();
    private final Logger logger = LogManager.getLogger(ConnectionsController.class);
    private KeysController keysController;

    @FXML
    private void initialize() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RedisPulseApplication.class.getResource("keys.fxml"));

            Parent root = fxmlLoader.load();
            connectionsRoot.getChildren().add(root);
            keysController = fxmlLoader.getController();
        } catch (IOException e) {
            logger.error("Error while appending the keys component to the connection", e);
        }
        loadConnections();
    }
    private void loadConnections() {
        File connectionsFile = new File(Config.CONNECTIONS_JSON_FILE);
        if(!connectionsFile.isFile()) {
            logger.info("The connections json file does not exist");
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<ConnectionData> connectionsLocal = objectMapper.readValue(connectionsFile, new TypeReference<>() {});
            connectionsLocal.forEach(connectionData -> connections.put(connectionData.id(), connectionData));
            connectionsLocal.forEach(this::renderConnection);
            logger.info("Loaded the connections json file");
        } catch (JsonParseException | JsonMappingException e) {
            logger.error("Error while parsing the connections json file", e);
        } catch (IOException e) {
            logger.error("Error while reading the connections json file", e);
        }
    }
    private void renderConnection(ConnectionData connection) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(RedisPulseApplication.class.getResource("connection.fxml"));
            Parent newItem = fxmlLoader.load();
            connectionsListView.getItems().add(newItem);
            Text connectionName = (Text) newItem.lookup("#nameText");
            connectionName.setText(connection.name());

            ConnectionController connectionController = fxmlLoader.getController();
            connectionController.setConnectionsController(this);
            connectionController.setConnectionData(connection);
            connectionController.setKeysController(keysController);
        } catch (IOException e) {
            logger.info("Error while loading the connection template", e);
        }
    }
    public boolean addNewConnection(ConnectionData connection) {
        if(connections.values().stream().filter(c -> c.name().equals(connection.name())).toList().size() > 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            String message = String.format("A connection with the name \"%s\" already exists", connection.name());
            alert.setContentText(message);
            alert.showAndWait();
            return false;
        }
        UUID connectionId = UUID.randomUUID();
        connections.put(connectionId, new ConnectionData(connectionId, connection.name(), connection.address(), connection.port()));
        dumpConnections();
        renderConnection(connection);
        logger.info("Added a new connection: " + connection);
        return true;
    }
    public boolean editConnection(String connectionName, ConnectionData newConnection) {
        if(connections.values().stream().filter(
                c -> !c.id().equals(newConnection.id()) && c.name().equals(newConnection.name())).toList().size() > 0) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("An error occurred");
            String message = String.format("A connection with the name \"%s\" already exists", newConnection.name());
            alert.setContentText(message);
            alert.showAndWait();
            return false;
        }

        connections.put(newConnection.id(), newConnection);

        Optional<Parent> item = connectionsListView.getItems().stream().filter(el -> ((Text) el.lookup("#nameText")).getText().equals(connectionName)).findFirst();

        if(item.isEmpty()) {
            logger.error("Could not find the connection inside the ListView");
            return false;
        }

        Text nameText = (Text) item.get().lookup("#nameText");
        nameText.setText(newConnection.name());

        dumpConnections();
        return true;
    }
    public void deleteConnection(UUID connectionId) {
        String connectionName = connections.get(connectionId).name();
        connections.remove(connectionId);
        connectionsListView.getItems().removeIf(hBox -> ((Text) hBox.lookup("#nameText")).getText().equals(connectionName));
        dumpConnections();
        logger.info("Removed connection: " + connectionName);
    }
    private void dumpConnections() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            File connectionsFile = new File(Config.CONNECTIONS_JSON_FILE);
            objectMapper.writeValue(connectionsFile, connections.values());
            logger.info("Dumped the connections");
        } catch (IOException e) {
            logger.error("Error while dumping the connections", e);
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

            ConnectionPopupController connectionPopupController = fxmlLoader.getController();
            connectionPopupController.setHandler(this::addNewConnection);
            connectionPopupController.setPopupStage(popupStage);

            Scene scene = new Scene(root);
            popupStage.setScene(scene);

            popupStage.showAndWait();

        } catch (IOException e) {
            logger.error("Error while creating the popup", e);
        }
    }
}
