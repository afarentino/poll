package com.github.afarentino.poll;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
@Document("entries")
public class Entry {
    @Id
    private String id;
    private String timeStamp;
    private String firstName;
    private String lastName;
    private String email;
    private List<TimeSlot> timeSlots;

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Entry(final String timeStamp, final String firstName, final String lastName, final String email,
                 final List<TimeSlot> timeSlots) {
        this.timeStamp = timeStamp;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.timeSlots = timeSlots;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<TimeSlot> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(List<TimeSlot> timeSlots) {
        this.timeSlots = timeSlots;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "id='" + id + '\'' +
                ", timeStamp='" + timeStamp + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", timeSlots=" + timeSlots +
                '}';
    }
}
