
package com.example.database;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

// Основной контроллер для управления главным окном приложения
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

    private final PersonRepository repository = PersonRepository.getInstance(); // Данные для таблицы

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
    * Обработка нажатия кнопки Новая таблица
    */
    public void createBtn(ActionEvent actionEvent) {
        repository.clearAll();
    }

    /**
     * Обработка нажатия кнопки Сохранить
     */
    public void saveBtn(ActionEvent actionEvent) {
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
     * Обработка нажатия кнопки Загрузить
     */
    public void loadBtn(ActionEvent actionEvent) {
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
}