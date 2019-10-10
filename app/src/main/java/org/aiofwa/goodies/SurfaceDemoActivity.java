package org.aiofwa.goodies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.ActionMenuView;

import java.util.ArrayList;
import java.util.Random;


public class SurfaceDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SurfaceView surfaceView = new SurfaceView(this);
        surfaceView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        setContentView(surfaceView);
        callback = new Callback(surfaceView, this);
        surfaceView.getHolder().addCallback(callback);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Intent intent = new Intent(this, TextureDemoActivity.class);
        callback.halt();
        startActivity(intent);
        finish();
        return super.onTouchEvent(event);
    }

    Callback callback;
}

class Callback implements SurfaceHolder.Callback {
    Callback() {}
    Callback(SurfaceView view, Activity activity) {
        this.view = view;
        this.holder = view.getHolder();
        thread = new RenderThread(view, activity);
        this.activity = activity;
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        thread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        halt();
    }

    public void halt() {
        thread.halt();
        thread = new RenderThread(view, activity);
    }

    public void start() {
        thread.start();
    }

    Canvas canvas;
    SurfaceHolder holder;
    SurfaceView view;
    RenderThread thread;
    Activity activity;
    Color background;
}

// RenderThread 是绘画的线程，这个线程会绘制圆圈，并且随着时间越绘制越多，意图卡住手机
class RenderThread extends Thread {
    RenderThread() {}
    RenderThread(SurfaceView view, Activity activity) {
        this.view = view;
        this.holder = view.getHolder();
        random = new Random(System.currentTimeMillis());
        circles = new ArrayList<>();
        this.activity = activity;
    }

    @Override
    public void run() {
        drawing = true;
        canvas = holder.lockCanvas();
        holder.unlockCanvasAndPost(canvas);
        counter = 0.0f;
        threshold = 1.0f;
        long justNow = System.currentTimeMillis();
        while (drawing) {
            long current = System.currentTimeMillis();
            float delta = (float) (current - justNow) / 1000.0f;
            justNow = current;
            canvas = holder.lockCanvas();
            if (canvas == null) {
                continue;
            }
            draw(delta);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void halt() {
        drawing = false;
    }

    void draw(final float delta) {
        counter += delta;
        if (counter >= threshold) {
            int randX = random.nextInt(view.getWidth());
            int randY = random.nextInt(view.getHeight());
            int r = random.nextInt(100) + 100;
            circles.add(new Circle(randX, randY, r, paint(
                    255,
                    random.nextInt(255),
                    random.nextInt(255),
                    random.nextInt(255)
            )));
            counter = 0.0f;
            threshold -= 0.035f;
            if (threshold <= 0.0f) {
                threshold = 0.001f;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.setTitle("Surface 圆圈数：" + circles.size() + " 个 " + (1.0f / delta));
                }
            });
        }

        canvas.drawRGB(0, 0, 0);
        for (Circle circle : circles) {
            canvas.drawCircle(circle.x, circle.y, circle.r, circle.paint);
            circle.update(delta);
        }
    }

    Paint paint(int a, int r, int g, int b) {
        Paint paint = new Paint();
        paint.setARGB(a, r, g, b);
        return paint;
    }

    Canvas canvas;
    SurfaceHolder holder;
    SurfaceView view;
    boolean drawing;
    Random random;
    float counter;
    float threshold;
    ArrayList<Circle> circles;
    Activity activity;
}

class Circle {
    Circle() {}
    Circle(int x, int y, int r, Paint paint) {
        this.x = x;
        this.y = y;
        this.r = r;
        this.paint = paint;
    }

    void update(float delta) {
        r += delta * r * 10.0f;
    }

    float x, y, r;
    Paint paint;
}
