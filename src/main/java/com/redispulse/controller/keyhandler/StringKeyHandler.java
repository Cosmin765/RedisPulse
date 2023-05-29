package com.redispulse.controller.keyhandler;

import com.redispulse.operations.StringOperations;
import com.redispulse.operations.base.BasicOperations;
import com.redispulse.util.KeyData;
import javafx.scene.text.Text;

public class StringKeyHandler extends KeyHandler {
    private final BasicOperations<String> operations;
    public StringKeyHandler(KeyData keyData) {
        super(keyData);
        operations = new StringOperations(keyData.name(), keyData.connection());
    }

    @Override
    public void handleSelect() {
        Text value = new Text(operations.read());
        operationsController.valueContainer.getChildren().clear();
        operationsController.valueContainer.getChildren().add(value);
    }
}
