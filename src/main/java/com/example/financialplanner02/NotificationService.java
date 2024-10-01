package com.example.financialplanner02;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

import java.util.List;

public class NotificationService extends Service {
    private static final String CHANNEL_ID = "NotificationServiceChannel";
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DatabaseHelper(this);
        createNotificationChannel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int userId = intent.getIntExtra("userId", -1);

        // Check if the user has achieved any savings goal or if expenses exceed a predefined limit
        // If yes, send the notification
        if (checkSavingsGoals(userId)) {
            Intent notificationIntent = new Intent(this, HomePageActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Financial Planner")
                    .setContentText("You have achieved a savings goal or your expenses have exceeded the limit.")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .build();

            startForeground(1, notification);
            stopSelf();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Financial Planner Notifications";
            String description = "You have achieved a savings goal! Log in to see what you've saved for";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private boolean checkSavingsGoals(int userId) {
        // Get the list of savings goals for this user
        List<SavingsGoal> savingsGoals = dbHelper.getSavingsGoals(userId);

        // For each savings goal, check if it has been achieved
        for (SavingsGoal goal : savingsGoals) {
            if (goal.isGoalAchieved()) {
                return true;
            }
        }
        // If no savings goal has been achieved, return false
        return false;
    }
}
