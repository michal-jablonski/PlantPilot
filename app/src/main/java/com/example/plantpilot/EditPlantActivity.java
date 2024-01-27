package com.example.plantpilot;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.plantpilot.Services.Api.*;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.ByteArrayOutputStream;
import java.time.LocalTime;

public class EditPlantActivity extends AppCompatActivity {
    private static final String TAG = "EditPlantActivity";
    public static final String EXTRA_EDIT_PLANT_NAME = "pb.edu.pl.EDIT_PLANT_NAME";
    public static final String EXTRA_EDIT_PLANT_DESCRIPTION = "pb.edu.pl.EDIT_PLANT_DESCRIPTION";
    public static final String EXTRA_EDIT_PLANT_IMAGE_BITMAP = "pb.edu.pl.EDIT_PLANT_IMAGE_BITMAP";
    public static final String EXTRA_EDIT_PLANT_WEEK_DAY = "pb.edu.pl.EDIT_PLANT_WEEK_DAY";
    public static final String EXTRA_EDIT_PLANT_TIME = "pb.edu.pl.EDIT_PLANT_TIME";
    public static final int REQUEST_IMAGE_CAPTURE = 101;

    private EditText editNameEditText;
    private EditText editDescriptionEditText;
    private ImageView capturedImageView;
    private Button captureImageButton;
    private Spinner wateringDaySpinner;
    private TimePicker wateringTimePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_plant);
        initViews();
        handleIntentExtras();
        setupSaveButton();
        setupCaptureImageButton();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    EditPlantActivity.this,
                    android.Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(EditPlantActivity.this,
                        new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        final Button btnScheduleNotification = findViewById(R.id.btnScheduleNotification);
        btnScheduleNotification.setOnClickListener(view -> NotificationScheduler.makeNotification(this, "Test title"));

        final Button btnYoutube = findViewById(R.id.btnYoutube);
        btnYoutube.setOnClickListener(view -> PlantInformationProvider.openYouTubeAppWithPlantName(view, "Monstera deliciosa"));
    }

    private void initViews() {
        editNameEditText = findViewById(R.id.edit_plant_name);
        editDescriptionEditText = findViewById(R.id.edit_plant_description);
        capturedImageView = findViewById(R.id.captured_plant_image);
        wateringDaySpinner = findViewById(R.id.spinner_weekday);
        wateringDaySpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Weekday.values()));
        wateringTimePicker = findViewById(R.id.timePicker_watering_time);
        captureImageButton = findViewById(R.id.capture_plant_image_button);
    }

    private void handleIntentExtras() {
        Intent intent = getIntent();
        setEditTextFromIntentExtra(intent, EXTRA_EDIT_PLANT_NAME, editNameEditText);
        setEditTextFromIntentExtra(intent, EXTRA_EDIT_PLANT_DESCRIPTION, editDescriptionEditText);

        if (intent.hasExtra(EXTRA_EDIT_PLANT_IMAGE_BITMAP)) {
            handlePlantImageBitmapExtra(intent);
        }

        setSpinnerSelectionFromIntentExtra(intent, EXTRA_EDIT_PLANT_WEEK_DAY, wateringDaySpinner, Weekday.values());

        if (intent.hasExtra(EXTRA_EDIT_PLANT_TIME)) {
            setLocalTimeFromIntentExtra(intent, EXTRA_EDIT_PLANT_TIME, wateringTimePicker);
        }
    }

    private void setEditTextFromIntentExtra(Intent intent, String extraKey, EditText editText) {
        if (intent.hasExtra(extraKey)) {
            editText.setText(intent.getStringExtra(extraKey));
        }
    }

    private void handlePlantImageBitmapExtra(Intent intent) {
        Bitmap bitmap = intent.getParcelableExtra(EXTRA_EDIT_PLANT_IMAGE_BITMAP);
        if (bitmap != null) {
            capturedImageView.setImageBitmap(bitmap);
            captureImageButton.setText(R.string.button_capture_image_taken);
        }
    }

    private <T extends Enum<T>> void setSpinnerSelectionFromIntentExtra(Intent intent, String extraKey, Spinner spinner, T[] values) {
        if (intent.hasExtra(extraKey)) {
            T enumValue = (T) intent.getSerializableExtra(extraKey);
            spinner.setSelection(enumValue.ordinal());
        }
    }

    private void setLocalTimeFromIntentExtra(Intent intent, String extraKey, TimePicker timePicker) {
        LocalTime time = (LocalTime) intent.getSerializableExtra(extraKey);
        timePicker.setHour(time.getHour());
        timePicker.setMinute(time.getMinute());
    }

    private void setupSaveButton() {
        Button button = findViewById(R.id.button_save);
        button.setOnClickListener(view -> {
            Intent replyIntent = createReplyIntent();
            handleSaveButtonClick(replyIntent);
        });
    }

    private Intent createReplyIntent() {
        Intent replyIntent = new Intent();
        if (TextUtils.isEmpty(editNameEditText.getText()) || TextUtils.isEmpty(editDescriptionEditText.getText())) {
            setResult(RESULT_CANCELED, replyIntent);
        } else {
            setReplyIntentExtras(replyIntent);
            setResult(RESULT_OK, replyIntent);
        }
        return replyIntent;
    }

    private void setReplyIntentExtras(Intent replyIntent) {
        replyIntent.putExtra(EXTRA_EDIT_PLANT_NAME, editNameEditText.getText().toString());
        replyIntent.putExtra(EXTRA_EDIT_PLANT_DESCRIPTION, editDescriptionEditText.getText().toString());
        if (capturedImageView.getDrawable() != null) {
            replyIntent.putExtra(EXTRA_EDIT_PLANT_IMAGE_BITMAP, ((BitmapDrawable) capturedImageView.getDrawable()).getBitmap());
        }
        replyIntent.putExtra(EXTRA_EDIT_PLANT_WEEK_DAY, (Weekday) wateringDaySpinner.getSelectedItem());
        LocalTime time = LocalTime.of(wateringTimePicker.getHour(), wateringTimePicker.getMinute());
        replyIntent.putExtra(EXTRA_EDIT_PLANT_TIME, time);
    }

    private void handleSaveButtonClick(Intent replyIntent) {
        finish();
    }

    private void setupCaptureImageButton() {
        captureImageButton = findViewById(R.id.capture_plant_image_button);
        captureImageButton.setOnClickListener(view -> {
            startCameraIntent();
        });
    }

    private void startCameraIntent() {
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE_SECURE);
        startActivityForResult(openCameraIntent, REQUEST_IMAGE_CAPTURE);
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
        PlantApiRequest plantApiRequest = createPlantApiRequest(capturedImage);
        Call<PlantApiResponse> plantApiCall = plantApi.identify(plantApiRequest, getString(R.string.apikey));

        plantApiCall.enqueue(new Callback<PlantApiResponse>() {
            @Override
            public void onResponse(Call<PlantApiResponse> call, Response<PlantApiResponse> response) {
                handlePlantApiResponse(response);
            }

            @Override
            public void onFailure(Call<PlantApiResponse> call, Throwable t) {
                handleApiFailure(t);
            }
        });
    }

    private PlantApiRequest createPlantApiRequest(Bitmap capturedImage) {
        PlantApiRequest plantApiRequest = new PlantApiRequest();
        String encodedImage = bitMapAsBase64(capturedImage);
        plantApiRequest.images.add("data:image/jpg;base64," + encodedImage);
        plantApiRequest.latitude = 0.0;
        plantApiRequest.longitude = 0.0;
        return plantApiRequest;
    }

    private void handlePlantApiResponse(Response<PlantApiResponse> response) {
        if (response.isSuccessful() && response.body() != null) {
            Result plantIdentification = response.body().result;
            handlePlantIdentification(plantIdentification);
        } else {
            showSnackbar("Failed to fetch plant info");
        }
    }

    private void handlePlantIdentification(Result plantIdentification) {
        Suggestion plantSuggestion = plantIdentification.classification.suggestions.get(0);
        if (!plantIdentification.is_plant.binary) {
            showSnackbar("Image does not contain plant");
            return;
        }

        showPlantDialog(plantSuggestion);
    }

    private void showPlantDialog(Suggestion plantSuggestion) {
        Dialog settingsDialog = new Dialog(this);
        settingsDialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View view = getLayoutInflater().inflate(R.layout.image_layout, null);

        if (!plantSuggestion.similar_images.isEmpty()) {
            ImageView imageView = view.findViewById(R.id.image_layout_imageView);
            String plantImageUrl = plantSuggestion.similar_images.get(0).url;
            Picasso.get().load(plantImageUrl).into(imageView);
        }

        TextView name = view.findViewById(R.id.image_layout_textView);
        name.setText(plantSuggestion.name);

        Button acceptButton = view.findViewById(R.id.image_layout_accept_button);
        acceptButton.setOnClickListener(v -> {
            editNameEditText.setText(plantSuggestion.name);
            settingsDialog.dismiss();
        });

        Button cancelButton = view.findViewById(R.id.image_layout_decline_button);
        cancelButton.setOnClickListener(v -> settingsDialog.dismiss());

        settingsDialog.setContentView(view);
        settingsDialog.show();
    }

    private void handleApiFailure(Throwable t) {
        showSnackbar("Something went wrong: " + t.getMessage());
    }

    private void showSnackbar(String message) {
        Snackbar.make(findViewById(R.id.edit_plant_activity), message, Snackbar.LENGTH_LONG).show();
    }

    private static String bitMapAsBase64(Bitmap capturedImage) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        capturedImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }
}
