package com.mooo.pantasya.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class PongView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private Thread gameThread;
    private volatile boolean playing;

    private final SurfaceHolder holder;
    private final Paint paint;
    private final MainActivity activity;
    private final Random random = new Random();

    private int screenX, screenY;
    private Paddle paddle;
    private Ball ball;

    private int score = 0;
    private int highScore = 0;
    private boolean initialized = false;
    private boolean surfaceReady = false;

    public PongView(Context context, int savedHighScore) {
        super(context);
        activity = (MainActivity) context;
        holder = getHolder();
        holder.addCallback(this);
        paint = new Paint();
        highScore = savedHighScore;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        surfaceReady = true;
        if (ball != null) ball.reset(screenX, screenY);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        surfaceReady = false;
        pause();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        screenX = width;
        screenY = height;

        float paddleW = screenX / 4f;
        float paddleH = screenY / 30f;
        float paddleX = (screenX - paddleW) / 2f;
        float paddleY = screenY - 120;

        paddle = new Paddle(paddleX, paddleY, paddleW, paddleH);
        ball = new Ball(screenX / 20f);

        initialized = true;
        ball.reset(screenX, screenY);
    }

    @Override
    public void run() {
        while (playing) {
            if (surfaceReady && initialized) {
                update();
                drawGame();
            }
            sleep();
        }
    }

    private void update() {
        ball.update();

        if (ball.getX() <= 0 || ball.getX() + ball.getSize() >= screenX) {
            ball.bounceX();
        }

        if (ball.getY() <= 0) {
            ball.bounceY();
        }

        if (ball.getY() + ball.getSize() >= paddle.getY() &&
            ball.getY() + ball.getSize() <= paddle.getY() + paddle.getH() &&
            ball.getX() + ball.getSize() >= paddle.getX() &&
            ball.getX() <= paddle.getX() + paddle.getW() &&
            ballSpeedDown()) {
            ball.bounceY();
            ball.setY(paddle.getY() - ball.getSize() - 1);
            score++;
            if (score > highScore) highScore = score;
        }

        if (ball.getY() > screenY) {
            activity.updateHighScore(score);
            score = 0;
            ball.reset(screenX, screenY);
        }
    }

    private boolean ballSpeedDown() {
        return true;
    }

    private void drawGame() {
        if (!holder.getSurface().isValid()) return;

        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;

        canvas.drawColor(Color.BLACK);
        paint.setColor(Color.WHITE);

        paddle.draw(canvas, paint);
        ball.draw(canvas, paint);

        paint.setTextSize(60);
        canvas.drawText("Score: " + score, 40, 80, paint);
        canvas.drawText("High: " + highScore, 40, 150, paint);

        holder.unlockCanvasAndPost(canvas);
    }

    private void sleep() {
        try {
            Thread.sleep(16);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void resume() {
        if (gameThread == null || !gameThread.isAlive()) {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }
    }

    public void pause() {
        playing = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            gameThread = null;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (paddle == null) return true;

        float x = event.getX() - paddle.getW() / 2f;
        if (x < 0) x = 0;
        if (x + paddle.getW() > screenX) x = screenX - paddle.getW();

        paddle.setX(x);
        return true;
    }
}


