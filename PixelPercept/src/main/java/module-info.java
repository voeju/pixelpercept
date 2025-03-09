module de.uni_hannover.pixelpercept {
    requires javafx.controls;
    requires javafx.fxml;
    requires transitive java.desktop;

    opens de.uni_hannover.pixelpercept.view to javafx.fxml;
    opens de.uni_hannover.pixelpercept.controller to javafx.fxml;
    exports de.uni_hannover.pixelpercept.view;
    exports de.uni_hannover.pixelpercept.controller;
    exports de.uni_hannover.pixelpercept.model;
}