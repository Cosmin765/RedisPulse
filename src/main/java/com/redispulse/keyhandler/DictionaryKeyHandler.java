package com.redispulse.keyhandler;

import com.redispulse.operations.DictionaryOperations;
import com.redispulse.util.KeyData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.AbstractMap;
import java.util.Map;

public class DictionaryKeyHandler extends KeyHandler {
    private final DictionaryOperations operations;
    private TableView<Map.Entry<String, String>> tableView;
    private TextField keyField;
    private TextArea valueArea;
    private String oldKey;
    private boolean shouldScroll;
    public DictionaryKeyHandler(KeyData keyData) {
        super(keyData);
        operations = new DictionaryOperations(keyData.name(), keyData.connection());
    }

    @SuppressWarnings("unchecked")
    private void addValues() {
        operationsController.valueContainer.getChildren().clear();

        tableView = new TableView<>();
        tableView.setPrefHeight(200);

        tableView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if(newValue == null) {
                return;
            }

            oldKey = newValue.getKey();

            keyField.setText(newValue.getKey());
            valueArea.setText(newValue.getValue());
        });

        // scrolling event
        tableView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if(newSkin == null || !shouldScroll) {
                return;
            }
            Platform.runLater(() -> {
                tableView.scrollTo(tableView.getSelectionModel().getSelectedIndex());
                shouldScroll = false;
            });
        });

        TableColumn<Map.Entry<String, String>, Double> scoreColumn = new TableColumn<>("Key");
        TableColumn<Map.Entry<String, String>, String> valueColumn = new TableColumn<>("Value");

        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("key"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("value"));

        tableView.getColumns().addAll(scoreColumn, valueColumn);

        ObservableList<Map.Entry<String, String>> items = FXCollections.observableArrayList();
        for(Map.Entry<String, String> item : operations.getRange(0, 50)) {
            items.add(item);
        }

        tableView.setItems(items);

        operationsController.valueContainer.getChildren().add(tableView);

    }

    private void addEditing() {
        keyField = new TextField();
        valueArea = new TextArea();
        valueArea.setWrapText(true);
        operationsController.valueContainer.getChildren().addAll(keyField, valueArea);
    }

    private void addButtons() {
        operationsController.controllersContainer.getChildren().clear();
        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> onSavePressed());
        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(event -> onDeletePressed());
        Button addButton = new Button("Add entry");
        addButton.setOnAction(event -> onAddPressed());
        operationsController.controllersContainer.getChildren()
                .addAll(saveButton, deleteButton, addButton);
    }

    private void onSavePressed() {
        int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
        if(selectedIndex == -1) {
            return;
        }

        String newKey = keyField.getText();
        String newValue = valueArea.getText();

        if(oldKey != null && !newKey.equals(oldKey)) {
            operations.getJedis().hdel(keyData.name(), oldKey);
        }
        oldKey = null;

        Map.Entry<String, String> item = new AbstractMap.SimpleEntry<>(newKey, newValue);
        operations.push(item);
        handleSelect();
        tableView.getSelectionModel().select(item);
        shouldScroll = true;
    }

    private void onDeletePressed() {
        operations.getJedis().hdel(keyData.name(), oldKey);
        handleSelect();
    }

    private void onAddPressed() {
        oldKey = null;
        keyField.clear();
        valueArea.clear();
        keyField.requestFocus();
        tableView.getSelectionModel().clearSelection();
    }

    @Override
    public void handleSelect() {
        operations.setKey(keyData.name());
        addValues();
        addEditing();
        addButtons();
    }
}
