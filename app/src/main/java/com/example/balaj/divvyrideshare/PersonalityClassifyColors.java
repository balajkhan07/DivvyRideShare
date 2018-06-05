package com.example.balaj.divvyrideshare;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PersonalityClassifyColors extends AppCompatActivity {

    Button red;
    Button blue;
    Button white;
    Button black;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personality_classify_colors);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        myToolbar.setBackgroundColor(Color.rgb(92, 0, 0));
        myToolbar.setTitleTextColor(Color.WHITE);
        setTitle("PERSONALITY CLASSIFICATION");

        red = findViewById(R.id.red);
        blue = findViewById(R.id.blue);
        white = findViewById(R.id.white);
        black = findViewById(R.id.black);
        textView = findViewById(R.id.perClassify);

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText("Love And Intense Emotions");
            }
        });

        blue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText("Calm And Relaible");
            }
        });

        black.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText("Danger But Also Sophistication");
            }
        });

        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView.setText("Innocence And Goodness");
            }
        });
    }
}
