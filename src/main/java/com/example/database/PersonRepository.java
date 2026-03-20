package com.example.database;

///  @author Артём Томских

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Класс-репозиторий для хранения и управления данными о людях
 * Реализует шаблон проектирования "Одиночка" (Singleton)
 *
 * Основные функции:
 * - Хранение списка объектов Person в ObservableList для автоматического обновления UI
 * - Поддержка уникальности ID через Set<Integer>
 * - Сортировка записей по ID
 * - Сохранение и загрузка данных в/из текстового файла
 * - CRUD операции (Create, Read, Update, Delete)
 *
 *  * КЛЮЧЕВЫЕ КОНЦЕПЦИИ КЛАССА:
 *  *
 *  * 1. ШАБЛОН ОДИНОЧКА (SINGLETON):
 *  *    - Обеспечивает единственный экземпляр репозитория во всём приложении
 *  *    - Все контроллеры работают с одними и теми же данными
 *  *    - Ленивая инициализация - создание при первом обращении
 *  *
 *  * 2. OBSERVABLELIST:
 *  *    - Наблюдаемый список, автоматически обновляющий UI
 *  *    - Привязывается к TableView для автоматического отображения изменений
 *  *    - Изменения в списке сразу видны в интерфейсе
 *  *
 *  * 3. УНИКАЛЬНОСТЬ ID:
 *  *    - Множество (HashSet) обеспечивает быструю проверку уникальности O(1)
 *  *    - Исключение при попытке добавить дубликат
 *  *    - Автоматическое обновление при добавлении/удалении
 *  *
 *  * 4. СОРТИРОВКА:
 *  *    - Автоматическая сортировка после добавления
 *  *    - Используется Comparator.comparingInt для производительности
 *  *    - Всегда отображаются в порядке возрастания ID
 *  *
 *  * 5. РАБОТА С ФАЙЛАМИ:
 *  *    - Текстовый формат с разделителем ";" (CSV-like)
 *  *    - try-with-resources для безопасной работы с потоками
 *  *    - Обработка ошибок при чтении/записи
 *  *    - Автоматическая проверка формата при загрузке
 *  *
 *  * 6. ПОТОКОВАЯ БЕЗОПАСНОСТЬ:
 *  *    - Класс не потокобезопасен (не синхронизирован)
 *  *    - Для многопоточности требуется внешняя синхронизация
 *  *    - В данном приложении все операции выполняются в JavaFX Application Thread
 *
 * @author Артём Томских
 * @version 1.0
 */
public class PersonRepository {

    // ==================== СТАТИЧЕСКИЕ ПОЛЯ ====================

    /**
     * Единственный экземпляр класса (Singleton)
     * Статическая переменная хранит ссылку на единственный экземпляр репозитория
     */
    private static PersonRepository instance;

    // ==================== ПОЛЯ ДАННЫХ ====================

    /**
     * Наблюдаемый список объектов Person
     * ObservableList автоматически уведомляет UI об изменениях (добавление, удаление, обновление)
     * Позволяет TableView автоматически обновляться при изменении данных
     */
    private final ObservableList<Person> persons = FXCollections.observableArrayList();

    /**
     * Множество для хранения всех ID
     * Используется для быстрой проверки уникальности ID (O(1) сложность)
     * Не позволяет существовать двум записям с одинаковым ID
     */
    private final Set<Integer> ids = new HashSet<>();

    // ==================== КОНСТРУКТОР ====================

    /**
     * Приватный конструктор (Singleton pattern)
     * Запрещает создание объекта извне, обеспечивая единственный экземпляр
     */
    private PersonRepository() {}

    // ==================== ПУБЛИЧНЫЕ МЕТОДЫ ====================

    /**
     * Получение наблюдаемого списка всех персон
     *
     * @return ObservableList<Person> - наблюдаемый список для привязки к TableView
     */
    public ObservableList<Person> getObservablePersons() {
        return persons;
    }

    /**
     * Метод для получения единственного экземпляра репозитория (Singleton pattern)
     * Реализует ленивую инициализацию - экземпляр создаётся при первом обращении
     *
     * @return PersonRepository - единственный экземпляр репозитория
     */
    public static PersonRepository getInstance() {
        if (instance == null) {
            instance = new PersonRepository();
        }
        return instance;
    }

    /**
     * Добавление персоны в репозиторий
     *
     * Выполняет следующие операции:
     * 1. Проверяет уникальность ID (не должен существовать)
     * 2. Добавляет Person в наблюдаемый список
     * 3. Добавляет ID в множество для быстрого поиска
     * 4. Сортирует список для поддержания порядка
     *
     * @param person объект Person для добавления
     * @throws IllegalArgumentException если Person с таким ID уже существует
     */
    public void addPerson(Person person) {
        // Проверка уникальности ID
        if (ids.contains(person.getId())) {
            throw new IllegalArgumentException("Дублирование ID: " + person.getId());
        }

        // Добавление в коллекции
        persons.add(person);
        ids.add(person.getId());

        // Сортировка для поддержания порядка
        sortPersons();
    }

    /**
     * Сортировка списка персон по ID в порядке возрастания
     * Использует стандартный Comparator для примитивных типов int
     */
    private void sortPersons() {
        // FXCollections.sort - специальный метод для сортировки ObservableList
        // Comparator.comparingInt - создаёт компаратор для сортировки по int полю
        FXCollections.sort(persons, Comparator.comparingInt(Person::getId));
    }

    /**
     * Получение списка всех персон, отсортированного по ID
     *
     * Важно: метод возвращает новый отсортированный список, не изменяя исходный
     * Исходный список persons остаётся в исходном состоянии (но обычно он тоже отсортирован)
     *
     * @return ObservableList<Person> - отсортированный список всех персон
     */
    public ObservableList<Person> getAllPersons() {
        // persons.sorted() - возвращает новый список, отсортированный по указанному компаратору
        return persons.sorted(Comparator.comparingInt(Person::getId));
    }

    /**
     * Проверка уникальности ID
     * Используется в AddLineController для валидации вводимых данных
     *
     * @param id проверяемый идентификатор
     * @return true если ID уже существует в репозитории, false если уникальный
     */
    public boolean containsId(int id) {
        return ids.contains(id);
    }

    /**
     * Удаление персоны из репозитория
     *
     * Выполняет две операции:
     * 1. Удаляет объект Person из основного списка
     * 2. Удаляет ID персоны из множества уникальных идентификаторов
     *
     * @param person объект Person для удаления
     */
    public void deletePerson(Person person) {
        persons.remove(person);   // Удаление из наблюдаемого списка
        ids.remove(person.getId()); // Удаление ID из множества
    }

    /**
     * Сохранение данных в текстовый файл
     *
     * Формат файла:
     * - Каждая запись сохраняется в отдельной строке
     * - Поля разделяются символом ";"
     * - Порядок полей: ID;Name;Surname;Email;Phone
     *
     * Пример строки: "1;Иван;Петров;ivan@mail.ru;+7-911-123-45-67"
     *
     * @param file файл для сохранения
     * @throws IOException при ошибках ввода-вывода
     */
    public void saveToFile(File file) throws IOException {
        // try-with-resources - автоматическое закрытие потока после выполнения
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Перебор всех записей и запись в файл
            for (Person person : persons) {
                // String.format - форматирование строки с разделителями
                // %d - целое число (ID)
                // %s - строка (все остальные поля)
                // \n - символ новой строки
                writer.write(String.format("%d;%s;%s;%s;%s\n",
                        person.getId(),
                        person.getName(),
                        person.getSurname(),
                        person.getEmail(),
                        person.getPhone()));
            }
        }
        // Блок try автоматически закрывает writer, даже при возникновении исключения
    }

    /**
     * Загрузка данных из текстового файла
     *
     * Алгоритм загрузки:
     * 1. Очищает текущие данные в репозитории
     * 2. Читает файл построчно
     * 3. Разбивает строку по разделителю ";"
     * 4. Проверяет корректность формата (ровно 5 полей)
     * 5. Создаёт объект Person из полученных данных
     * 6. Добавляет запись через метод addPerson (с проверкой уникальности ID)
     *
     * @param file файл для загрузки
     * @throws IOException при ошибках чтения файла
     * @throws NumberFormatException если ID не является числом
     */
    public void loadFromFile(File file) throws IOException {
        // Очищаем все существующие данные
        persons.clear();
        ids.clear();

        // try-with-resources для автоматического закрытия BufferedReader
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line; // Переменная для хранения текущей строки

            // Чтение файла построчно
            while ((line = reader.readLine()) != null) {
                // Разделение строки на части по символу ";"
                String[] parts = line.split(";");

                // Проверка: строка должна содержать ровно 5 полей
                if (parts.length == 5) {
                    // Создание объекта Person из полей и добавление в репозиторий
                    addPerson(new Person(
                            Integer.parseInt(parts[0]), // ID - преобразуем строку в число
                            parts[1],                    // Имя
                            parts[2],                    // Фамилия
                            parts[3],                    // Email
                            parts[4]                     // Телефон
                    ));
                }
                // Если формат строки неверный - пропускаем (можно добавить логирование)
            }
        }
    }

    /**
     * Полная очистка репозитория
     * Удаляет все записи из списка и очищает множество ID
     * После вызова этого метода репозиторий становится пустым
     */
    public void clearAll() {
        persons.clear(); // Очистка наблюдаемого списка
        ids.clear();     // Очистка множества ID
    }
}