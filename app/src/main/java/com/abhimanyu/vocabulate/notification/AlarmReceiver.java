package com.abhimanyu.vocabulate.notification;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.core.app.NotificationCompat;

import com.abhimanyu.vocabulate.QuizContract;
import com.abhimanyu.vocabulate.QuizDbHelper;
import com.abhimanyu.vocabulate.R;
import com.abhimanyu.vocabulate.SplashActivity;
import com.abhimanyu.vocabulate.db.VocabulateData;
//import com.abhimanyu.vocabulate.QuizContract.NotificationTable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
/**
 * Created by abhimanyu
 */
public class AlarmReceiver extends BroadcastReceiver{
    public static String CHANNEL_ID = "This is Channel ID";
    public static String NotificationTitle = "Word of the Day";
    private VocabulateData vocabObj;
    private HashMap<String,String> hm;
    private Object[] keys;
    private Object key;
    private HashSet<Object> keysDone;
//    private ContentValues cv;
//    private String[] colsToSelect = {NotificationTable.WORD};;
//    private String whereClause = NotificationTable.WORD + " = ?";
//    private QuizDbHelper dbHelper;
//    private SQLiteDatabase db;
//    private Cursor cursor1;
    @Override
    public void onReceive(Context context, Intent intent) {
        //Get notification manager to manage/send notifications


        //Intent to invoke app when click on notification.
        //In this sample, we want to start/launch this sample app when user clicks on notification
        Intent intentToRepeat = new Intent(context, SplashActivity.class);
        //set flag to restart/relaunch the app
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        //Pending intent to handle launch of Activity in intent above
        PendingIntent pendingIntent =
                PendingIntent.getActivity(context, NotificationHelper.ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

        //Randomizing the notification content
        vocabObj = new VocabulateData();
        hm = vocabObj.VocabData();
        keys = hm.keySet().toArray();
        keysDone = new HashSet<>();
//        key = keys[new Random().nextInt(keys.length)];

        //Build notification
        Notification repeatedNotification = buildLocalNotification(context, pendingIntent).build();

        //Send local notification
        NotificationHelper.getNotificationManager(context).notify(NotificationHelper.ALARM_TYPE_RTC, repeatedNotification);



    }

    public NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {
        int counter = 0;
        key = keys[new Random().nextInt(keys.length)];
        while (keysDone.contains(key))
        {
            counter++;
            key = keys[new Random().nextInt(keys.length)];
            if(counter==keys.length)
            {
                keysDone.clear();
                break;
            }
        }
//        int wordExisits = 0;
//        String[] whereSelectionArgs = { key.toString() };
//        dbHelper = new QuizDbHelper(context);
//        db = dbHelper.getWritableDatabase();
//        cursor1 = db.query(
//                NotificationTable.TABLE_NAME,
//                colsToSelect,
//                whereClause,
//                whereSelectionArgs,
//                null,
//                null,
//                null
//        );
//        while(cursor1.moveToNext()){
//            if(cursor1.getString(cursor1.getColumnIndex(NotificationTable.WORD)).equals(key.toString()))
//            {
//                wordExisits = 1;
//                break;
//            }
//        }
//        cursor1.close();
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.green_tick)
                .setContentTitle(NotificationTitle)
                .setContentText(key.toString()+": "+hm.get(key))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);
        keysDone.add(key);
        return builder;
    }
}

