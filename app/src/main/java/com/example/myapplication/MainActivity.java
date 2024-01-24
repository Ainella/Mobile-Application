package com.example.myapplication;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private EditText editText1, editText2, editText3;
    private TextView nameLabel,sumLabel;
    private ArrayList<String> listItems;

    private static final String PREFS_NAME = "MyPrefsFile";
    private static final String LIST_KEY = "listKey";
    private int stopCounter;
    private static final String STOP_COUNTER_KEY = "STOP_COUNTER_KEY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText1 = findViewById(R.id.editText1);
        editText2 = findViewById(R.id.editText2);
        editText3 = findViewById(R.id.editText3);
        nameLabel = findViewById(R.id.name);
        sumLabel = findViewById(R.id.sum);

        Button calculateButton = findViewById(R.id.calculateButton);
        Button showListButton = findViewById(R.id.showListButton);
        Button clearListButton = findViewById(R.id.clearListButton);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateAndDisplayResult();
            }
        });

        showListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showList();
            }
        });

        clearListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
            }
        });

        loadListFromSharedPreferences();
    }

    private void calculateAndDisplayResult() {
        String value1 = editText1.getText().toString();
        String value2 = editText2.getText().toString();
        String value3 = editText3.getText().toString();

        int sum = Integer.parseInt(value2) + Integer.parseInt(value3);
        nameLabel.setText(value1);
        sumLabel.setText(String.valueOf(sum));

        addToSharedPreferences(value1, String.valueOf(sum));
    }

    private void showList() {
        Intent intent = new Intent(this, ListActivity.class);
        intent.putStringArrayListExtra("listItems", listItems);
        startActivity(intent);
    }

    private void clearList() {
        listItems.clear();
        saveListToSharedPreferences();
        Toast.makeText(this, "List cleared", Toast.LENGTH_SHORT).show();
    }

    private void addToSharedPreferences(String name, String sum) {
        listItems.add(name + ": " + sum);
        saveListToSharedPreferences();
    }

    private void saveListToSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> set = new HashSet<>(listItems);
        editor.putStringSet(LIST_KEY, set);
        editor.putInt(STOP_COUNTER_KEY, stopCounter);
        editor.apply();
    }

    private void loadListFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(LIST_KEY, new HashSet<>());
        listItems = new ArrayList<>(set);
        stopCounter = prefs.getInt(STOP_COUNTER_KEY,0);
    }
    private static final String NOTIFICATION_CHANNEL_ID = "My ChannelId";
    private static final int NOTIFICATION_ID = 1;
    @Override
    protected void onStop() {
        super.onStop();
        stopCounter++;
        saveListToSharedPreferences();
        showNotification();
    }

    private void showNotification() {
        // Implement code to display notification with a stop counter
        // Create a notification channel for Android Oreo and higher
        NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Channel",
                NotificationManager.IMPORTANCE_DEFAULT
        );

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        Notification notification = new Notification.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("App Stopped")
                .setContentText("The app has been stopped = " + String.valueOf(stopCounter) + " times ")
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();

        // Display the notification
        if (notificationManager != null) {
            notificationManager.notify(NOTIFICATION_ID, notification);
        }

    }


}


