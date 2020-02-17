package edu.csc4360.thescotchdatabase;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;


public class FrontPage extends AppCompatActivity {

    private static int WELCOME_TIMEOUT = 3000;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_front_page);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent welcome = new Intent(FrontPage.this, MainActivity.class);
                startActivity(welcome);
                overridePendingTransition(R.animator.fade_in, R.animator.fade_out);
                finish();
            }
        }, WELCOME_TIMEOUT);


        imageView = (ImageView) findViewById(R.id.imageView);
        ((AnimationDrawable) imageView.getDrawable()).start();


    }
}
