package com.mooo.pantasya.pong;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Random;

public class Ball {

    private float x, y, size;
    private float speedX, speedY;
    private final Random random = new Random();

    public Ball(float size) {
        this.size = size;
    }

    public void reset(int screenX, int screenY) {
        x = random.nextInt((int) (screenX - size));
        y = random.nextInt(screenY / 2);
        speedX = random.nextBoolean() ? 10 : -10;
        speedY = 10;
    }

    public void update() {
        x += speedX;
        y += speedY;
    }

    public void bounceX() {
        speedX = -speedX;
    }

    public void bounceY() {
        speedY = -speedY;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(x, y, x + size, y + size, paint);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getSize() {
        return size;
    }

    public void setY(float y) {
        this.y = y;
    }
}