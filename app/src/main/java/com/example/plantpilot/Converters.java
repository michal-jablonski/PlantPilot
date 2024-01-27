package com.example.plantpilot;

import androidx.room.TypeConverter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Converters {
    @TypeConverter
    public Weekday toWeekday(int value) {
        return Weekday.values()[value - 1];
    }

    @TypeConverter
    public int fromWeekday(Weekday dayOfWeek) {
        return dayOfWeek.value;
    }

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_TIME;

    @TypeConverter
    public static LocalTime toLocalTime(String value) {
        return value == null ? null : LocalTime.parse(value, formatter);
    }

    @TypeConverter
    public static String fromLocalTime(LocalTime value) {
        return value == null ? null : value.format(formatter);
    }
}
