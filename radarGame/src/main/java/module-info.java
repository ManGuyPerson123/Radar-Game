module com.example.radargame {
    requires javafx.controls;
    requires java.desktop;


    opens com.example.radargame to javafx.fxml;
    exports com.example.radargame;
}