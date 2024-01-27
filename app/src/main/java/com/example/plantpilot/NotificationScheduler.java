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
        Weekday wateringDay = plant.getWateringDay();
        LocalTime wateringTime = plant.getWateringTime();

        Calendar currentCalendar = Calendar.getInstance();
        int currentDayOfWeek = currentCalendar.get(Calendar.DAY_OF_WEEK);
        int currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY);
        int currentMinute = currentCalendar.get(Calendar.MINUTE);

        int wateringDayOfWeek = wateringDay.value;

        int daysUntilWatering = wateringDayOfWeek - currentDayOfWeek;
        if (daysUntilWatering < 0) {
            daysUntilWatering += 7;
        }

        int wateringHour = wateringTime.getHour();
        int wateringMinute = wateringTime.getMinute();
        long millisUntilWatering = daysUntilWatering * 24 * 60 * 60 * 1000 +
                (wateringHour - currentHour) * 60 * 60 * 1000 +
                (wateringMinute - currentMinute) * 60 * 1000;

        if (millisUntilWatering < 0) {
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
