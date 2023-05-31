package com.redispulse.controller;

import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class OperationsController {
    public Text titleText;
    public VBox valueContainer;
    public HBox controllersContainer;

    public void delete() {
        titleText.setText("");
        valueContainer.getChildren().clear();
        controllersContainer.getChildren().clear();
    }
}
