package com.example.plantpilot;

public enum Weekday {

    MONDAY(1),
    TUESDAY(2),
    WEDNESDAY(3),
    THURSDAY(4),
    FRIDAY(5),
    SATURDAY(6),
    SUNDAY(7);


    public final int value;

    private Weekday(int value) {
        this.value = value;
    }
}
