package com.example.srk;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

import com.example.srk.activities.KKSCodeActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FirebaseService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        super.onMessageReceived(remoteMessage);
        Log.d("FIREBASE", "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0){
            Log.d("FIREBASE", "Message data payload: " + remoteMessage.getData());
            showNotification(remoteMessage.getData().get("title"), remoteMessage.getData().get("message"));
        }
    }

    @Override
    public void onNewToken(String s) {
        Log.d("FIREBASE", "Token refreshed: " + s);
    }

    private RemoteViews getCustomDesign(String title,String message){
        RemoteViews remoteViews=new RemoteViews(getApplicationContext().getPackageName(), R.layout.notification);
        remoteViews.setTextViewText(R.id.title,title);
        remoteViews.setTextViewText(R.id.message,message);

        Calendar calendar = Calendar.getInstance();;
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");;
        String date = dateFormat.format(calendar.getTime());;
        remoteViews.setTextViewText(R.id.notificationTime, date);
        return remoteViews;
    }

    private void showNotification(String title,String message){

        Intent intent = new Intent(this, KKSCodeActivity.class);
        intent.putExtra("kksCode", message);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        String channelId="web_app_channel";
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(getApplicationContext(),channelId).setSmallIcon(R.drawable.switchboard_icon)
                .setSound(uri)
                .setAutoCancel(true)
                .setVibrate(new long[]{1000,1000,1000,1000,1000})
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            builder=builder.setContent(getCustomDesign(title,message));
        }else{
            builder=builder.setContentTitle(title)
                    .setContentText(message);
        }

        NotificationManager notificationManager= (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel notificationChannel=new NotificationChannel(channelId,"web_app",NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setSound(uri,null);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0,builder.build());
    }
}
