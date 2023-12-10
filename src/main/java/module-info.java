module com.example.newgame {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.newgame to javafx.fxml;
    exports com.example.newgame;
}