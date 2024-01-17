package com.example.plantpilot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.plantpilot.Services.Api.*;
import com.google.android.material.snackbar.Snackbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;

public class EditPlantActivity extends AppCompatActivity {
    public static final String EXTRA_EDIT_PLANT_NAME = "pb.edu.pl.EDIT_PLANT_NAME";
    public static final String EXTRA_EDIT_PLANT_DESCRIPTION = "pb.edu.pl.EDIT_PLANT_DESCRIPTION";
    public static final int REQUEST_IMAGE_CAPTURE = 101;

    private EditText editNameEditText;
    private EditText editDescriptionEditText;
    private ImageView capturedImageView;
    private Button captureImageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);

        initViews();

        handleIntentExtras();

        setupSaveButton();

        setupCaptureImageButton();
    }

    private void initViews() {
        editNameEditText = findViewById(R.id.edit_plant_name);
        editDescriptionEditText = findViewById(R.id.edit_plant_description);
        capturedImageView = findViewById(R.id.captured_plant_image);
    }

    private void handleIntentExtras() {
        if (getIntent().hasExtra(EXTRA_EDIT_PLANT_NAME)) {
            editNameEditText.setText(getIntent().getStringExtra(EXTRA_EDIT_PLANT_NAME));
        }

        if (getIntent().hasExtra(EXTRA_EDIT_PLANT_DESCRIPTION)) {
            editDescriptionEditText.setText(getIntent().getStringExtra(EXTRA_EDIT_PLANT_DESCRIPTION));
        }
    }

    private void setupSaveButton() {
        Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = new Intent();
            if (TextUtils.isEmpty(editNameEditText.getText()) || TextUtils.isEmpty(editDescriptionEditText.getText())) {
                setResult(RESULT_CANCELED, replyIntent);
            } else {
                replyIntent.putExtra(EXTRA_EDIT_PLANT_NAME, editNameEditText.getText().toString());
                replyIntent.putExtra(EXTRA_EDIT_PLANT_DESCRIPTION, editDescriptionEditText.getText().toString());
                setResult(RESULT_OK, replyIntent);
            }
            finish();
        });
    }

    private void setupCaptureImageButton() {
        captureImageButton = findViewById(R.id.capture_plant_image_button);
        captureImageButton.setOnClickListener(view -> {
            Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
            startActivityForResult(openCameraIntent, REQUEST_IMAGE_CAPTURE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            processCapturedImage((Bitmap) data.getExtras().get("data"));
        }
    }

    private void processCapturedImage(Bitmap capturedImage) {
        capturedImageView.setImageBitmap(capturedImage);
        captureImageButton.setText(R.string.button_capture_image_taken);

        PlantApi plantApi = RetrofitInstance.getRetrofitInstance().create(PlantApi.class);
        PlantApiRequest plantApiRequest = new PlantApiRequest();
        String encodedImage = bitMapAsBase64(capturedImage);
        plantApiRequest.images.add("data:image/jpg;base64," + encodedImage);
        plantApiRequest.latitude = 0.0;
        plantApiRequest.longitude = 0.0;

        Call<PlantApiResponse> plantApiCall = plantApi.identify(plantApiRequest, getString(R.string.apikey));

        plantApiCall.enqueue(new Callback<PlantApiResponse>() {

            @Override
            public void onResponse(Call<PlantApiResponse> call, Response<PlantApiResponse> response) {
                handlePlantApiResponse(response);
            }

            @Override
            public void onFailure(Call<PlantApiResponse> call, Throwable t) {
                Snackbar.make(findViewById(R.id.edit_plant_activity), "Something went wrong: " + t.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void handlePlantApiResponse(Response<PlantApiResponse> response) {
        Log.d("PlantApi", "onResponse: " + response);
        if (response.isSuccessful() && response.body() != null) {
            Result plantIdentification = response.body().result;
            handlePlantIdentification(plantIdentification);
        } else {
            Snackbar.make(findViewById(R.id.edit_plant_activity), "Failed to fetch plant info", Snackbar.LENGTH_LONG).show();
        }
    }

    private void handlePlantIdentification(Result plantIdentification) {
        Suggestion plantSuggestion = plantIdentification.classification.suggestions.get(0);

        if (!plantIdentification.is_plant.binary) {
            Snackbar.make(findViewById(R.id.edit_plant_activity), "Image does not contain plant", Snackbar.LENGTH_LONG).show();
        } else {
            editNameEditText.setText(plantSuggestion.name);
        }
    }

    private static String bitMapAsBase64(Bitmap capturedImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
