module com.redispulse.redispulse {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;
    requires lombok;


    opens com.redispulse.redispulse to javafx.fxml;
    opens com.redispulse.redispulse.util to com.fasterxml.jackson.databind;
    exports com.redispulse.redispulse;
    exports com.redispulse.redispulse.controller;
    exports com.redispulse.redispulse.util;
    opens com.redispulse.redispulse.controller to javafx.fxml;
}