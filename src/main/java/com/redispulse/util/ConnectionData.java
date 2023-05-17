package com.redispulse.util;

import java.io.Serializable;
import java.util.UUID;

public record ConnectionData(UUID id, String name, String address, Integer port) implements Serializable {}