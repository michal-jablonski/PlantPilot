package com.example.plantpilot;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.graphics.Color;

import android.os.PersistableBundle;
import android.util.Log;
import androidx.core.app.NotificationCompat;

public class NotificationJobService extends JobService {

    private static final String TAG = "NotificationJobService";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "Job started");
        sendNotification(getApplicationContext(), params);
        Log.d(TAG, "Job finished");
        jobFinished(params, false);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job stopped");
        return false;
    }

    private void sendNotification(Context context, JobParameters params) {
        String channelId = "CHANNEL_ID_NOTIFICATION";
        PersistableBundle extras = params.getExtras();
        String plantName = extras.getString(NotificationScheduler.PLANT_NAME_NOTIFICATION_KEY);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
        builder
                .setSmallIcon(R.drawable.ic_water_drop_24)
                .setContentTitle(plantName)
                .setContentText(getString(R.string.time_for_watering))
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel(
                channelId,
                getString(R.string.scheduled_notifications),
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.enableLights(true);
        channel.setLightColor(Color.GREEN);
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);

        notificationManager.notify(0, builder.build());
    }
}
