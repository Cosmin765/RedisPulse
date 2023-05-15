package com.redispulse.redispulse.util;

import lombok.Data;

import java.io.Serializable;

public record ConnectionData(String name, String address, String port) implements Serializable {}