package com.example.plantpilot;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

public class Migration1 extends Migration {

    public Migration1() {
        super(1, 2);
    }

    @Override
    public void migrate(SupportSQLiteDatabase database) {
        database.execSQL("ALTER TABLE Plant ADD COLUMN wateringDay TEXT");
        database.execSQL("ALTER TABLE Plant ADD COLUMN wateringTime TEXT");
    }
}
