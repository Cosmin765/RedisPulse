package com.redispulse.controller.keyhandler;

import com.redispulse.util.KeyData;

public abstract class KeyHandler {
    protected KeyData keyData;
    public KeyHandler(KeyData keyData) {
        this.keyData = keyData;
    }
    public abstract void handleSelect();
}
