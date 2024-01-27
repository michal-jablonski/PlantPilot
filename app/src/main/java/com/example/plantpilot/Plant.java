package com.example.plantpilot;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Time;
import java.time.LocalTime;


@Entity(tableName = "plant")
public class Plant {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;
    private final Weekday wateringDay;
    private final LocalTime wateringTime;


    public Plant(String name, String description, Weekday wateringDay, LocalTime wateringTime) {
        this.name = name;
        this.description = description;
        this.wateringDay = wateringDay;
        this.wateringTime = wateringTime;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Weekday getWateringDay() {
        return wateringDay;
    }

    public LocalTime getWateringTime() {
        return wateringTime;
    }
}