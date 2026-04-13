package com.mooo.pantasya.pong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Random;

public class PongView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private volatile boolean playing;

    private final SurfaceHolder holder;
    private final Paint paint;
    private final MainActivity activity;
    private final Random random = new Random();

    private int screenX, screenY;
    private float paddleX, paddleY, paddleW, paddleH;
    private float ballX, ballY, ballSize;
    private float ballSpeedX = 10, ballSpeedY = 10;

    private int score = 0;
    private int highScore = 0;
    private boolean initialized = false;

    public PongView(Context context, int savedHighScore) {
        super(context);
        activity = (MainActivity) context;
        holder = getHolder();
        paint = new Paint();
        highScore = savedHighScore;
    }

    public int getScore() {
        return score;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        screenX = w;
        screenY = h;

        paddleW = screenX / 4f;
        paddleH = screenY / 30f;
        paddleX = (screenX - paddleW) / 2f;
        paddleY = screenY - 120;

        ballSize = screenX / 20f;
        initialized = true;
        resetBall();
    }

    @Override
    public void run() {
        while (playing) {
            update();
            drawGame();
            sleep();
        }
    }

    private void update() {
        ballX += ballSpeedX;
        ballY += ballSpeedY;

        if (ballX <= 0 || ballX + ballSize >= screenX) {
            ballSpeedX = -ballSpeedX;
        }

        if (ballY <= 0) {
            ballSpeedY = -ballSpeedY;
        }

        if (ballY + ballSize >= paddleY &&
            ballY + ballSize <= paddleY + paddleH &&
            ballX + ballSize >= paddleX &&
            ballX <= paddleX + paddleW &&
            ballSpeedY > 0) {
            ballSpeedY = -ballSpeedY;
            ballY = paddleY - ballSize - 1;
            score++;
            if (score > highScore) {
                highScore = score;
            }
        }

        if (ballY > screenY) {
            activity.updateHighScore(score);
            score = 0;
            resetBall();
        }
    }

    private void drawGame() {
        if (!holder.getSurface().isValid()) return;

        Canvas canvas = holder.lockCanvas();
        if (canvas == null) return;

        canvas.drawColor(Color.BLACK);

        paint.setColor(Color.WHITE);
        canvas.drawRect(paddleX, paddleY, paddleX + paddleW, paddleY + paddleH, paint);
        canvas.drawRect(ballX, ballY, ballX + ballSize, ballY + ballSize, paint);

        paint.setTextSize(60);
        canvas.drawText("Score: " + score, 40, 80, paint);
        canvas.drawText("High: " + highScore, 40, 150, paint);

        holder.unlockCanvasAndPost(canvas);
    }

    private void resetBall() {
        if (!initialized) return;

        ballX = random.nextInt((int) (screenX - ballSize));
        ballY = random.nextInt(screenY / 2);

        ballSpeedX = random.nextBoolean() ? 10 : -10;
        ballSpeedY = 10;
    }

    private void sleep() {
        try {
            Thread.sleep(16);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void pause() {
        playing = false;
        if (gameThread != null) {
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        paddleX = event.getX() - paddleW / 2f;

        if (paddleX < 0) paddleX = 0;
        if (paddleX + paddleW > screenX) paddleX = screenX - paddleW;

        return true;
    }
}