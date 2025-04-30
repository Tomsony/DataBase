module com.example.database {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.database to javafx.fxml;
    exports com.example.database;
}