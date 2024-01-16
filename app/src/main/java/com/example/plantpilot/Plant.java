package com.example.plantpilot;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plant")
public class Plant {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String description;

    public Plant(String name, String description) {
        this.name = name;
        this.description = description;
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
}