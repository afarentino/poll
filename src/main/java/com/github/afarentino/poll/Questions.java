package com.github.afarentino.poll;

public class Questions {
    public final int KEYS = 14;
    public final int VALUES = 5;
    private String firstName;
    private String lastName;
    private String email;

    private String[][] checkboxes;
    private String[] keys;

    public String getFirstName() { return this.firstName; }
    public void setFirstName(String name) { this.firstName = name; }

    public String getLastName() { return this.lastName; }
    public void setLastName(String val) { this.lastName = val; }

    public Questions() {
        this.checkboxes = new String[KEYS][VALUES];  // Spring MVC will fill these at runtime when form is bound
        this.keys = new String[KEYS];
    }
    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public String[][] getCheckboxes() {
        return this.checkboxes;
    }

    public void setCheckboxes(String[][] val) {
        this.checkboxes = val;
    }

    public String[] getKeys() {
        return this.keys;
    }

    public String keyAt(int index) {
        return keys[index];
    }
    public void setTimes(String[] values) {
        this.keys = values;
    }




}
