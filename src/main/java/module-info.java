module com.redispulse.redispulse {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires lombok;
    requires log4j.api;


    opens com.redispulse to javafx.fxml;
    opens com.redispulse.util to com.fasterxml.jackson.databind;
    exports com.redispulse;
    exports com.redispulse.controller;
    exports com.redispulse.util;
    opens com.redispulse.controller to javafx.fxml;
}