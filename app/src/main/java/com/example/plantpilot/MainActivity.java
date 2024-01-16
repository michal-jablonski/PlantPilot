package com.example.plantpilot;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
            Intent intent = new Intent(MainActivity.this, EditPlantActivity.class);
            startActivityForResult(intent, NEW_PLANT_ACTIVITY_REQUEST_CODE);
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        final PlantAdapter adapter = new PlantAdapter();
        recyclerView.setAdapter(adapter);
        setupSwipeToDelete(recyclerView);
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
        if (requestCode == NEW_PLANT_ACTIVITY_REQUEST_CODE & resultCode == RESULT_OK) {
            Plant Plant = new Plant(data.getStringExtra(EditPlantActivity.EXTRA_EDIT_PLANT_NAME),
                    data.getStringExtra(EditPlantActivity.EXTRA_EDIT_PLANT_DESCRIPTION));
            plantViewModel.insert(Plant);
            Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.item_added),
                    Snackbar.LENGTH_LONG).show();
        } else if (requestCode == EDIT_REQUEST_ACTIVITY_REQUEST_CODE & resultCode == RESULT_OK) {
            editedPlant.setName(data.getStringExtra(EditPlantActivity.EXTRA_EDIT_PLANT_NAME));
            editedPlant.setDescription(data.getStringExtra(EditPlantActivity.EXTRA_EDIT_PLANT_DESCRIPTION));

            plantViewModel.update(editedPlant);
            Snackbar.make(findViewById(R.id.coordinator_layout), getString(R.string.item_updated),
                    Snackbar.LENGTH_LONG).show();
            editedPlant = null;
        } else {
            Snackbar.make(findViewById(R.id.coordinator_layout),
                            getString(R.string.empty_not_saved),
                            Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    private class PlantHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener, View.OnClickListener {
        private final TextView plantNameTextView;
        private final TextView plantDescriptionTextView;

        private Plant plant;

        public PlantHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.plant_list_item, parent, false));
            plantNameTextView = itemView.findViewById(R.id.plant_title);
            plantDescriptionTextView = itemView.findViewById(R.id.plant_author);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Plant Plant) {
            this.plant = Plant;
            plantNameTextView.setText(Plant.getName());
            plantDescriptionTextView.setText(Plant.getDescription());
        }

        @Override
        public void onClick(View v) {
            editedPlant = plant;
            Intent intent = new Intent(MainActivity.this, EditPlantActivity.class);
            intent.putExtra(EditPlantActivity.EXTRA_EDIT_PLANT_NAME, plant.getName());
            intent.putExtra(EditPlantActivity.EXTRA_EDIT_PLANT_DESCRIPTION, plant.getDescription());
            startActivityForResult(intent, EDIT_REQUEST_ACTIVITY_REQUEST_CODE);
        }

        @Override
        public boolean onLongClick(View v) {
             new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.confirm_deletion_title)
                    .setMessage(R.string.confirm_deletion_question)
                    .setPositiveButton(R.string.yes, (dialog, which) -> {
                        plantViewModel.delete(plant);
                        Snackbar.make(findViewById(R.id.coordinator_layout), R.string.item_removed, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.no, (dialog, which) -> dialog.dismiss())
                    .show();

            return true;
        }
    }

    private void setupSwipeToDelete(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
                0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT
        ) {
            @Override
            public boolean onMove(
                    @NonNull RecyclerView recyclerView,
                    @NonNull RecyclerView.ViewHolder viewHolder,
                    @NonNull RecyclerView.ViewHolder target
            ) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT || direction == ItemTouchHelper.RIGHT) {
                    Snackbar.make(findViewById(R.id.coordinator_layout), R.string.plant_archived, Snackbar.LENGTH_SHORT).show();
                }
            }

            @Override
            public float getSwipeThreshold(RecyclerView.ViewHolder viewHolder) {
                return 0.5f;
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
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
                Plant Plant = plants.get(position);
                holder.bind(Plant);
            } else {
                Log.d("MainActivity", "No Plants");
            }
        }

        @Override
        public int getItemCount() {
            if (plants != null) {
                return plants.size();
            } else {
                return 0;
            }
        }

        void setPlants(List<Plant> plants) {
            this.plants = plants;
            notifyDataSetChanged();
        }
    }
}