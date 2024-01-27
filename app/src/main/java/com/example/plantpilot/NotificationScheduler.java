package com.example.plantpilot;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;

import java.sql.Time;
import java.time.LocalTime;
import java.util.Calendar;

public class NotificationScheduler {
    public static final String PLANT_NAME_NOTIFICATION_KEY = "plan_name_notification_key";

    public static void makeNotification(Context context, String title) {
//        String channelId = "CHANNEL_ID_NOTIFICATION";
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);
//        builder
//                .setSmallIcon(R.drawable.ic_add_black_24)
//                .setContentTitle(title)
//                .setContentText("Some text for notification here")
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//        Intent intent = new Intent(context, EditPlantActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        intent.putExtra("data", "Some value to be passed here");
//
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);
//        builder.setContentIntent(pendingIntent);
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        NotificationChannel notificationChannel =
//                notificationManager.getNotificationChannel(channelId);
//        if (notificationChannel == null) {
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            notificationChannel = new NotificationChannel(channelId, "Some description", importance);
//            notificationChannel.setLightColor(Color.GREEN);
//            notificationChannel.enableVibration(true);
//            notificationManager.createNotificationChannel(notificationChannel);
//        }
//
//        notificationManager.notify(0, builder.build());
        scheduleNotification(context);
    }

    public static void scheduleWateringJobForPlant(Context context, Plant plant) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context, NotificationJobService.class);
        PersistableBundle extras = new PersistableBundle();
        extras.putString(PLANT_NAME_NOTIFICATION_KEY, plant.getName());

        JobInfo.Builder builder = new JobInfo.Builder(plant.getId(), componentName)
                .setExtras(extras)
                .setMinimumLatency(calculateMillisUntilWatering(plant));

        jobScheduler.schedule(builder.build());

        int resultCode = jobScheduler.schedule(builder.build());
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d("NotificationScheduler", "Job scheduled successfully!");
        } else {
            Log.e("NotificationScheduler", "Job scheduling failed!");
        }
    }

    public static long calculateMillisUntilWatering(Plant plant) {
        // Pobierz bieżący kalendarz i czas
        Weekday wateringDay = plant.getWateringDay();
        LocalTime wateringTime = plant.getWateringTime();

        Calendar currentCalendar = Calendar.getInstance();
        int currentDayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(Calendar.MINUTE);

        // Oblicz numer dnia tygodnia dla podlania
        int wateringDayOfWeek = wateringDay.ordinal() + 1; // +2, ponieważ Weekday zaczyna się od Monday (Monday ma wartość 0)

        // Sprawdź, czy dniem podlania jest dzisiaj, czy później w tygodniu
        int daysUntilWatering = wateringDayOfWeek - currentDayOfWeek;
        if (daysUntilWatering < 0) {
            daysUntilWatering += 7; // Jeśli roślina ma być podlana w następnym tygodniu, dodaj 7 dni
        }

        // Oblicz różnicę czasu między bieżącym czasem a czasem podlania
        int wateringHour = wateringTime.getHour();
        int wateringMinute = wateringTime.getMinute();
        long millisUntilWatering = daysUntilWatering * 24 * 60 * 60 * 1000 +
                (wateringHour - currentHour) * 60 * 60 * 1000 +
                (wateringMinute - currentMinute) * 60 * 1000;

        if (millisUntilWatering < 0) {
            // Jeśli czas podlania już minął w tym tygodniu, dodaj 7 dni
            millisUntilWatering += 7 * 24 * 60 * 60 * 1000;
        }

        return millisUntilWatering;
    }

    private static void scheduleNotification(Context context) {
        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        ComponentName componentName = new ComponentName(context, NotificationJobService.class);

        JobInfo.Builder builder = new JobInfo.Builder(0, componentName)
//                .setPeriodic(15 * 60 * 1000);
                .setMinimumLatency(10 * 1000); // Run every 24 hours
//        builder.setPeriodic(30 * 1000); // Run every 24 hours

        if (jobScheduler != null) {
            int resultCode = jobScheduler.schedule(builder.build());
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d("NotificationScheduler", "Job scheduled successfully!");
            } else {
                Log.e("NotificationScheduler", "Job scheduling failed!");
            }
        }
    }
}
