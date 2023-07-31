package com.assignment2;

/**
 * Created by arulsmv on 26/7/23.
 */
// Main only for simple testing.
public class Main {
    public static void main(String[] args) {
        Person person = new Person("person", 45);
        Employee employee = new Employee("person", 45, "role1");

        try {
        System.out.println("person "+person.checksum());
        System.out.println("employee "+employee.checksum());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
