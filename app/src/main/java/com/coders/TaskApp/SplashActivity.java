package com.coders.TaskApp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashActivity extends AppCompatActivity {


    Animation top, bottom;
    TextView name;
    ImageView icon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        name=findViewById(R.id.text);
        icon=findViewById(R.id.icon);

        top= AnimationUtils.loadAnimation(this,R.anim.bottom_to_top);
        name.setAnimation(top);

        bottom=AnimationUtils.loadAnimation(this,R.anim.top_to_bottom);
        icon.setAnimation(bottom);

        top.startNow();bottom.startNow();

        TodoData todoData = new TodoData(this);
        todoData.readData();

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        },Math.max(top.getDuration(),bottom.getDuration()));

    }
}