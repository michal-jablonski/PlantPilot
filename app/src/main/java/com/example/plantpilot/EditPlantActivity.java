package com.example.plantpilot;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;

public class EditPlantActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_PLANT_NAME = "pb.edu.pl.EDIT_PLANT_NAME";
    public static final String EXTRA_EDIT_PLANT_DESCRIPTION = "pb.edu.pl.EDIT_PLANT_DESCRIPTION";
    private EditText editNameEditText;
    private EditText editDescriptionEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);
        editNameEditText = findViewById(R.id.edit_plant_name);
        editDescriptionEditText = findViewById(R.id.edit_plant_description);

        if(getIntent().hasExtra(EXTRA_EDIT_PLANT_NAME)){
            String title = getIntent().getStringExtra(EXTRA_EDIT_PLANT_NAME);
            editNameEditText.setText(title);
        }

        if (getIntent().hasExtra(EXTRA_EDIT_PLANT_DESCRIPTION)) {
            String author = getIntent().getStringExtra(EXTRA_EDIT_PLANT_DESCRIPTION);
            editDescriptionEditText.setText(author);
        }

        final Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(editNameEditText.getText())
                    || TextUtils.isEmpty(editDescriptionEditText.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                String title = editNameEditText.getText().toString();
                replyIntent.putExtra(EXTRA_EDIT_PLANT_NAME, title);
                String author = editDescriptionEditText.getText().toString();
                replyIntent.putExtra(EXTRA_EDIT_PLANT_DESCRIPTION, author);
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }
}
