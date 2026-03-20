package com.example.database;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Контроллер для окна добавления новой записи
 * Отвечает за:
 * - Получение данных из полей ввода
 * - Валидацию введенных данных
 * - Создание объекта Person и добавление его в репозиторий
 * - Отображение ошибок при некорректном вводе
 *
 *  * КЛЮЧЕВЫЕ МОМЕНТЫ РАБОТЫ КОНТРОЛЛЕРА:
 *  *
 *  * 1. ВАЛИДАЦИЯ:
 *  *    - Все поля проверяются перед созданием объекта Person
 *  *    - ID проверяется на отрицательность и уникальность
 *  *    - Обязательные поля (имя, фамилия) проверяются на пустоту
 *  *    - Email проверяется на наличие символа @
 *  *
 *  * 2. ОБРАБОТКА ОШИБОК:
 *  *    - Ошибки валидации накапливаются в StringBuilder
 *  *    - Если есть ошибки - показываем их и не создаем запись
 *  *    - Исключения (NumberFormatException) обрабатываются отдельно
 *  *
 *  * 3. ВЗАИМОДЕЙСТВИЕ С РЕПОЗИТОРИЕМ:
 *  *    - Используется синглтон PersonRepository для доступа к данным
 *  *    - Проверка уникальности ID через repository.containsId()
 *  *    - Добавление записи через repository.addPerson()
 *  *
 *  * 4. СВЯЗЬ С ГЛАВНЫМ КОНТРОЛЛЕРОМ:
 *  *    - mainController используется для обновления таблицы после добавления
 *  *    - setMainController() вызывается из MainController после загрузки FXML
 *  *
 *  * 5. ЗАКРЫТИЕ ОКНА:
 *  *    - После успешного добавления окно автоматически закрывается
 *  *    - closeWindow() находит окно через элемент idField
 *
 * @author Артём Томских
 * @version 1.0
 */
public class AddLineController {

    // ==================== FXML КОМПОНЕНТЫ ====================

    @FXML
    private TextField idField;        // Поле для ввода уникального идентификатора (ID)
    @FXML
    private TextField nameField;      // Поле для ввода имени
    @FXML
    private TextField surnameField;   // Поле для ввода фамилии
    @FXML
    private TextField emailField;     // Поле для ввода email-адреса
    @FXML
    private TextField phoneField;     // Поле для ввода номера телефона
    @FXML
    private TextArea errorTextArea;   // Область для отображения сообщений об ошибках

    // ==================== ЗАВИСИМОСТИ ====================

    /**
     * Репозиторий для работы с данными (синглтон)
     * Используется для проверки уникальности ID и добавления новых записей
     */
    private PersonRepository repository = PersonRepository.getInstance();

    /**
     * Ссылка на главный контроллер
     * Используется для уведомления главного окна о добавлении новой записи
     */
    private MainController mainController;

    // ==================== ПУБЛИЧНЫЕ МЕТОДЫ ====================

    /**
     * Установка ссылки на главный контроллер
     * Вызывается из MainController после загрузки этого окна
     *
     * @param mainController Ссылка на главный контроллер приложения
     */
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    /**
     * Установка текста сообщения об ошибке
     * Используется для отображения ошибок из других частей приложения
     *
     * @param message Текст сообщения об ошибке
     */
    public void setErrorMessage(String message) {
        errorTextArea.setText(message);
        errorTextArea.requestLayout(); // Принудительно обновляем компоновку
    }

    // ==================== ОБРАБОТЧИКИ СОБЫТИЙ ====================

    /**
     * Обработчик нажатия кнопки "Создать"
     * Выполняет следующие шаги:
     * 1. Валидация введенных данных
     * 2. Если есть ошибки - отображает их и прерывает выполнение
     * 3. Если данные корректны - создает объект Person
     * 4. Добавляет запись в репозиторий
     * 5. Закрывает текущее окно
     */
    @FXML
    private void handleCreate() {
        // Шаг 1: Валидация введенных данных
        String errorMessage = validateInput();

        // Шаг 2: Если есть ошибки валидации - отображаем их и выходим
        if (errorMessage != null) {
            showError(errorMessage);
            return;
        }

        try {
            // Шаг 3: Создание нового объекта Person с данными из полей ввода
            // Integer.parseInt() преобразует строку из поля ID в целое число
            Person person = new Person(
                    Integer.parseInt(idField.getText()),  // ID (уникальный идентификатор)
                    nameField.getText(),                   // Имя
                    surnameField.getText(),                // Фамилия
                    emailField.getText(),                  // Email
                    phoneField.getText()                   // Телефон
            );

            // Шаг 4: Добавление человека в репозиторий
            // Вариант 1: Через главный контроллер (закомментирован)
            // mainController.addPerson(person);

            // Вариант 2: Непосредственное добавление в репозиторий
            repository.addPerson(person);

            // Шаг 5: Закрытие окна после успешного добавления
            closeWindow();

        } catch (NumberFormatException e) {
            // Обработка ошибки: если ID не является числом
            showError("Некорректный ввод данных! ID должен быть целым числом.");
        }
    }

    // ==================== ПРИВАТНЫЕ МЕТОДЫ ====================

    /**
     * Отображение сообщения об ошибке в текстовой области
     *
     * @param errorMessage Текст сообщения об ошибке для отображения
     */
    private void showError(String errorMessage) {
        errorTextArea.setText(errorMessage);
    }

    /**
     * Валидация введенных данных
     * Проверяет все поля на соответствие требованиям:
     * - ID: неотрицательное число, уникальность
     * - Имя: не пустое
     * - Фамилия: не пустая
     * - Email: содержит символ @
     *
     * @return null если все данные корректны, иначе строка с описанием ошибок
     */
    private String validateInput() {
        try {
            // Строитель для накопления сообщений об ошибках
            StringBuilder errors = new StringBuilder();

            // --- Проверка поля ID ---
            int inputId = Integer.parseInt(idField.getText());

            // Проверка: ID не может быть отрицательным
            if (inputId < 0) {
                errors.append("ID не может быть отрицательным!\n");
            }

            // Проверка: ID должен быть уникальным (не должен существовать в репозитории)
            if (repository.containsId(inputId)) {
                errors.append("ID должен быть уникальным! Запись с таким ID уже существует.\n");
            }

            // --- Проверка поля Имя ---
            if (nameField.getText().isEmpty()) {
                errors.append("Имя не может быть пустым!\n");
            }

            // --- Проверка поля Фамилия ---
            if (surnameField.getText().isEmpty()) {
                errors.append("Фамилия не может быть пустой!\n");
            }

            // --- Проверка поля Email ---
            // Email должен содержать символ @ для корректности адреса
            if (!emailField.getText().contains("@")) {
                errors.append("Email должен содержать символ '@' (например, user@domain.com)\n");
            }

            // Возвращаем null если ошибок нет, иначе строку с ошибками
            return errors.toString().isEmpty() ? null : errors.toString();

        } catch (NumberFormatException e) {
            // Обработка ошибки: если ID не является числом
            return "ID должен быть целым числом!";
        }
    }

    /**
     * Закрытие текущего окна добавления записи
     * Получает окно через любой из его элементов (idField) и закрывает его
     */
    private void closeWindow() {
        // Получаем окно (Stage) через любой элемент интерфейса и закрываем его
        ((Stage) idField.getScene().getWindow()).close();
    }
}
