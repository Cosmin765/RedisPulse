package com.redispulse.keyhandler;

import com.redispulse.operations.StringOperations;
import com.redispulse.util.KeyData;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class StringKeyHandler extends KeyHandler {
    private final StringOperations operations;
    private TextArea textArea;
    public StringKeyHandler(KeyData keyData) {
        super(keyData);
        operations = new StringOperations(keyData.name(), keyData.connection());
    }

    private void addTextArea() {
        operationsController.valueContainer.getChildren().clear();
        textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setText(operations.read());
        operationsController.valueContainer.getChildren().add(textArea);
    }

    private void addButtons() {
        operationsController.controllersContainer.getChildren().clear();
        Button saveButton = new Button("Save");
        saveButton.setOnAction(event -> onSavePressed());
        operationsController.controllersContainer.getChildren().add(saveButton);
    }

    private void onSavePressed() {
        String textAreaContent = textArea.getText();
        operations.assign(textAreaContent);
    }

    @Override
    public void handleSelect() {
        operations.setKey(keyData.name());
        addTextArea();
        addButtons();
    }
}
