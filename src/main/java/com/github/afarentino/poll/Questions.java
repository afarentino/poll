package com.github.afarentino.poll;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Questions {
    private String firstName;
    private String lastName;
    private String email;

    private String[][] checkboxes;
    private String[] times;

    public String getFirstName() { return this.firstName; }
    public void setFirstName(String name) { this.firstName = name; }

    public String getLastName() { return this.lastName; }
    public void setLastName(String val) { this.lastName = val; }

    public Questions() {
        this.checkboxes = new String[2][5];
        this.times = new String[2];
    }
    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String[][] getCheckboxes() {
        return this.checkboxes;
    }

    public void setCheckboxes(String[][] val) {
        this.checkboxes = val;
    }

    public String[] getTimes() {
        return this.times;
    }

    public String timeAt(int index) {
        return times[index];
    }
    public void setTimes(String[] values) {
        this.times = values;
    }




}
