package com.example.plantpilot;

import androidx.room.TypeConverter;

import java.time.LocalTime;

public class Converters {
    @TypeConverter
    public static LocalTime fromString(String value) {
        return value == null ? null : LocalTime.parse(value);
    }

    @TypeConverter
    public static String localTimeToString(LocalTime localTime) {
        return localTime == null ? null : localTime.toString();
    }
}