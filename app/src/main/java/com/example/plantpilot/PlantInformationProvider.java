package com.example.plantpilot;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import com.google.android.material.snackbar.Snackbar;

import static androidx.core.content.ContextCompat.startActivity;

public class PlantInformationProvider {

    public static void openYouTubeAppWithPlantName(View view, String plantName) {
        Context context = view.getContext();
        PackageManager packageManager = context.getPackageManager();
        Intent youtubeIntent = packageManager.getLaunchIntentForPackage(context.getString(R.string.youtube_package));

        if (youtubeIntent == null) {
            String errorMessage = context.getString(R.string.youtube_not_installed);
            Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show();
        }

        String youtubeQuery = context.getString(R.string.youtube_link) + Uri.encode(plantName);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(youtubeQuery));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (intent.resolveActivity(packageManager) == null) {
            String errorMessage = context.getString(R.string.no_app_to_open_youtube);
            Snackbar.make(view, errorMessage, Snackbar.LENGTH_LONG).show();
        }

        startActivity(context, intent, null);
    }
}