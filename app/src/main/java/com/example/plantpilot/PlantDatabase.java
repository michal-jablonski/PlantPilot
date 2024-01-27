package com.example.plantpilot;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.time.LocalTime;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {Plant.class}, version = 2, exportSchema = false)
@TypeConverters({Converters.class})
public abstract class PlantDatabase extends RoomDatabase {
    private static PlantDatabase databaseInstance;
    static final ExecutorService databaseWriteExecutor = Executors.newSingleThreadExecutor();

    public abstract PlantDao plantDao();

    static PlantDatabase getDatabase(final Context context) {
        if (databaseInstance == null) {
            databaseInstance = Room.databaseBuilder(context.getApplicationContext(),
                            PlantDatabase.class, "plant_database")
                    .addCallback(roomDatabaseCallback)
                    .addMigrations(MIGRATION_1_2)
                    .build();
        }
        return databaseInstance;
    }

    private static final RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            databaseWriteExecutor.execute(() -> {
                PlantDao dao = databaseInstance.plantDao();
                Plant plant = new Plant("Monstera Deliciosa", "My fav plant", Weekday.Sunday, LocalTime.now().plusSeconds(30));

                dao.insert(plant);
            });
        }
    };

    static final Migration MIGRATION_1_2 = new Migration1();
}