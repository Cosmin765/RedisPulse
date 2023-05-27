package com.redispulse.controller;

import com.redispulse.operations.ListOperations;
import com.redispulse.util.KeyData;
import com.redispulse.util.KeyType;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    public OperationsController operationsController;
//    private KeyData keyData;

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
            logger.error("Render data not implemented for type " + keyData.type());
            return;
        }

        String keyName = keyData.name();
        Color bubbleColor = renderData.getKey();
        String letter = renderData.getValue();

        keyLabel.setText(keyName);
        bubble.setFill(bubbleColor);
        letterText.setText(letter);
    }

    private void handleSelect() {
        operationsController.valueText.setText(keyData.name());

        if(keyData.type() == KeyType.LIST) {
            ListOperations listOperations = new ListOperations();
            listOperations.setKey(keyData.name());
            listOperations.setJedis(keyData.connection());

            System.out.println(keyData.name());

//            listOperations.remove();
//            for(int j = 0; j < 1000; ++j) {
//                List<String> items = new ArrayList<>();
//                for(int i = 0; i < 1000; ++i) {
//                    items.add(Integer.toString(i));
//                }
//                System.out.println(j);
//                listOperations.pushMultiple(items);
//            }
//            System.out.println("saved");
            long index = 0;
            long start = System.nanoTime();
            for(String item : listOperations.get()) {
//                if(index > 100_000) {
//                    break;
//                }
                index++;
            }
            long end = System.nanoTime();

            long delta = end - start;
            System.out.println((float)delta / 1_000_000);

            System.out.println(index);
        }

    }

    @FXML
    private void onKeyClick(MouseEvent event) {
        switch (event.getButton()) {
            case PRIMARY -> handleSelect();
//            case SECONDARY -> {
//                if (event.getClickCount() == 1) {
//                    contextMenu.show(ellipsis, event.getScreenX(), event.getScreenY());
//                }
//            }
        }
    }
}
