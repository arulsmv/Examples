package com.assignment2;

import org.testng.annotations.*;

//import static org.junit.Assert.assertEquals;

import static org.testng.AssertJUnit.*;



/**
 * Created by arulsmv on 26/7/23.
 */
public class PersonEmployeeCheckSumTest {
    @Test
    public void testPersonEmployee() throws Throwable {

        Person person = new Person("person", 45);
        Employee employee = new Employee("person", 45, "role1");

        Person person1 = new Person("person", 45);
        Employee employee1 = new Employee("person", 45, "role1");
        Employee employee2 = new Employee("person2", 45, "role1");

        assertEquals(person.checksum(), person1.checksum());
        assertEquals(employee.checksum(), employee.checksum());
        assertEquals(person.equals(person1), true);
        assertEquals(employee.equals(employee1), true);
        assertEquals(employee.equals(person), false);
        assertEquals(employee.equals(employee2), false);

    }

}
