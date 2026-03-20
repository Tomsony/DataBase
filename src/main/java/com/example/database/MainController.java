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

/// Основной контроллер для управления главным окном приложения
public class MainController {
    @FXML
    private TableView<Person> tableView; // Таблица для отображения данных
    @FXML
    private TableColumn<Person, Integer> colId; // Колонка для ID
    @FXML
    private TableColumn<Person, String> colName; // Колонка для имени
    @FXML
    private TableColumn<Person, String> colSurname; // Колонка для фамилии
    @FXML
    private TableColumn<Person, String> colEmail; // Колонка для email
    @FXML
    private TableColumn<Person, String> colPhone; // Колонка для телефона

    @FXML private TextField searchField; // Поисковое поле

    private final PersonRepository repository = PersonRepository.getInstance(); // Данные для таблицы

    private final FilteredList<Person> filteredData; //Данные для поиска

    //Конструктор для поиска
    public MainController() {
        filteredData = new FilteredList<>(PersonRepository.getInstance().getAllPersons(), p -> true);
    }

    @FXML
    public void initialize() {
        // Инициализация колонок таблицы
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colSurname.setCellValueFactory(new PropertyValueFactory<>("surname"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Настройка возможности редактирования колонок
        setupEditableColumns();

        // Установка данных в таблицу
        tableView.setItems(repository.getAllPersons());

        // Настройка поиска
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(person -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Показывать все строки если поиск пуст
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return person.getName().toLowerCase().contains(lowerCaseFilter)
                        || person.getSurname().toLowerCase().contains(lowerCaseFilter)
                        || person.getEmail().toLowerCase().contains(lowerCaseFilter)
                        || person.getPhone().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(person.getId()).contains(lowerCaseFilter);
            });
        });

        tableView.setItems(filteredData);
    }

    /**
     * Метод для настройки редактируемых колонок таблицы
     */
    private void setupEditableColumns() {
        tableView.setEditable(true);

        // Для колонки ID
        colId.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colId.setOnEditCommit(e -> e.getRowValue().setId(e.getNewValue()));

        // Для колонки Имя
        colName.setCellFactory(TextFieldTableCell.forTableColumn());
        colName.setOnEditCommit(e -> e.getRowValue().setName(e.getNewValue()));

        // Для колонки Фамилия
        colSurname.setCellFactory(TextFieldTableCell.forTableColumn());
        colSurname.setOnEditCommit(e -> e.getRowValue().setSurname(e.getNewValue()));

        // Для колонки Email
        colEmail.setCellFactory(TextFieldTableCell.forTableColumn());
        colEmail.setOnEditCommit(e -> e.getRowValue().setEmail(e.getNewValue()));

        // Для колонки Телефон
        colPhone.setCellFactory(TextFieldTableCell.forTableColumn());
        colPhone.setOnEditCommit(e -> e.getRowValue().setPhone(e.getNewValue()));
    }

    /**
     * Метод для добавления нового человека в таблицу
     */
    public void addPerson(Person person) {
        repository.addPerson(person);
    }

//    /** Метод для сортировки данных по ID*/
//    private void sortData() {
//        data.sort((p1, p2) -> Integer.compare(p1.getId(), p2.getId()));
//    }

    /**
     * Метод для удаления выбранной записи
     */
    @FXML
    private void handleDelete() {
        Person selected = tableView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            repository.deletePerson(selected);
        }
    }

    @FXML
    private void help(ActionEvent event) throws IOException {
        // Создание загрузчика FXML для файла интерфейса нового окна
        FXMLLoader loader = new FXMLLoader(getClass().getResource("assistance.fxml"));
        // Загрузка интерфейса из FXML-файла
        Parent root = loader.load();

        // Настройка окна
        Stage stage = new Stage();
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/database/icons/logo-16x16.png")));
        stage.getIcons().add(icon);
        stage.setScene(new Scene(root));
        stage.setTitle("Справка");
        stage.show();
    }

    /**
     * Метод для открытия окна добавления новой записи
     * @param event Объект события, создающий новую сцену
     */
    @FXML
    private void openAddScene(ActionEvent event) throws IOException {
        // Создание загрузчика FXML для файла интерфейса нового окна
        FXMLLoader loader = new FXMLLoader(getClass().getResource("addLineMenu.fxml"));

        // Загрузка интерфейса из FXML-файла
        Parent root = loader.load();

        // Получение контроллера для загруженного FXML
        AddLineController addLineController = loader.getController();
        addLineController.setMainController(this);

        // Создание нового окна (Stage)
        Stage stage = new Stage();

        // Загрузка иконки для нового окна
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/example/database/icons/logo-16x16.png")));
        stage.getIcons().add(icon); // Установка иконки

        // Создание сцены с загруженным интерфейсом
        stage.setScene(new Scene(root));

        // Установка заголовка окна
        stage.setTitle("Ввод данных");

        // Отображение окна
        stage.show();
    }

    /**
     * Обработчик для создания нового файла/очистки данных
     * @param actionEvent Событие нажатия на пункт меню "New"
     */
    public void handleNew(ActionEvent actionEvent) {
        repository.clearAll();
    }

    /**
     * Обработчик для открытия и загрузки данных из файла
     * @param actionEvent Событие нажатия на пункт меню "Open"
     */
    public void handleOpen(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Загрузить данные");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));

        File file = fileChooser.showOpenDialog(tableView.getScene().getWindow());
        if (file != null) {
            try {
                repository.clearAll();
                repository.loadFromFile(file);
                tableView.setItems(repository.getAllPersons());
            } catch (IOException | NumberFormatException e) {
                System.err.println("Ошибка загрузки" + e.getMessage());
            }
        }
    }

    /**
     * Обработчик для сохранения данных в файл
     * @param actionEvent Событие нажатия на пункт меню "Save"
     */
    public void handleSave(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить данные");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"));

        File file = fileChooser.showSaveDialog(tableView.getScene().getWindow());
        if (file != null) {
            try {
                repository.saveToFile(file);
            } catch (IOException e) {
                System.err.println("Ошибка сохранения: " + e.getMessage());
            }
        }
    }

    /**
     * Обработчик для выхода из приложения
     * @param actionEvent Событие нажатия на пункт меню "Exit"
     */
    public void handleExit(ActionEvent actionEvent) {
        Platform.exit();
    }

    /**
     * Обновление данных в таблице
     * Метод обновляет отображение таблицы, получая актуальные данные из репозитория
     * и очищает поле поиска для сброса фильтрации
     *
     * @param actionEvent Событие, вызвавшее обновление (нажатие кнопки или пункта меню)
     */
    @FXML
    private void handleRefresh(ActionEvent actionEvent) {
        // Получаем актуальный список всех персон из репозитория и устанавливаем его в таблицу
        tableView.setItems(repository.getAllPersons());

        // Очищаем поле поиска, чтобы сбросить все примененные фильтры
        searchField.clear();

        // Выводим сообщение в консоль для отладки
        System.out.println("Данные обновлены");
    }

    /**
     * Очистка всех данных в таблице
     * Метод запрашивает подтверждение у пользователя перед удалением всех записей.
     * В случае подтверждения очищает репозиторий и обновляет отображение таблицы.
     *
     * @param actionEvent Событие, вызвавшее очистку (нажатие кнопки или пункта меню)
     */
    @FXML
    private void handleClearAll(ActionEvent actionEvent) {
        // Создаем диалоговое окно подтверждения действия
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение");
        alert.setHeaderText("Очистка всех данных");
        alert.setContentText("Вы уверены, что хотите удалить все записи?");

        // Отображаем диалог и ожидаем ответа пользователя
        alert.showAndWait().ifPresent(response -> {
            // Если пользователь подтвердил действие (нажал OK)
            if (response == ButtonType.OK) {
                // Очищаем репозиторий - удаляем все записи
                repository.clearAll();

                // Обновляем таблицу, чтобы отобразить пустой список
                tableView.setItems(repository.getAllPersons());

                // Выводим сообщение в консоль для отладки
                System.out.println("Все данные очищены");
            }
            // Если пользователь нажал Cancel или закрыл окно - ничего не делаем
        });
    }

    /**
     * Показать информацию о программе
     */
    @FXML
    private void handleAbout(ActionEvent actionEvent) {
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

        // Показываем диалоговое окно и ждем, пока пользователь его закроет
        alert.showAndWait();
    }
}