package com.github.afarentino.poll;


public class TimeSlot {
    private String header;
    private String value;

    @Override
    public String toString() {
        return "TimeSlot{" +
                "header='" + header + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

    public TimeSlot(final String header, final String value) {
        this.header = header;
        this.value = value;
    }

    public String getHeader() { return this.header; }
    public void setHeader(String h) {
        this.header = h;
    }
    public String getValue() { return this.value; }
    public void setValue(String v) {
        this.value = v;
    }

}
