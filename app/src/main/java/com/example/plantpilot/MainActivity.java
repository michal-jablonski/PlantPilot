package com.example.plantpilot;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plantpilot.Services.ImageSaver;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.time.LocalTime;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int NEW_PLANT_ACTIVITY_REQUEST_CODE = 1;
    public static final int EDIT_REQUEST_ACTIVITY_REQUEST_CODE = 2;
    private PlantViewModel plantViewModel;
    private Plant editedPlant;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton addPlantButton = findViewById(R.id.add_button);
        addPlantButton.setOnClickListener(view -> {
            startEditPlantActivity(NEW_PLANT_ACTIVITY_REQUEST_CODE);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final PlantAdapter adapter = new PlantAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        plantViewModel = new ViewModelProvider(this).get(PlantViewModel.class);
        plantViewModel.findAll().observe(this, adapter::setPlants);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == NEW_PLANT_ACTIVITY_REQUEST_CODE || requestCode == EDIT_REQUEST_ACTIVITY_REQUEST_CODE) && resultCode == RESULT_OK) {
            handlePlantActivityResult(requestCode, data);
        } else {
            showSnackbar(getString(R.string.empty_not_saved));
        }
    }

    private void handlePlantActivityResult(int requestCode, Intent data) {
        Plant plant = createPlantFromIntentData(data);
        NotificationScheduler.scheduleWateringJobForPlant(this, plant);
        Bitmap imageBitmap = data.getParcelableExtra(EditPlantActivity.EXTRA_EDIT_PLANT_IMAGE_BITMAP);
        if (requestCode == NEW_PLANT_ACTIVITY_REQUEST_CODE) {
            plantViewModel.insert(plant);
            ImageSaver imageSaver = new ImageSaver(getApplicationContext());
            imageSaver.setFileName(plant.getImageId() + ".png")
                    .setDirectoryName("images")
                    .save(imageBitmap);
            showSnackbar(getString(R.string.item_added));
        } else if (requestCode == EDIT_REQUEST_ACTIVITY_REQUEST_CODE) {
            ImageSaver imageSaver = new ImageSaver(getApplicationContext());
            imageSaver.setFileName(editedPlant.getImageId() + ".png")
                    .setDirectoryName("images")
                    .save(imageBitmap);
            updatePlantAndView(plant);
            showSnackbar(getString(R.string.item_updated));
        }
    }

    private Plant createPlantFromIntentData(Intent data) {
        return new Plant(
                data.getStringExtra(EditPlantActivity.EXTRA_EDIT_PLANT_NAME),
                data.getStringExtra(EditPlantActivity.EXTRA_EDIT_PLANT_DESCRIPTION),
                (Weekday) data.getSerializableExtra(EditPlantActivity.EXTRA_EDIT_PLANT_WEEK_DAY),
                (LocalTime) data.getSerializableExtra(EditPlantActivity.EXTRA_EDIT_PLANT_TIME)
        );
    }

    private void updatePlantAndView(Plant plant) {
        editedPlant.setName(plant.getName());
        editedPlant.setDescription(plant.getDescription());
        editedPlant.setWateringDay(plant.getWateringDay());
        editedPlant.setWateringTime(plant.getWateringTime());
        plantViewModel.update(editedPlant);
        editedPlant = null;
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(R.id.coordinator_layout), message, Snackbar.LENGTH_LONG).show();
    }

    private void startEditPlantActivity(int requestCode) {
        Intent intent = new Intent(MainActivity.this, EditPlantActivity.class);
        if (requestCode == EDIT_REQUEST_ACTIVITY_REQUEST_CODE && editedPlant != null) {
            intent.putExtra(EditPlantActivity.EXTRA_EDIT_PLANT_NAME, editedPlant.getName());
            intent.putExtra(EditPlantActivity.EXTRA_EDIT_PLANT_DESCRIPTION, editedPlant.getDescription());
            intent.putExtra(EditPlantActivity.EXTRA_EDIT_PLANT_IMAGE_BITMAP, new ImageSaver(MainActivity.this)
                    .setFileName(editedPlant.getImageId() + ".png")
                    .setDirectoryName("images")
                    .load());
            intent.putExtra(EditPlantActivity.EXTRA_EDIT_PLANT_WEEK_DAY, editedPlant.getWateringDay());
            intent.putExtra(EditPlantActivity.EXTRA_EDIT_PLANT_TIME, editedPlant.getWateringTime());
        }

        startActivityForResult(intent, requestCode);
    }

    private class PlantHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private final TextView plantNameTextView;
        private final TextView plantDescriptionTextView;
        private final TextView plantWateringDayTextView;
        private final TextView plantWateringTimeTextView;
        private Plant plant;

        public PlantHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.plant_list_item, parent, false));
            plantNameTextView = itemView.findViewById(R.id.plant_title);
            plantDescriptionTextView = itemView.findViewById(R.id.plant_description);
            plantWateringDayTextView = itemView.findViewById(R.id.plant_watering_day);
            plantWateringTimeTextView = itemView.findViewById(R.id.plant_watering_time);
            ImageButton plantExploreButton = itemView.findViewById(R.id.info_button);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            plantExploreButton.setOnClickListener(v ->
                    PlantInformationProvider.openYouTubeAppWithPlantName(v, plant.getName()));
        }

        public void bind(Plant plant) {
            this.plant = plant;
            plantNameTextView.setText(plant.getName());
            plantDescriptionTextView.setText(plant.getDescription());
            plantWateringDayTextView.setText(plant.getWateringDay().toString());
            plantWateringTimeTextView.setText(plant.getWateringTime().toString());
        }

        @Override
        public void onClick(View v) {
            editedPlant = plant;
            startEditPlantActivity(EDIT_REQUEST_ACTIVITY_REQUEST_CODE);
        }

        @Override
        public boolean onLongClick(View v) {
            editedPlant = plant;
            showDeleteConfirmationDialog();
            return true;
        }
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.confirm_deletion_title)
                .setMessage(R.string.confirm_deletion_question)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    deletePlantAndShowSnackbar();
                })
                .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void deletePlantAndShowSnackbar() {
        plantViewModel.delete(editedPlant);
        Snackbar.make(findViewById(R.id.coordinator_layout), R.string.item_removed, Toast.LENGTH_SHORT).show();
    }

    private class PlantAdapter extends RecyclerView.Adapter<PlantHolder> {
        private List<Plant> plants;

        @NonNull
        @Override
        public PlantHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PlantHolder(getLayoutInflater(), parent);
        }

        @Override
        public void onBindViewHolder(@NonNull PlantHolder holder, int position) {
            if (plants != null) {
                Plant plant = plants.get(position);
                holder.bind(plant);
            } else {
                Log.d("MainActivity", "No Plants");
            }
        }

        @Override
        public int getItemCount() {
            return plants != null ? plants.size() : 0;
        }

        void setPlants(List<Plant> plants) {
            this.plants = plants;
            notifyDataSetChanged();
        }
    }
}
