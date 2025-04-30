package com.example.database;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

//Класс контроллер для окна validation
//Единственная функция == отображать ошибку валидации данных
public class ValidationController {
    @FXML
    private TextArea errorTextArea;

    /**
     @param message передает сообщение об ошибке из AddLineController
     */
    public void setErrorMessage(String message){
        errorTextArea.setText(message);
        errorTextArea.requestLayout();
    }

    @FXML
    public void initialize() {
        // Проверка инициализации TextArea
        System.out.println("TextArea initialized: " + (errorTextArea != null));
    }

    @FXML
    private void closeWindow() {
        ((Stage) errorTextArea.getScene().getWindow()).close();
    }
}
