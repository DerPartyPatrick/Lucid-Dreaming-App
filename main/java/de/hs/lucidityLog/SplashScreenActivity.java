package de.hs.lucidityLog;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import de.hs.lucidityLog.testing.MyIdlingRessource;

public class SplashScreenActivity extends AppCompatActivity {
    private final int delay = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        Intent i = new Intent(this, MainActivity.class);

        MyIdlingRessource.getIdlingResource().increment(); //testing sync
        new Handler().postDelayed(() -> {
            MyIdlingRessource.getIdlingResource().decrement(); //testing sync
            startActivity(i);
        }, delay);
    }
}
