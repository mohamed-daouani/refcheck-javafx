module RefCheck {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires javafx.swing;

    requires org.slf4j;
    requires java.sql;

    requires tess4j;
    requires jbcrypt;

    opens view to javafx.fxml, javafx.base;
    opens controller to javafx.fxml;
    exports main;
}
