package com.abhimanyu.vocabulate;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.abhimanyu.vocabulate.notification.NotificationHelper;
import com.abhimanyu.vocabulate.onboarding.PrefManager;
import com.abhimanyu.vocabulate.onboarding.ViewsSliderActivity;

import static com.abhimanyu.vocabulate.notification.AlarmReceiver.CHANNEL_ID;
/**
 * Created by abhimanyu
 */
public class SplashActivity extends AppCompatActivity {
    private Context mContext;

    private static int SPLASH_SCREEN_TIME_OUT=1000;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        createNotificationChannel();
        mContext =getApplicationContext();
        prefManager = new PrefManager(this);

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, ViewsSliderActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);


        if(prefManager.isFirstTimeLaunch()) {
            NotificationHelper.cancelAlarmRTC();
            NotificationHelper.disableBootReceiver(mContext);
            NotificationHelper.scheduleRepeatingRTCNotification(mContext);
            NotificationHelper.enableBootReceiver(mContext);
        }

    }
    private void createNotificationChannel()
    {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channel_name = "Word Of The Day";
            String channel_desc = "Gives word of the day notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,channel_name,importance);
            channel.setDescription(channel_desc);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


}
