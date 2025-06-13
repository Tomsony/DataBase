package com.example.database;

import javafx.beans.property.*;

///Класс для создания экземпляра Person и связи данных с JavaFX элементами
/// @author Артём Томских
public class Person {
    private final IntegerProperty id;
    private final StringProperty name;
    private final StringProperty surname;
    private final StringProperty email;
    private final StringProperty phone;

    // Конструктор
    public Person(int id, String name, String surname, String email, String phone){
        this.id = new SimpleIntegerProperty(id);
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
        this.email = new SimpleStringProperty(email);
        this.phone = new SimpleStringProperty(phone);
    }

    // Геттеры для значений
    public int getId() { return id.get(); }
    public String getName() { return name.get(); }
    public String getSurname() { return surname.get(); }
    public String getEmail() { return email.get(); }
    public String getPhone() { return phone.get(); }

    // Сеттеры
    public void setId(int id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setSurname(String surname) { this.surname.set(surname); }
    public void setEmail(String email) { this.email.set(email); }
    public void setPhone(String phone) { this.phone.set(phone); }
}
