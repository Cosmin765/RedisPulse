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
        operationsController.controllersContainer.getChildren().add(saveButton);
    }

    private void onSavePressed() {
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if(selectedIndex == -1) {
            return;
        }
        String textAreaContent = textArea.getText();
        operations.getJedis().lset(keyData.name(), selectedIndex, textAreaContent);
        handleSelect();  // reload the values
        listView.getSelectionModel().select(selectedIndex);
        shouldScroll = true;
    }

    @Override
    public void handleSelect() {
        operations.setKey(keyData.name());
        addValues();
        addEditing();
        addButtons();
    }
}