package com.redispulse.keyhandler;

import com.redispulse.operations.SortedSetOperations;
import com.redispulse.util.KeyData;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import redis.clients.jedis.resps.Tuple;

public class SortedSetHandler extends KeyHandler {
    private final SortedSetOperations operations;
    private TableView<Tuple> tableView;
    private TextField scoreField;
    private TextArea elementArea;
    private String oldElement;
    private boolean shouldScroll;
    public SortedSetHandler(KeyData keyData) {
        super(keyData);
        operations = new SortedSetOperations(keyData.name(), keyData.connection());
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

            oldElement = newValue.getElement();

            scoreField.setText(String.valueOf(newValue.getScore()));
            elementArea.setText(newValue.getElement());
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

        TableColumn<Tuple, Double> scoreColumn = new TableColumn<>("Score");
        TableColumn<Tuple, String> valueColumn = new TableColumn<>("Value");

        scoreColumn.setCellValueFactory(new PropertyValueFactory<>("score"));
        valueColumn.setCellValueFactory(new PropertyValueFactory<>("element"));

        tableView.getColumns().addAll(scoreColumn, valueColumn);

        ObservableList<Tuple> items = FXCollections.observableArrayList();
        for(Tuple item : operations.getRange(0, 50)) {
            items.add(item);
        }

        tableView.setItems(items);

        operationsController.valueContainer.getChildren().add(tableView);
    }

    private void addEditing() {
        scoreField = new TextField();
        elementArea = new TextArea();
        elementArea.setWrapText(true);
        operationsController.valueContainer.getChildren().addAll(scoreField, elementArea);
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
        double newScore;
        try {
            newScore = Double.parseDouble(scoreField.getText());
        } catch (NumberFormatException ignored) {
            scoreField.setStyle("-fx-border-color: red;");
            return;
        }
        String newElement = elementArea.getText();

        if(oldElement != null && !newElement.equals(oldElement)) {
            operations.getJedis().zrem(keyData.name(), oldElement);
        }
        oldElement = null;

        Tuple item = new Tuple(newElement, newScore);
        operations.push(item);
        handleSelect();  // reload the values
        tableView.getSelectionModel().select(item);
        shouldScroll = true;
    }

    private void onDeletePressed() {
        operations.getJedis().zrem(keyData.name(), oldElement);
        handleSelect();
    }

    private void onAddPressed() {
        oldElement = null;
        scoreField.clear();
        elementArea.clear();
        scoreField.requestFocus();
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
