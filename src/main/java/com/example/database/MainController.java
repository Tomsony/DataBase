package com.example.database;

import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * Основной контроллер для управления главным окном приложения
 * Отвечает за:
 * - Отображение данных в таблице
 * - Обработку действий пользователя (добавление, удаление, редактирование)
 * - Поиск и фильтрацию данных
 * - Работу с файлами (сохранение/загрузка)
 * - Управление меню и панелью инструментов
 */
public class MainController {

    // ==================== FXML КОМПОНЕНТЫ ====================

    @FXML
    private TableView<Person> tableView;          // Таблица для отображения данных о людях
    @FXML
    private TableColumn<Person, Integer> colId;   // Колонка для отображения ID
    @FXML
    private TableColumn<Person, String> colName;  // Колонка для отображения имени
    @FXML
    private TableColumn<Person, String> colSurname; // Колонка для отображения фамилии
    @FXML
    private TableColumn<Person, String> colEmail; // Колонка для отображения email
    @FXML
    private TableColumn<Person, String> colPhone; // Колонка для отображения телефона
    @FXML
    private TextField searchField;                // Поле для ввода поискового запроса

    // ==================== ДАННЫЕ И РЕПОЗИТОРИЙ ====================

    /**
     * Репозиторий для работы с данными (синглтон)
     * Хранит все записи и предоставляет методы для их управления
     */
    private final PersonRepository repository = PersonRepository.getInstance();

    /**
     * Фильтрованный список для поиска
     * Позволяет динамически фильтровать данные без изменения исходного списка
     */
    private final FilteredList<Person> filteredData;

    // ==================== КОНСТРУКТОР ====================

    /**
     * Конструктор контроллера
     * Инициализирует фильтрованный список с начальным предикатом (все записи отображаются)
     */
    public MainController() {
        filteredData = new FilteredList<>(PersonRepository.getInstance().getAllPersons(), p -> true);
    }

    // ==================== ИНИЦИАЛИЗАЦИЯ ====================

    /**
     * Метод инициализации, вызываемый автоматически после загрузки FXML
     * Настраивает:
     * - Связывание колонок таблицы с полями класса Person
     * - Редактирование ячеек
     * - Поиск и фильтрацию данных
     */
    @FXML
    public void initialize() {
        // ----- Настройка колонок таблицы -----
        // Связываем каждую колонку с соответствующим полем класса Person
        // PropertyValueFactory использует рефлексию для доступа к геттерам (getId(), getName() и т.д.)
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // ----- Настройка редактирования -----
        // Делаем таблицу редактируемой и настраиваем поведение колонок
        setupEditableColumns();

        // ----- Установка данных -----
        // Загружаем начальные данные из репозитория в таблицу
        tableView.setItems(repository.getAllPersons());

        // ----- Настройка поиска -----
        // Добавляем слушатель на изменение текста в поле поиска
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            // Обновляем предикат фильтрации при каждом изменении текста
            filteredData.setPredicate(person -> {
                // Если поле поиска пустое - показываем все записи
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                // Приводим поисковый запрос к нижнему регистру для регистронезависимого поиска
                String lowerCaseFilter = newValue.toLowerCase();

                // Проверяем, содержит ли любое поле записи искомый текст
                return person.getName().toLowerCase().contains(lowerCaseFilter)      // Поиск по имени
                        || person.getSurname().toLowerCase().contains(lowerCaseFilter) // Поиск по фамилии
                        || person.getEmail().toLowerCase().contains(lowerCaseFilter)   // Поиск по email
                        || person.getPhone().toLowerCase().contains(lowerCaseFilter)   // Поиск по телефону
                        || String.valueOf(person.getId()).contains(lowerCaseFilter);    // Поиск по ID
            });
        });

        // Устанавливаем фильтрованный список в таблицу
        tableView.setItems(filteredData);
    }

    // ==================== НАСТРОЙКА РЕДАКТИРОВАНИЯ ====================

    /**
     * Настройка редактируемых колонок таблицы
     * Позволяет пользователю редактировать ячейки двойным кликом
     */
    private void setupEditableColumns() {
        // Включаем режим редактирования таблицы
        tableView.setEditable(true);

        // ----- Настройка колонки ID -----
        // Используем конвертер для преобразования текста в Integer
        colId.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        // При завершении редактирования обновляем значение в объекте Person
        colId.setOnEditCommit(e -> e.getRowValue().setId(e.getNewValue()));

        // ----- Настройка колонки Имя -----
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(e -> e.getRowValue().setName(e.getNewValue()));

        // ----- Настройка колонки Фамилия -----
        colSurname.setCellFactory(TextFieldTableCell.forTableColumn());
        colSurname.setOnEditCommit(e -> e.getRowValue().setSurname(e.getNewValue()));

        // ----- Настройка колонки Email -----
        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        colEmail.setOnEditCommit(e -> e.getRowValue().setEmail(e.getNewValue()));

        // ----- Настройка колонки Телефон -----
        colPhone.setCellFactory(TextFieldTableCell.forTableColumn());
        colPhone.setOnEditCommit(e -> e.getRowValue().setPhone(e.getNewValue()));
    }

    // ==================== МЕТОДЫ УПРАВЛЕНИЯ ДАННЫМИ ====================

    /**
     * Добавление нового человека в таблицу
     * Этот метод вызывается из окна добавления записи
     *
     * @param person Объект Person, созданный в окне добавления
     */
    public void addPerson(Person person) {
        repository.addPerson(person);
    }

    /**
     * Удаление выбранной записи
     * Проверяет, выбрана ли запись, и удаляет её из репозитория
     */
    @FXML
    private void handleDelete() {
        // Получаем выбранную запись из таблицы
        Person selected = tableView.getSelectionModel().getSelectedItem();

        // Если запись выбрана - удаляем её
        if (selected != null) {
            repository.deletePerson(selected);
        }
        // Если запись не выбрана - ничего не делаем (можно добавить уведомление)
    }

    // ==================== НАВИГАЦИЯ И ОКНА ====================

    /**
     * Открытие окна справки
     * Загружает assistance.fxml и отображает его в новом окне
     *
     * @param event Событие, вызвавшее открытие справки
     * @throws IOException Если файл FXML не найден
     */
    @FXML
    private void help(ActionEvent event) throws IOException {
        // Создаем загрузчик для FXML файла справки
        FXMLLoader loader = new FXMLLoader(getClass().getResource("assistance.fxml"));

        // Загружаем корневой элемент интерфейса
        Parent root = loader.load();

        // Настраиваем и отображаем окно
        Stage stage = new Stage();

        // Устанавливаем иконку окна
        Image icon = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/com/example/database/icons/logo-16x16.png")
        ));
        stage.getIcons().add(icon);

        // Устанавливаем сцену и заголовок
        stage.setScene(new Scene(root));
        stage.setTitle("Справка");

        // Показываем окно (не блокирующее)
        stage.show();
    }

    /**
     * Открытие окна для добавления новой записи
     * Загружает addLineMenu.fxml и передаёт контроллеру ссылку на MainController
     *
     * @param event Событие, вызвавшее открытие окна
     * @throws IOException Если файл FXML не найден
     */
    @FXML
    private void openAddScene(ActionEvent event) throws IOException {
        // Создаем загрузчик для FXML файла формы добавления
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addLineMenu.fxml"));

        // Загружаем интерфейс
        Parent root = loader.load();

        // Получаем контроллер дочернего окна и передаём ему ссылку на главный контроллер
        AddLineController addLineController = loader.getController();
        addLineController.setMainController(this);

        // Создаем и настраиваем новое окно
        Stage stage = new Stage();

        // Устанавливаем иконку
        Image icon = new Image(Objects.requireNonNull(
                getClass().getResourceAsStream("/com/example/database/icons/logo-16x16.png")
        ));
        stage.getIcons().add(icon);

        // Настраиваем сцену и заголовок
        stage.setScene(new Scene(root));
        stage.setTitle("Ввод данных");

        // Показываем окно
        stage.show();
    }

    // ==================== ОБРАБОТЧИКИ МЕНЮ "ФАЙЛ" ====================

    /**
     * Обработчик для создания нового файла / очистки всех данных
     * Полностью очищает репозиторий
     *
     * @param actionEvent Событие нажатия на пункт меню "Создать"
     */
    public void handleNew(ActionEvent actionEvent) {
        repository.clearAll();
    }

    /**
     * Обработчик для открытия и загрузки данных из файла
     * Открывает диалог выбора файла и загружает данные из текстового файла
     *
     * @param actionEvent Событие нажатия на пункт меню "Открыть"
     */
    public void handleOpen(ActionEvent actionEvent) {
        // Создаем диалог выбора файла
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить данные");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );

        // Показываем диалог и получаем выбранный файл
        File file = fileChooser.showOpenDialog(tableView.getScene().getWindow());

        // Если файл выбран
        if (file != null) {
            try {
                // Очищаем текущие данные и загружаем новые из файла
                repository.clearAll();
                repository.loadFromFile(file);

                // Обновляем отображение таблицы
                tableView.setItems(repository.getAllPersons());
            } catch (IOException | NumberFormatException e) {
                // В случае ошибки выводим сообщение в консоль
                System.err.println("Ошибка загрузки: " + e.getMessage());
            }
        }
    }

    /**
     * Обработчик для сохранения данных в файл
     * Открывает диалог выбора места сохранения и сохраняет данные в текстовый файл
     *
     * @param actionEvent Событие нажатия на пункт меню "Сохранить"
     */
    public void handleSave(ActionEvent actionEvent) {
        // Создаем диалог сохранения файла
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить данные");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt")
        );

        // Показываем диалог и получаем файл для сохранения
        File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());

        // Если файл выбран
        if (file != null) {
            try {
                // Сохраняем данные в файл
                repository.saveToFile(file);
            } catch (IOException e) {
                // В случае ошибки выводим сообщение в консоль
                System.err.println("Ошибка сохранения: " + e.getMessage());
            }
        }
    }

    /**
     * Обработчик для выхода из приложения
     * Завершает работу JavaFX приложения
     *
     * @param actionEvent Событие нажатия на пункт меню "Выход"
     */
    public void handleExit(ActionEvent actionEvent) {
        Platform.exit();
    }

    // ==================== ОБРАБОТЧИКИ МЕНЮ "ПРАВКА" ====================

    /**
     * Обновление данных в таблице
     * Получает актуальные данные из репозитория и очищает поле поиска
     *
     * @param actionEvent Событие, вызвавшее обновление
     */
    @FXML
    private void handleRefresh(ActionEvent actionEvent) {
        // Получаем актуальный список из репозитория
        tableView.setItems(repository.getAllPersons());

        // Очищаем поле поиска для сброса фильтрации
        searchField.clear();

        // Логируем действие (для отладки)
        System.out.println("Данные обновлены");
    }

    /**
     * Очистка всех данных в таблице
     * Запрашивает подтверждение у пользователя перед удалением
     *
     * @param actionEvent Событие, вызвавшее очистку
     */
    @FXML
    private void handleClearAll(ActionEvent actionEvent) {
        // Создаем диалоговое окно подтверждения
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Очистка всех данных");
        alert.setContentText("Вы уверены, что хотите удалить все записи?");

        // Показываем диалог и обрабатываем ответ пользователя
        alert.showAndWait().ifPresent(response -> {
            // Если пользователь подтвердил удаление
            if (response == ButtonType.OK) {
                // Очищаем репозиторий
                repository.clearAll();

                // Обновляем отображение таблицы
                tableView.setItems(repository.getAllPersons());

                // Логируем действие
                System.out.println("Все данные очищены");
            }
            // Если пользователь нажал Cancel или закрыл окно - ничего не делаем
        });
    }

    // ==================== ОБРАБОТЧИКИ МЕНЮ "СПРАВКА" ====================

    /**
     * Показать информацию о программе
     * Отображает диалоговое окно с данными о версии и разработчике
     *
     * @param actionEvent Событие нажатия на пункт меню "О программе"
     */
    @FXML
    private void handleAbout(ActionEvent actionEvent) {
        // Создаем информационное диалоговое окно
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("О программе");
        alert.setHeaderText("Приложение для работы с базой данных");
        alert.setContentText(
                "Версия: 1.0\n" +
                        "Разработчик: tttmsnyy\n" +
                        "Год: 2025\n\n" +
                        "Приложение позволяет создавать, редактировать и удалять записи о людях.\n" +
                        "Данные сохраняются в текстовые файлы."
        );

        // Показываем диалоговое окно и ждем закрытия
        alert.showAndWait();
    }
}