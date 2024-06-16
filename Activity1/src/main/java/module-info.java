module com.example.activity1 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.activity1 to javafx.fxml;
    exports com.example.activity1;
}