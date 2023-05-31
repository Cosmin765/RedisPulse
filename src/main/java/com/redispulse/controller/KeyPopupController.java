package com.redispulse.controller;

import com.redispulse.util.KeyData;
import com.redispulse.util.KeyType;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.function.Function;

public class KeyPopupController {
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private TextField nameField;
    private Stage popupStage;
    Function<KeyData, Boolean> handler;

    public void setPopupStage(Stage popupStage) {
        this.popupStage = popupStage;
    }
    public void onAddKeyPress(ActionEvent ignored) {
        String keyName = nameField.getText();
        KeyType keyType = KeyType.fromString(comboBox.getValue());
        KeyData keyData = new KeyData(keyName, keyType, null);
        boolean success = handler.apply(keyData);

        if(success) {
            popupStage.close();
        }
    }

    public void setHandler(Function<KeyData, Boolean> handler) {
        this.handler = handler;
    }
}
