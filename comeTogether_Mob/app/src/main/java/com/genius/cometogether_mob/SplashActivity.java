package com.genius.cometogether_mob;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class SplashActivity extends AppCompatActivity {
    private ImageView ucfiv;
    private ImageView comeTogetheriv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ucfiv = findViewById(R.id.ucfLogo);
        comeTogetheriv = findViewById(R.id.comeTogetherLogo);
        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.mytransition);
        ucfiv.startAnimation(myanim);
        comeTogetheriv.startAnimation(myanim);
        final Intent i = new Intent(this, LoginActivity.class);
        Thread timer = new Thread()
        {
            public void run ()
            {
                try
                {
                    sleep(5000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    startActivity(i);
                    finish();
                }
            }
        };

        timer.start();
    }
}
