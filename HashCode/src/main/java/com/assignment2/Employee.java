package com.assignment2;

import org.apache.commons.codec.digest.DigestUtils;


/**
 * Created by arulsmv on 26/7/23.
 */
public class Employee extends Person {
    private final String role;
    public Employee(String name, int age, String role) {
        super(name, age);
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    // code edited by arulsmv
    // a simple Alternative
    @Override
    public String Sha256() {
        return DigestUtils.sha256Hex("name:"+getName()+"age:"+ Integer.toString(getAge()) + "role" + role);
        //can also use super class sha
    }
}
