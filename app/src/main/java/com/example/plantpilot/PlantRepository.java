package com.example.plantpilot;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PlantRepository {
    private final PlantDao plantDao;
    private final LiveData<List<Plant>> plants;

    PlantRepository(Application application) {
        PlantDatabase database = PlantDatabase.getDatabase(application);
        plantDao = database.plantDao();
        plants = plantDao.findAll();
    }


    LiveData<List<Plant>> findAllPlants() {
        return plants;
    }

    void insert(Plant plant) {
        PlantDatabase.databaseWriteExecutor.execute(() -> plantDao.insert(plant));
    }

    void update(Plant plant) {
        PlantDatabase.databaseWriteExecutor.execute(() -> plantDao.update(plant));
    }

    void delete(Plant plant) {
        PlantDatabase.databaseWriteExecutor.execute(() -> plantDao.delete(plant));
    }
}