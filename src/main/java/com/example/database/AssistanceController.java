package com.example.database;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.text.Text;
import javafx.stage.Stage;

///  Класс реализует обработку закрытия окна
public class AssistanceController {
    @FXML
    private Text title;

    @FXML
    private TextArea description;

    public void closeWindow(ActionEvent event) {
        ((Stage) ((Button)event.getSource()).getScene().getWindow()).close();
    }
}
