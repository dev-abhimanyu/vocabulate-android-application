package com.abhimanyu.vocabulate;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.abhimanyu.vocabulate.db.TastyToasts;
import com.abhimanyu.vocabulate.like.LikeButtonView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
/**
 * Created by abhimanyu
 */
public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "extraScore";
    private static final long COUNTDOWN_IN_MILLIS = 30000;          //30000

    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";

    private TextView textViewQuestion;
    private TextView textViewScore;
    private TextView textViewQuestionCount;
    private TextView textViewCategory;
    private TextView textViewDifficulty;
    private TextView textViewCountDown;
    private RadioGroup rbGroup;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private Button buttonConfirmNext;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultCd;

    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private ArrayList<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    public Question currentQuestion;               //private
//    public LikeButtonView likeObj;

    private int score;
    private boolean answered;
    private int incorrectAns = 0;

    private long backPressedTime;

    private TextView[] alphabets = new TextView[10];

    private ProgressBar countdownProgressBar;
    private LikeButtonView likeButton;
    private ImageView ivLike;

    //For no option selected toast
    private int toastClickCount = 0;
    private String toastText = "Please select an answer";

    //score trophy
    ImageView trophyView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        //setting notification bar color
        getWindow().setStatusBarColor(getResources().getColor(R.color.splashBackground));

        // adding custom font
        alphabets[0] = findViewById(R.id.letter_v);
        alphabets[1] = findViewById(R.id.letter_o);
        alphabets[2] = findViewById(R.id.letter_c);
        alphabets[3] = findViewById(R.id.letter_a);
        alphabets[4] = findViewById(R.id.letter_b);
        alphabets[5] = findViewById(R.id.letter_u);
        alphabets[6] = findViewById(R.id.letter_l);
        alphabets[7] = findViewById(R.id.letter_a2);
        alphabets[8] = findViewById(R.id.letter_t);
        alphabets[9] = findViewById(R.id.letter_e);
        // setting vocabulate countdown font
        Typeface vocabulate_font = Typeface.createFromAsset(getAssets(),  "fonts/lacquer.ttf");
        // permanent_marker_regular.ttf, lacquer.ttf, bangers_regular.ttf
        for(int alphaCounter = 0; alphaCounter<10;alphaCounter++)
        {alphabets[alphaCounter].setTypeface(vocabulate_font);}

        textViewQuestion = findViewById(R.id.text_view_question);
        // setting question font
        Typeface ques_font = Typeface.createFromAsset(getAssets(), "fonts/fredoka_one_regular.ttf");
        textViewQuestion.setTypeface(ques_font);

        // setting countdown res
        countdownProgressBar=findViewById(R.id.progressBar);

        //setting score font
        textViewScore = findViewById(R.id.text_view_score);
        Typeface score_font = Typeface.createFromAsset(getAssets(), "fonts/fredoka_one_regular.ttf");
        textViewScore.setTypeface(score_font);

        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewCategory = findViewById(R.id.text_view_category);
        textViewDifficulty = findViewById(R.id.text_view_difficulty);

        textViewCountDown = findViewById(R.id.text_view_countdown);
        //setting countdown font
        Typeface countdown_font = Typeface.createFromAsset(getAssets(), "fonts/fredoka_one_regular.ttf");
        textViewCountDown.setTypeface(countdown_font);

        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        //setting choices fonts
        Typeface choice_font = Typeface.createFromAsset(getAssets(), "fonts/opensans_semi_bold.ttf");
        rb1.setTypeface(choice_font);
        rb2.setTypeface(choice_font);
        rb3.setTypeface(choice_font);

        // like button
        likeButton = findViewById(R.id.starLike);
        likeButton.isChecked();
        ivLike = findViewById(R.id.ivStar);

        buttonConfirmNext = findViewById(R.id.button_confirm_next);
        //setting next button fonts
        Typeface button_font = Typeface.createFromAsset(getAssets(), "fonts/opensans_semi_bold.ttf");
        buttonConfirmNext.setTypeface(button_font);

        //setting score trophy view
        trophyView = findViewById(R.id.scoreTrophy);

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCd = textViewCountDown.getTextColors();

        Intent intent = getIntent();
        int categoryID = intent.getIntExtra(StartingScreenActivity.EXTRA_CATEGORY_ID, 0);
        String categoryName = intent.getStringExtra(StartingScreenActivity.EXTRA_CATEGORY_NAME);
        String difficulty = intent.getStringExtra(StartingScreenActivity.EXTRA_DIFFICULTY);

        textViewCategory.setText("Category: " + categoryName);
        textViewDifficulty.setText("Difficulty: " + difficulty);

        if (savedInstanceState == null) {
            QuizDbHelper dbHelper = QuizDbHelper.getInstance(this);
            questionList = dbHelper.getQuestions(categoryID, difficulty);
            questionCountTotal = questionList.size();
            Collections.shuffle(questionList);
            incorrectAns =0;
//            LikeButtonView likeButton;
//            likeButton = findViewById(R.id.starLike);
//            likeButton.isChecked();
//            likeButton.isChecked = false;
//            LikeButtonView.animatorSet.cancel();
            showNextQuestion();
        } else {
            questionList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            questionCountTotal = questionList.size();
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currentQuestion = questionList.get(questionCounter - 1);
            LikeButtonView.LikedWords = currentQuestion.getQuestion();
            score = savedInstanceState.getInt(KEY_SCORE);
            timeLeftInMillis = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);

            if (!answered) {
                startCountDown();
            } else {
                updateCountDownText();
                showSolution();
            }
        }

        buttonConfirmNext.setOnClickListener(new View.OnClickListener() {
            TastyToasts toastObj = new TastyToasts();
            Toast noSelectionToast =  Toast.makeText(QuizActivity.this, toastText, Toast.LENGTH_SHORT);
            @Override
            public void onClick(View v) {
                if (!answered) {
                    if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked()) {
                        checkAnswer();

                    } else {
                        toastClickCount++;
                        if(toastClickCount>1)
                            toastText = toastObj.getToast();
                        noSelectionToast.cancel();
                        noSelectionToast =  Toast.makeText(QuizActivity.this, toastText, Toast.LENGTH_SHORT);
                        noSelectionToast.show();
//                        Toast.makeText(QuizActivity.this, toastText, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if(incorrectAns>10)
                        finishQuiz();
                    else {
//                        LikeButtonView.isChecked = false;
//                        LikeButtonView.animatorSet.cancel();
//                        LikeButtonView likeButton;
//                        likeButton = findViewById(R.id.starLike);
//                        likeButton.isChecked();
//                        likeButton.isChecked = false;
                        showNextQuestion();
                    }
                }
            }
        });
    }

    private void showNextQuestion() {
        ivLike.setImageResource(R.drawable.ic_star_rate_off);
        likeButton.isChecked = false;
        toastClickCount = 0;
        toastText = "Please select an answer";
//        likeButton.isChecked = false;
//        likeButton.animatorSet.cancel();
//        LikeButtonView lbv = new LikeButtonView(this);
//        LikeButtonView.isChecked = false;
//        LikeButtonView.ivStar.se
        // unchecking the green tick for all the options
        rb1.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        rb2.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        rb3.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);

        for (int i = 0; i < rbGroup.getChildCount(); i++) {
            rbGroup.getChildAt(i).setEnabled(true);
        }
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

//        if (incorrectAns>10)
//            finishQuiz();
//        else {
            if (questionCounter < questionCountTotal) {
                currentQuestion = questionList.get(questionCounter);
                LikeButtonView.LikedWords = currentQuestion.getQuestion();
                textViewQuestion.setText(currentQuestion.getQuestion());
                rb1.setText(currentQuestion.getOption1());
                rb2.setText(currentQuestion.getOption2());
                rb3.setText(currentQuestion.getOption3());

                questionCounter++;
                textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
                answered = false;
                buttonConfirmNext.setText("confirm");
                timeLeftInMillis = COUNTDOWN_IN_MILLIS;
                startCountDown();
            } else {
                finishQuiz();
                Intent noWordsActivity = new Intent(getApplicationContext(),NoMoreWordsActivity.class);
                startActivity(noWordsActivity);
            }
    }

    private void startCountDown() {
        countdownProgressBar.setProgress(30000);
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    private void updateCountDownText() {
//        int minutes = (int) (timeLeftInMillis / 1000) / 60;
//        int seconds = (int) (timeLeftInMillis / 1000) % 60;
//
//        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);

        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        countdownProgressBar.setProgress(seconds);
        String timeFormatted = String.format(Locale.getDefault(), "%02d", seconds);

        textViewCountDown.setText(timeFormatted);

        if (timeLeftInMillis < 10000) {
//            countdownProgressBar.setProgressTintList(ColorStateList.valueOf(getResources().getColor(R.color.countdownEndColor)));
            textViewCountDown.setTextColor(getResources().getColor(R.color.countdownEndColor));
        } else {
            textViewCountDown.setTextColor(textColorDefaultCd);
        }
    }

    private void checkAnswer() {
        answered = true;

        countDownTimer.cancel();

        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        if (answerNr == currentQuestion.getAnswerNr()) {
            score++;
            textViewScore.setText(String.valueOf(score));               //original: textViewScore.setText("Score: " + score);
            if(score==10) {
                trophyView.setImageResource(R.drawable.silver_trophy);
                textViewScore.setTextColor(getResources().getColor(R.color.silver_trophy_text));
            }
            else if(score==40) {
                trophyView.setImageResource(R.drawable.gold_trophy);
                textViewScore.setTextColor(getResources().getColor(R.color.gold_trophy_text));
            }
        }
        else
        {
            textViewScore.setText(String.valueOf(score));                  //original: textViewScore.setText("Score: " + score);
            incorrectAns++;
            if(incorrectAns<=10) {
                alphabets[incorrectAns - 1].setVisibility(View.VISIBLE);
            }
        }
        showSolution();
    }

    private void showSolution() {
        rb1.setTextColor(getResources().getColor(R.color.wrongChoice));
        rb2.setTextColor(getResources().getColor(R.color.wrongChoice));
        rb3.setTextColor(getResources().getColor(R.color.wrongChoice));
        for (int i = 0; i < rbGroup.getChildCount(); i++) {
            rbGroup.getChildAt(i).setEnabled(false);
        }
        switch (currentQuestion.getAnswerNr()) {
            case 1:
                rb1.setTextColor(getResources().getColor(R.color.rightChoice));
                rb1.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.green_tick,0);
//                textViewQuestion.setText("Answer 1 is correct");
                break;
            case 2:
                rb2.setTextColor(getResources().getColor(R.color.rightChoice));
                rb2.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.green_tick,0);
//                textViewQuestion.setText("Answer 2 is correct");
                break;
            case 3:
                rb3.setTextColor(getResources().getColor(R.color.rightChoice));
                rb3.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.green_tick,0);
//                textViewQuestion.setText("Answer 3 is correct");
                break;
        }

        if (questionCounter < questionCountTotal && incorrectAns<=10) {
            buttonConfirmNext.setText("next");
        } else {
            buttonConfirmNext.setText("Finish");
        }
    }

    private void finishQuiz() {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_SCORE, score);
        setResult(RESULT_OK, resultIntent);
        incorrectAns =0;
        finish();
    }

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            finishQuiz();
        } else {
            Toast.makeText(this, "Press back again to finish", Toast.LENGTH_SHORT).show();
        }

        backPressedTime = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_SCORE, score);
        outState.putInt(KEY_QUESTION_COUNT, questionCounter);
        outState.putLong(KEY_MILLIS_LEFT, timeLeftInMillis);
        outState.putBoolean(KEY_ANSWERED, answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST, questionList);
    }
}