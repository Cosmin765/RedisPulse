package com.redispulse.controller;

import com.redispulse.controller.keyhandler.*;
import com.redispulse.util.KeyData;
import com.redispulse.util.KeyType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.Text;
import javafx.util.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class KeyController {
    private final Logger logger = LogManager.getLogger(ConnectionsController.class);
    private final Map<KeyType, Pair<Color, String>> typeToData = new HashMap<>();
    @FXML
    private Label keyLabel;
    @FXML
    private Text letterText;
    @FXML
    private Circle bubble;
    private KeyData keyData;
    private OperationsController operationsController;
    private KeyHandler keyHandler;

    public KeyController() {
        typeToData.put(KeyType.DICTIONARY, new Pair<>(Color.RED, "D"));
        typeToData.put(KeyType.STRING, new Pair<>(Color.GREEN, "V"));
        typeToData.put(KeyType.ZSET, new Pair<>(Color.PURPLE, "Z"));
        typeToData.put(KeyType.SET, new Pair<>(Color.BLUE, "S"));
        typeToData.put(KeyType.LIST, new Pair<>(Color.ORANGE, "L"));
    }

    public void setOperationsController(OperationsController operationsController) {
        this.operationsController = operationsController;
    }

    public void deleteKey() {
        keyData.connection().del(keyData.name());
        keyLabel.setTextFill(Color.GRAY);
        keyLabel.setFont(Font.font("System", FontPosture.ITALIC, 12));
    }

    private KeyHandler getKeyHandler(KeyData keyData) {
        switch (keyData.type()) {
            case LIST -> {
                return new ListKeyHandler(keyData);
            }
            case SET -> {
                return new SetKeyHandler(keyData);
            }
            case ZSET -> {
                return new SortedSetHandler(keyData);
            }
            case STRING -> {
                return new StringKeyHandler(keyData);
            }
            case DICTIONARY -> {
                return new DictionaryKeyHandler(keyData);
            }
        }
        throw new RuntimeException(keyData.type() + " not handled.");
    }

    public void setKeyData(KeyData keyData) {
        this.keyData = keyData;
        keyHandler = this.getKeyHandler(keyData);
        keyHandler.setOperationsController(operationsController);

        Pair<Color, String> renderData = typeToData.get(keyData.type());
        if(renderData == null) {
            logger.error("Render data not implemented for type " + keyData.type());
            return;
        }

        String keyName = keyData.name();
        Color bubbleColor = renderData.getKey();
        String letter = renderData.getValue();

        keyLabel.setText(keyName);
        bubble.setFill(bubbleColor);
        letterText.setText(letter);

//         TODO: delete this hardcoding
        if (keyData.name().equals("a")) {
            this.handleSelect();
        }
    }

    public void handleSelect() {
        operationsController.titleText.setText(keyData.name());

        keyHandler.handleSelect();
    }
}
