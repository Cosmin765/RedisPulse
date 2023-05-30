package com.redispulse.controller.keyhandler;

import com.redispulse.operations.StringOperations;
import com.redispulse.operations.base.BasicOperations;
import com.redispulse.util.KeyData;
import javafx.scene.control.TextArea;

public class StringKeyHandler extends KeyHandler {
    private final BasicOperations<String> operations;
    public StringKeyHandler(KeyData keyData) {
        super(keyData);
        operations = new StringOperations(keyData.name(), keyData.connection());
    }

    @Override
    public void handleSelect() {
        operationsController.valueContainer.getChildren().clear();

        TextArea textArea = new TextArea();
        textArea.setWrapText(true);
        textArea.setText(operations.read());
        operationsController.valueContainer.getChildren().add(textArea);
    }
}
