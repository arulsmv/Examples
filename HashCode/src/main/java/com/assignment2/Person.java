package com.assignment2;

import org.apache.commons.codec.digest.DigestUtils;


/**
 * Created by arulsmv on 26/7/23.
 */
public class Person {
    private final int age;
    private final String name;

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
        c= new CheckSum();  // Edited by arulsmv
    }

    // Code added by arulsmv
    // One implemenation using reflexion so this CheckSum c is only part of thie base class.
    private CheckSum c;

    public String checksum() throws Exception {
       return   c.md5(this);
    }

    public boolean equals(Person other) throws Exception {
        return this.checksum().equals(other.checksum());
    }

    // a simple Alternative
    public String Sha256() {
        return DigestUtils.sha256Hex("name:"+name+"age:"+ Integer.toString(age));
    }

}
