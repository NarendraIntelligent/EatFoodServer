package com.example.narendra.eatfoodserver.Service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.narendra.eatfoodserver.Common.Common;
import com.example.narendra.eatfoodserver.Helper.NotificationHelper;
import com.example.narendra.eatfoodserver.MainActivity;
import com.example.narendra.eatfoodserver.Model.Token;
import com.example.narendra.eatfoodserver.OrderStatus;
import com.example.narendra.eatfoodserver.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

/**
 * Created by narendra on 3/13/2018.
 */

public class MyFirebaseMessaging extends FirebaseMessagingService {

    @Override
    public void onNewToken( String token) {
        super.onNewToken(token);

        updateToServer(token);
    }
    private void updateToServer(String refreshedToken) {
        //copy code from client app
        if(Common.currentuser !=null) {
            FirebaseDatabase db = FirebaseDatabase.getInstance();
            DatabaseReference tokens = db.getReference("Tokens");
            Token data = new Token(refreshedToken, true);
            tokens.child(Common.currentuser.getPhone()).setValue(data);
        }

    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if (remoteMessage.getData() != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                sendNotificationAPI26(remoteMessage);
            else
                sendNotification(remoteMessage);
        }
    }
    private void sendNotificationAPI26(RemoteMessage remoteMessage) {

        Map<String,String>data=remoteMessage.getData();
        String title=data.get("title");
        String message=data.get("message");

        // Here we will fix to notification go to orders list
        PendingIntent pendingIntent;
        NotificationHelper helper;
         Notification.Builder builder;
   if(Common.currentuser !=null) {
    Intent intent = new Intent(this, OrderStatus.class);
    //it clears the previous activites
    intent.putExtra(Common.PHONE_TEXT, Common.currentuser.getPhone());
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    //FLAG_ONE_SHOT indicates for only one pendingintent
   pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    helper = new NotificationHelper(this);
    builder = helper.getChannelNotification(title,message, pendingIntent, defaultSoundUri);
    // Get random id to show all notifications
    helper.getManager().notify(new Random().nextInt(), builder.build());
}
else
{
    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    helper = new NotificationHelper(this);
    builder = helper.getChannelNotification(title,message,defaultSoundUri);
    // Get random id to show all notifications
    helper.getManager().notify(new Random().nextInt(), builder.build());
}

    }

    private void sendNotification(RemoteMessage remoteMessage) {

        //Remote message contains data
        Map<String,String>data=remoteMessage.getData();
        String title=data.get("title");
        String message=data.get("message");

        Intent intent=new Intent(this, MainActivity.class);
        //it clears the previous activites
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //FLAG_ONE_SHOT indicates for only one pendingintent
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        //Returns the Uri for the default ringtone of a particular type. Rather than returning the actual ringtone's sound Uri, this will return the symbolic Uri which will resolved to the actual sound when played.
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        NotificationManager noti=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        noti.notify(0,builder.build());




    }

    }

