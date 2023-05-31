package com.redispulse.keyhandler;

import com.redispulse.controller.OperationsController;
import com.redispulse.util.KeyData;

public abstract class KeyHandler {
    protected KeyData keyData;
    protected OperationsController operationsController;
    public KeyHandler(KeyData keyData) {
        this.keyData = keyData;
    }
    public void setOperationsController(OperationsController operationsController) {
        this.operationsController = operationsController;
    }
    public abstract void handleSelect();

    public void setKeyData(KeyData keyData) {
        this.keyData = keyData;
    }
}
