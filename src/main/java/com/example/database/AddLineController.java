package com.example.database;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

/// Контроллер для окна добавления новой записи
/// @author Артём Томских
public class AddLineController {
    // Поля ввода для данных о человеке
    @FXML
    private TextField idField; // Поле для ввода ID
    @FXML
    private TextField nameField; // Поле для ввода имени
    @FXML
    private TextField surnameField; // Поле для ввода фамилии
    @FXML
    private TextField emailField; // Поле для ввода email
    @FXML
    private TextField phoneField; // Поле для ввода телефона

    private PersonRepository repository = PersonRepository.getInstance(); // Добавляем доступ к репозиторию

    private MainController mainController; // Ссылка на главный контроллер

    /** Установка главного контроллера */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /** Обработчик нажатия кнопки "Создать" */
    @FXML
    private void handleCreate() {
        String errorMessage = validateInput();

        if(errorMessage != null){
            showError(errorMessage);
            return;}

        try {
             //Создание нового объекта Person с данными из полей ввода
            Person person = new Person(
                    Integer.parseInt(idField.getText()),
                    nameField.getText(),
                    surnameField.getText(),
                    emailField.getText(),
                    phoneField.getText()
            );

            //Добавление человека через главный контроллер
           // mainController.addPerson(person);
            repository.addPerson(person);

            // Закрытие окна
            closeWindow();

            //Обработка ошибок
        } catch (NumberFormatException e) {
            showError("Некорректный ввод данных!");
        }
    }

    private void showError(String errorMessage) {
        errorTextArea.setText(errorMessage);
    }

    /** Метод для валидации введенных данных */
    private String validateInput() {
        try {
            StringBuilder errors = new StringBuilder();
            int inputId = Integer.parseInt(idField.getText());

            // Проверка ID
            if(inputId < 0) errors.append("ID не может быть отрицательным!\n");
            if(repository.containsId(inputId)) errors.append("ID должен быть уникальным!\n");

            // Проверка имени
            if(nameField.getText().isEmpty()) errors.append("Имя не может быть пустым!\n");

            // Проверка фамилии
            if(surnameField.getText().isEmpty()) errors.append("Фамилия не может быть пустой!\n");

            // Проверка email
            if(!emailField.getText().contains("@")) errors.append("Email должен содержать @\n");

            return errors.toString().isEmpty() ? null : errors.toString();

        } catch (NumberFormatException e) {
            return "ID должен быть числом!";
        }
    }

    /** Метод для закрытия текущего окна */
    private void closeWindow() {
        ((Stage) idField.getScene().getWindow()).close();
    }

    @FXML
    private TextArea errorTextArea;

        public void setErrorMessage(String message)
        {
        errorTextArea.setText(message);
        errorTextArea.requestLayout();
        }

}
