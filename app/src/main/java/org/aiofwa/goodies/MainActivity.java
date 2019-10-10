package org.aiofwa.goodies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.telecom.Call;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.Random;


public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SurfaceView surfaceView = findViewById(R.id.surfaceView);
        SurfaceView surfaceView2 = findViewById(R.id.surfaceView2);
        holder = surfaceView.getHolder();
        holder2 = surfaceView2.getHolder();
        callback = new FlawCallback(holder);
        callback2 = new FlawCallback(holder2);
    }

    public void first(View view) {
        callback.halt();
        callback2.halt();
        holder2.removeCallback(callback2);
        holder.removeCallback(callback);
        holder.addCallback(callback);
        callback.start();
    }

    public void second(View view) {
        callback.halt();
        callback2.halt();
        holder2.removeCallback(callback2);
        holder.removeCallback(callback);
        holder2.addCallback(callback2);
        callback2.start();
    }

    public void together(View view) {
        callback.halt();
        callback2.halt();
        holder2.removeCallback(callback2);
        holder.removeCallback(callback);
        holder.addCallback(callback);
        holder2.addCallback(callback2);
        callback.start();
        callback2.start();
    }

    SurfaceHolder holder;
    SurfaceHolder holder2;
    FlawCallback callback;
    FlawCallback callback2;
}

class FlawCallback implements SurfaceHolder.Callback {
    FlawCallback() {}
    FlawCallback(SurfaceHolder holder) {
        this.holder = holder;
        thread = new FlawRenderThread(holder);
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
        thread = new FlawRenderThread(holder);
    }

    public void start() {
        thread.start();
    }

    Canvas canvas;
    SurfaceHolder holder;
    FlawRenderThread thread;
    Color background;
}

class FlawRenderThread extends Thread {
    FlawRenderThread() {}
    FlawRenderThread(SurfaceHolder holder) {
        this.holder = holder;
        random = new Random(System.currentTimeMillis());
    }

    static int r = 0, g = 0, b = 0;

    @Override
    public void run() {
        drawing = true;
        canvas = holder.lockCanvas();
        canvas.drawRGB(r, g, b);
        r += 10;
        g = b = r;
        holder.unlockCanvasAndPost(canvas);
        while (drawing) {
            canvas = holder.lockCanvas();
            if (canvas == null) {
                continue;
            }
            draw();
            holder.unlockCanvasAndPost(canvas);
        }
    }

    public void halt() {
        drawing = false;
    }

    void draw() {
//        canvas.drawRGB(0, 0, 0);
        int left = random.nextInt((int) (250 * 2.5));
        int right = left + random.nextInt((int) (289 * 2.5) - left) + 1;
        int bot = random.nextInt((int) (460 * 2.5));
        int top = bot + random.nextInt((int) (479 * 2.5) - bot) + 1;
        canvas.drawRect(left, top, right, bot, paint(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
//        canvas.drawRect(10.0f, 10.0f, 50.0f, 50.0f, paint(255, 253, 232, 255));
        try {
            sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    Paint paint(int a, int r, int g, int b) {
        Paint paint = new Paint();
        paint.setARGB(a, r, g, b);
        return paint;
    }

    Canvas canvas;
    SurfaceHolder holder;
    boolean drawing;
    Random random;
}
