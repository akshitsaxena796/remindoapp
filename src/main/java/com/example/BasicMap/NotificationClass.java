package com.example.BasicMap;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class NotificationClass extends BroadcastReceiver {

    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    @Override
    public void onReceive(Context context, Intent intent) {

       JSONObject notify_JsonObj= MyService.json_static ;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, RemainderDetailsActivity.class), 0);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String dayLongName = Calendar.getInstance().getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"NOTIFICATION_CHANNEL_NAME", importance);
            channel.enableLights(true);
            channel.setLightColor(Color.GREEN);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});


            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this

            mNotificationManager.createNotificationChannel(channel);
        }

        try {
            String place= notify_JsonObj.getString("placeName");
            String nearby = notify_JsonObj.getString("nearBy");


        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
               .setSmallIcon(R.drawable.ic_add_alarm_black_24dp)
               .setContentTitle(nearby+","+place)
                .setContentText("You visited this place last"+dayLongName+ ", Here is your hassle free route in case of re-visit");


        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
        mBuilder.setAutoCancel(true);

        int NOTIFICATION_ID = 1;


   mBuilder.setContentIntent(contentIntent);

   mNotificationManager.notify(NOTIFICATION_ID,mBuilder.build());


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    }

//
//    @Override
//    public void onReceive(Context context, Intent intent) {
//
//        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
//                new Intent(context, RemainderDetailsActivity.class), 0);
//
//        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//
//            Log.d("notification channel ","oreo");
//
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//
//            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,"NOTIFICATION_CHANNEL_NAME", importance);
//            channel.enableLights(true);
//            channel.setLightColor(Color.GREEN);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
//
//
//            // Register the channel with the system; you can't change the importance
//            // or other notification behaviors after this
//
//            mNotificationManager.createNotificationChannel(channel);
//        }
//
//
//    /*    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID)
//                .setSmallIcon(R.drawable.ic_add_alarm_black_24dp)
//                .setContentTitle("Upcoming Travel notification")
//                .setContentText("You visited this place last <day>, Here is your hassle free route in case of re-visit");
//
//        mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
//        mBuilder.setDefaults(Notification.DEFAULT_SOUND);
//        mBuilder.setAutoCancel(true);
//
//        int NOTIFICATION_ID = 1;
//        */
//
//
//     //   mBuilder.setContentIntent(contentIntent);
//
//     //   mNotificationManager.notify(NOTIFICATION_ID,mBuilder.build());
//    }
//
//
//}
