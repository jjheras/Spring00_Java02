module org.example.labjava02 {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens org.example.labjava02 to javafx.fxml;
    exports org.example.labjava02;
}