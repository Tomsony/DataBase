package com.example.database;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;
import java.util.Objects;

/**
 * Формат хранения данных:
 * - Все данные хранятся в памяти в ObservableList<Person>
 * - Структура Person:
 *   id: целое число, уникальный идентификатор
 *   name: строка
 *   surname: строка
 *   email: строка, должна содержать '@'
 *   phone: строка, произвольный формат
 *
 * - Автоматическая сортировка по ID при добавлении
 * - Редактирование прямо в таблице
 * - Поиск по всем текстовым полям
 */

public class MainApplication extends Application {
    /** основная точка входа для JavaFX-приложений */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        // Загрузка иконки в новое окно
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/database/icons/logo-32x32.png")));
        stage.getIcons().add(icon); // Установка иконки
        stage.setTitle("База данных");
        stage.setScene(scene);
        stage.show();
    }

    /** Стандартный метод main - точка входа для Java-приложения */
    public static void main(String[] args) {
        launch();
    }
}