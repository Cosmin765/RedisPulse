package com.redispulse.keyhandler;

import com.redispulse.operations.ListOperations;
import com.redispulse.util.KeyData;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class ListKeyHandler extends KeyHandler {
    private final ListOperations operations;
    private ListView<Text> listView;
    private TextArea textArea;
    private boolean shouldScroll = false;
    private boolean addNew = false;
    public ListKeyHandler(KeyData keyData) {
        super(keyData);
        operations = new ListOperations(keyData.name(), keyData.connection());
    }

    private void addValues() {
        operationsController.valueContainer.getChildren().clear();

        listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setPrefHeight(200);

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                addNew = false;
                textArea.setText(newValue.getText());
            }
        });

        // scrolling event
        listView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if(newSkin == null || !shouldScroll) {
                return;
            }
            Platform.runLater(() -> {
                listView.scrollTo(listView.getSelectionModel().getSelectedIndex());
                shouldScroll = false;
            });
        });

        for(String item : operations.getRange(0, 50)) {
            Text element = new Text(item);
            listView.getItems().add(element);
        }

        operationsController.valueContainer.getChildren().add(listView);
    }

    private void addEditing() {
        textArea = new TextArea();
        textArea.setWrapText(true);
        operationsController.valueContainer.getChildren().add(textArea);
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
        String textAreaContent = textArea.getText();

        if(addNew) {
            operations.push(textAreaContent);
            handleSelect();
            return;
        }

        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if(selectedIndex == -1) {
            return;
        }
        operations.getJedis().lset(keyData.name(), selectedIndex, textAreaContent);
        handleSelect();  // reload the values
        listView.getSelectionModel().select(selectedIndex);
        shouldScroll = true;
    }

    private void onDeletePressed() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if(selectedIndex == -1) {
            return;
        }

        String element = listView.getItems().get(selectedIndex).getText();
        operations.getJedis().lrem(keyData.name(), 1, element);
        handleSelect();
    }

    private void onAddPressed() {
        textArea.clear();
        textArea.requestFocus();
        addNew = true;
        listView.getSelectionModel().clearSelection();
    }

    @Override
    public void handleSelect() {
        operations.setKey(keyData.name());
        addValues();
        addEditing();
        addButtons();
    }
}
