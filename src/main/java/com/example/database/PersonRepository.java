package com.example.database;
///  @author Артём Томских

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


///  Класс реализует шаблон Одиночка, хранит данные объектов person в наблюдаемом списке
/// @author Артём Томских
public class PersonRepository {
    // Статическая переменная instance хранит ссылку на единственный экземпляр.
    // Приватный конструктор запрещает создание объекта извне:
    private static PersonRepository instance;
    private final ObservableList<Person> persons = FXCollections.observableArrayList();
    private final Set<Integer> ids = new HashSet<>();

    //Приватный конструктор
    private PersonRepository() {}

    /**Метод получения наблюдаемого списка*/
    public ObservableList<Person> getObservablePersons() {
        return persons;
    }

    /** Метод для получения единственного экземпляра шаблон класса Одиночка*/
    public static PersonRepository getInstance() {
        if (instance == null) {
            instance = new PersonRepository();
        }
        return instance;
    }

    /**
     * Добавление персоны с автоматической сортировкой
     * @param person это создаваемый объект класса Person с ново-заданными параметрами
     */
    public void addPerson(Person person) {
        if (ids.contains(person.getId())){
            throw new IllegalArgumentException("Дублирование ID");
        }
        persons.add(person);
        ids.add(person.getId());
        sortPersons();
    }

    /** Сортировка списка */
    private void sortPersons() {
        FXCollections.sort(persons, Comparator.comparingInt(Person::getId));
    }

    /**
     * Получение списка с сортировкой по ID
     * Возвращает новый отсортированный список на основе исходного списка
     */
    public ObservableList<Person> getAllPersons() {
        return persons.sorted(Comparator.comparingInt(Person::getId));
    }

    /**
     * Метод для проверки уникальности id в AddLineController
     * @param id это проверяемый параметр нового объекта
     */
    public boolean containsId(int id) {
        return ids.contains(id);

    }

    /** Удаление персоны из репозитория:
     *1. Удаляет объект Person из основного списка
     *2. Удаляет ID персоны из множества уникальных идентификаторов */
    public void deletePerson(Person person) {
        persons.remove(person);
        ids.remove(person.getId());
    }

    /** Сохранение данных в файл:
    * - Использует BufferedWriter для эффективной записи
    * - Формат данных: ID;Name;Surname;Email;Phone
    * - Каждая запись сохраняется в отдельной строке */
    public void saveToFile(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Person person : persons) {
                writer.write(String.format("%d;%s;%s;%s;%s\n",
                        person.getId(),
                        person.getName(),
                        person.getSurname(),
                        person.getEmail(),
                        person.getPhone()));
            }
        }
    }

    /** Загрузка данных из файла:
    1. Очищает текущие данные в репозитории
    2. Читает файл построчно с помощью BufferedReader
    3. Парсит строки по разделителю ";"
    4. Проверяет корректность формата данных (ровно 5 полей)
    5. Добавляет новые записи через метод addPerson (с проверкой уникальности ID) */
    public void loadFromFile(File file) throws IOException {
        persons.clear();
        ids.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 5) {
                    addPerson(new Person(
                            Integer.parseInt(parts[0]),
                            parts[1],
                            parts[2],
                            parts[3],
                            parts[4]
                    ));
                }
            }
        }
    }

    /** Полная очистка репозитория:
    * Удаляет все записи из основного списка
    * Множество ids очищается автоматически при следующем добавлении */
    public void clearAll() {
        persons.clear();
        ids.clear();
    }
}

