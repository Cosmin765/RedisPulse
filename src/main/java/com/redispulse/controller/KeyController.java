package com.redispulse.controller;

import com.redispulse.util.KeyData;
import com.redispulse.util.KeyType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.HashMap;
import java.util.Map;

public class KeyController {
    private final Map<KeyType, Pair<Color, String>> typeToData = new HashMap<>();
    @FXML
    private Label keyLabel;
    @FXML
    private Text letterText;
    @FXML
    private Circle bubble;
    private KeyData keyData;

    public KeyController() {
        typeToData.put(KeyType.DICTIONARY, new Pair<>(Color.RED, "D"));
        typeToData.put(KeyType.STRING, new Pair<>(Color.GREEN, "V"));
        typeToData.put(KeyType.ZSET, new Pair<>(Color.PURPLE, "Z"));
        typeToData.put(KeyType.SET, new Pair<>(Color.BLUE, "S"));
        typeToData.put(KeyType.LIST, new Pair<>(Color.ORANGE, "L"));
    }
    public void setKeyData(KeyData keyData) {
        this.keyData = keyData;

        Pair<Color, String> renderData = typeToData.get(keyData.type());
        if(renderData == null) {
            System.out.println("Render data not implemented for type " + keyData.type());
            return;
        }

        String keyName = keyData.name();
        Color bubbleColor = renderData.getKey();
        String letter = renderData.getValue();

        keyLabel.setText(keyName);
        bubble.setFill(bubbleColor);
        letterText.setText(letter);
    }
}
