module com.redispulse.redispulse {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires lombok;
    requires log4j.api;
    requires redis.clients.jedis;
    requires java.compiler;


    opens com.redispulse to javafx.fxml;
    opens com.redispulse.util to com.fasterxml.jackson.databind;
    exports com.redispulse;
    exports com.redispulse.controller;
    exports com.redispulse.util;
    exports com.redispulse.operations.base;
    opens com.redispulse.controller to javafx.fxml;
}