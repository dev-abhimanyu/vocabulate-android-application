package com.abhimanyu.vocabulate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.abhimanyu.vocabulate.db.VocabulateData;
import com.abhimanyu.vocabulate.notification.NotificationHelper;
import com.abhimanyu.vocabulate.recycler_swipe.LikeActivity;

import java.util.List;
import java.util.Random;
/**
 * Created by abhimanyu
 */
public class StartingScreenActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String EXTRA_CATEGORY_ID = "extraCategoryID";
    public static final String EXTRA_CATEGORY_NAME = "extraCategoryName";
    public static final String EXTRA_DIFFICULTY = "extraDifficulty";

    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String KEY_HIGHSCORE = "keyHighscore";

    private TextView textViewHighscore;
    private Spinner spinnerCategory;
    private Spinner spinnerDifficulty;

    private int highscore;
//    public static  String CHANNEL_ID = "WORD OF THE DAY";
//    private String NotificationTitle = "Word of the Day";
    private VocabulateData dataObj;
    private Context mContext;
//    private Toast wotdToast;
//    private boolean wotd_isEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_screen);
        mContext = getApplicationContext();
        //setting notification bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.splashBackground));

        //font for all buttons
        Typeface mainButtonFont = Typeface.createFromAsset(getAssets(), "fonts/schoolbell_regular.ttf");

        textViewHighscore = findViewById(R.id.text_view_highscore);
        textViewHighscore.setTypeface(mainButtonFont);

        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerDifficulty = findViewById(R.id.spinner_difficulty);

        //main heading
        TextView mainHeadingView = findViewById(R.id.mainHeading);
        Typeface mainHeadFont = Typeface.createFromAsset(getAssets(), "fonts/komikax.ttf");   // cabin_sketch_bold.otf
        mainHeadingView.setTypeface(mainHeadFont);

        TextView highTextView = findViewById(R.id.highscore_text);
        highTextView.setTypeface(mainButtonFont);

        //Setting toggles
        Button resetBtn = findViewById(R.id.score_reset);
        resetBtn.setTypeface(mainButtonFont);
        Button rate_btn = findViewById(R.id.rate_button);
        rate_btn.setTypeface(mainButtonFont);
        ToggleButton wotd_toggle = findViewById(R.id.wotd_button);
        wotd_toggle.setTypeface(mainButtonFont);
        wotd_toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    wotd_toggle.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(R.drawable.yes_wotd),null,null,null);
                    NotificationHelper.scheduleRepeatingRTCNotification(mContext);
                    NotificationHelper.enableBootReceiver(mContext);

                }else{
                    wotd_toggle.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(R.drawable.no_wotd),null,null,null);
                    NotificationHelper.cancelAlarmRTC();
                    NotificationHelper.disableBootReceiver(mContext);
                }
            }
        });
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        Toast wotdToast = Toast.makeText(StartingScreenActivity.this,"You will receive word of the day notification!",Toast.LENGTH_SHORT);
        wotd_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wotd_toggle.isChecked()) {
                    wotdToast.setText("You will receive word of the\nday notification!");
                    wotdToast.show();
                }
                else
                {
                    wotdToast.setText("Word of the day notification disabled!");
                    wotdToast.show();
                }
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("wotd_isEnabled", wotd_toggle.isChecked());
                editor.apply();
            }
        });
        boolean wotd_isEnabled = preferences.getBoolean("wotd_isEnabled",true);
        if(wotd_isEnabled)
            wotd_toggle.setChecked(true);
        else {
            wotd_toggle.setChecked(false);
            wotd_toggle.setCompoundDrawablesRelativeWithIntrinsicBounds(getResources().getDrawable(R.drawable.no_wotd),null,null,null);
        }
        //setting reset button action
        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder resetBuilder = new AlertDialog.Builder(StartingScreenActivity.this);
                resetBuilder.setTitle("Reset Highscore");
                resetBuilder.setMessage("Do you really want to reset the high score?");
                resetBuilder.setIcon(R.mipmap.ic_launcher_round);
                resetBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateHighscore(0);
                        dialog.dismiss();
                    }
                });
                resetBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog resetAlert = resetBuilder.create();
                resetAlert.show();
            }
        });
        rate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rateMe();
            }
        });

        loadCategories();
        loadDifficultyLevels();
        loadHighscore();

        Button buttonStartQuiz = findViewById(R.id.button_start_quiz);

        buttonStartQuiz.setTypeface(mainButtonFont);
        buttonStartQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });
        Button buttonStartBookmark = findViewById(R.id.bookmarkButton);
        buttonStartBookmark.setTypeface(mainButtonFont);
        buttonStartBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent bookmarkIntent = new Intent(getApplicationContext(), LikeActivity.class);
                startActivity(bookmarkIntent);
            }
        });


        // First createNotificationChannel() is called at SplashActivity
//        Intent notifyIntent = new Intent(this, SplashActivity.class);
//        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notifyIntent, 0);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(R.drawable.green_tick)
//                .setContentTitle(NotificationTitle)
//                .setContentText("abstruse: difficult to comprehend")
//                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//                // Set the intent that will fire when the user taps the notification
//                .setContentIntent(pendingIntent)
//                .setAutoCancel(true);
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
//        // notificationId is a unique int for each notification that you must define
//        Random r = new Random();
//        int notificationId = r.nextInt();
//        notificationManager.notify(notificationId,builder.build());
    }
    // move the below method to splash


    public void rateMe(){
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
//        try{
//            startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("market://details?id="+getPackageName())));
//        }catch (ActivityNotFoundException e){
//            startActivity(new Intent(Intent.ACTION_VIEW,
//                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
//        }
    }
    private void startQuiz() {
        Category selectedCategory = (Category) spinnerCategory.getSelectedItem();
        int categoryID = selectedCategory.getId();
        String categoryName = selectedCategory.getName();
        String difficulty = spinnerDifficulty.getSelectedItem().toString();

        Intent intent = new Intent(StartingScreenActivity.this, QuizActivity.class);
        intent.putExtra(EXTRA_CATEGORY_ID, categoryID);
        intent.putExtra(EXTRA_CATEGORY_NAME, categoryName);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);
        startActivityForResult(intent, REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_QUIZ) {
            if (resultCode == RESULT_OK) {
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE, 0);
                if (score > highscore) {
                    updateHighscore(score);
                }
            }
        }
    }

    private void loadCategories() {
        QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
        List<Category> categories = dbHelper.getAllCategories();

        ArrayAdapter<Category> adapterCategories = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapterCategories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapterCategories);
    }

    private void loadDifficultyLevels() {
        String[] difficultyLevels = Question.getAllDifficultyLevels();

        ArrayAdapter<String> adapterDifficulty = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, difficultyLevels);
        adapterDifficulty.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDifficulty.setAdapter(adapterDifficulty);
    }

    private void loadHighscore() {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        highscore = prefs.getInt(KEY_HIGHSCORE, 0);
        textViewHighscore.setText(String.valueOf(highscore));
    }

    private void updateHighscore(int highscoreNew) {
        highscore = highscoreNew;
        textViewHighscore.setText(String.valueOf(highscore));

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_HIGHSCORE, highscore);
        editor.apply();
    }

    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(StartingScreenActivity.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher_round);
        builder.setMessage("Thank you for using Vocabulate. Please give us your suggestions and feedback.")     //Do you really want to quit?
                .setCancelable(false)
                .setPositiveButton("Quit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("Rate", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        rateMe();
//                        Intent intent = new Intent(Intent.ACTION_VIEW);
//                        intent.setData(Uri.parse("http://play.google.com/store/apps/details?id="+getPackageName()));
//                        startActivity(intent);
                    }
                })
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.cancel();
//                    }
//                })

        ;
        AlertDialog alert=builder.create();
        alert.show();
    }
}