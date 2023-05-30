package com.redispulse.controller.keyhandler;

import com.redispulse.operations.ListOperations;
import com.redispulse.operations.base.OrderedIterableOperations;
import com.redispulse.util.KeyData;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class ListKeyHandler extends KeyHandler {
    private final OrderedIterableOperations<String> operations;
    private TextArea textArea;
    public ListKeyHandler(KeyData keyData) {
        super(keyData);
        operations = new ListOperations(keyData.name(), keyData.connection());
    }

    @Override
    public void handleSelect() {
        operationsController.valueContainer.getChildren().clear();

        ListView<Text> listView = new ListView<>();
        listView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        listView.setPrefHeight(200);

        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                textArea.setText(newValue.getText());
            }
        });

        for(String item : operations.getRange(0, 10)) {
            Text element = new Text(item);


            listView.getItems().add(element);
        }
        operationsController.valueContainer.getChildren().add(listView);

        textArea = new TextArea();
        textArea.setWrapText(true);
        operationsController.valueContainer.getChildren().add(textArea);
    }
}
