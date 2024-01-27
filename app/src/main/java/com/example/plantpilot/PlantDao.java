package com.example.plantpilot;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import java.util.List;

@Dao
public interface PlantDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Plant plant);

    @Update
    void update(Plant plant);

    @Delete
    void delete(Plant plant);

    @Query("DELETE FROM Plant")
    void deleteAll();

    @Query("SELECT * FROM Plant ORDER BY name")
    LiveData<List<Plant>> findAll();

    @Query("SELECT * FROM Plant WHERE name LIKE :name")
    List<Plant> findPlantByName(String name);
}
