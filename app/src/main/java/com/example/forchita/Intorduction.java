package com.example.forchita;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.airbnb.lottie.LottieAnimationView;

public class Intorduction extends AppCompatActivity {
    ImageView img;
    LottieAnimationView lottieAnimationView;
    private static int time=4300;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intorduction);
        lottieAnimationView = findViewById(R.id.lottie);

        lottieAnimationView.animate().translationY(1400).setDuration(1000).setStartDelay(4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(Intorduction.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },time);
    }
}
