package com.abhimanyu.vocabulate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
/**
 * Created by abhimanyu
 */
public class NoMoreWordsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_more_words);
        Typeface noMoreTf = Typeface.createFromAsset(getAssets(),"fonts/opensans_semi_bold.ttf");
        TextView noMoreWordsView = findViewById(R.id.noWordsMsg);
        noMoreWordsView.setTypeface(noMoreTf);
        Button yesBt = findViewById(R.id.button_reset);
        yesBt.setTypeface(noMoreTf);
        yesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Button noBt = findViewById(R.id.button_no_reset);
        noBt.setTypeface(noMoreTf);
    }
}
