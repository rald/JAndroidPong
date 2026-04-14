package com.mooo.pantasya.pong;

import android.graphics.Canvas;
import android.graphics.Paint;

public class Paddle {

    private float x, y, w, h;

    public Paddle(float x, float y, float w, float h) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getW() {
        return w;
    }

    public float getH() {
        return h;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawRect(x, y, x + w, y + h, paint);
    }
}