package com.redispulse.util;

import java.io.Serializable;

public record KeyData(String name, KeyType type) implements Serializable {}
