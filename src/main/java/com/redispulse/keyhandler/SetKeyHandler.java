package com.redispulse.keyhandler;

import com.redispulse.operations.SetOperations;
import com.redispulse.util.KeyData;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;

public class SetKeyHandler extends KeyHandler {
    private final SetOperations operations;
    private ListView<Text> listView;
    private TextArea textArea;
    private String oldElement;

    public SetKeyHandler(KeyData keyData) {
        super(keyData);
        operations = new SetOperations(keyData.name(), keyData.connection());
    }

    private void addValues() {
        operationsController.valueContainer.getChildren().clear();

        listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setPrefHeight(200);

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                oldElement = newValue.getText();
                textArea.setText(newValue.getText());
            }
        });

        // scrolling event
        listView.skinProperty().addListener((obs, oldSkin, newSkin) -> {
            if(newSkin == null) {
                return;
            }
            Platform.runLater(() -> listView.scrollTo(listView.getSelectionModel().getSelectedIndex()));
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
        operationsController.controllersContainer.getChildren().addAll(saveButton, deleteButton);
    }

    private void onSavePressed() {
        String textAreaContent = textArea.getText();

        if(!oldElement.equals(textAreaContent)) {
            operations.getJedis().srem(keyData.name(), oldElement);
        }

        operations.push(textAreaContent);
        handleSelect();  // reload the values
    }

    private void onDeletePressed() {
        operations.getJedis().srem(keyData.name(), oldElement);
        handleSelect();
    }

    @Override
    public void handleSelect() {
        operations.setKey(keyData.name());
        addValues();
        addEditing();
        addButtons();
    }
}
