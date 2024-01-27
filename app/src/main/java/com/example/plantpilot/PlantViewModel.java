package com.example.plantpilot;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class PlantViewModel extends AndroidViewModel {
    private final PlantRepository plantRepository;
    private final LiveData<List<Plant>> plants;

    public PlantViewModel(@NonNull Application application) {
        super(application);
        plantRepository = new PlantRepository(application);
        plants = plantRepository.findAllPlants();
    }

    public LiveData<List<Plant>> findAll() {
        return plants;
    }

    public void insert(Plant plant) {
        plantRepository.insert(plant);
    }

    public void update(Plant plant) {
        plantRepository.update(plant);
    }

    public void delete(Plant plant) {
        plantRepository.delete(plant);
    }
}
