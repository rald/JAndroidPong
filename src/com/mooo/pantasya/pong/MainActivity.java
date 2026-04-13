package com.mooo.pantasya.pong;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends Activity {

    private PongView gameView;
    private SharedPreferences prefs;
    private int highScore = 0;

    public static final String PREFS_NAME = "pong_prefs";
    public static final String KEY_HIGH_SCORE = "high_score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        highScore = prefs.getInt(KEY_HIGH_SCORE, 0);

        gameView = new PongView(this, highScore);
        setContentView(gameView);
    }

    public void updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            prefs.edit().putInt(KEY_HIGH_SCORE, highScore).apply();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
        updateHighScore(gameView.getScore());
    }
}