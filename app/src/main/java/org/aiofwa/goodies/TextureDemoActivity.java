package org.aiofwa.goodies;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;

import java.util.ArrayList;
import java.util.Random;

public class TextureDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TextureView textureView = new TextureView(this);
        setContentView(textureView);
        thread = new TextureRenderThread(textureView,  this, 1.0f);
        thread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Intent intent = new Intent(this, RotatingTextureDemoActivity.class);
        thread.halt();
        startActivity(intent);
        finish();
        return super.onTouchEvent(event);
    }

    TextureRenderThread thread;
}

// TextureRenderThread 是绘画的线程，这个线程会绘制圆圈，并且随着时间越绘制越多，意图卡住手机
// 在这里还加入了 acc 作为加速圆圈增加的速度的参数，假若为 0 就是正常的圆圈缩放动画
class TextureRenderThread extends Thread {
    TextureRenderThread() {}
    TextureRenderThread(TextureView view, Activity activity, float acc) {
        this.view = view;
        random = new Random(System.currentTimeMillis());
        circles = new ArrayList<>();
        this.activity = activity;
        this.acc = acc;
        this.cb = null;
    }

    @Override
    public void run() {
        drawing = true;
        canvas = view.lockCanvas();
        view.unlockCanvasAndPost(canvas);
        counter = 0.0f;
        threshold = 1.0f;
        total = 0.0f;
        long justNow = System.currentTimeMillis();
        while (drawing) {
            long current = System.currentTimeMillis();
            float delta = (float) (current - justNow) / 1000.0f;
            justNow = current;
            canvas = view.lockCanvas();
            if (canvas == null) {
                continue;
            }
            draw(delta);
            view.unlockCanvasAndPost(canvas);
        }
    }

    public void halt() {
        drawing = false;
    }

    void draw(final float delta) {
        if (this.acc == 0.0f) {
            view.setRotation(total);
            view.setAlpha((float) Math.abs(Math.sin(total)));
        }
        counter += delta;
        total += delta;
        if (cb != null) {
            cb.cb(total);
        }
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
            threshold -= 0.035f * acc;
            if (threshold <= 0.0f) {
                threshold = 0.001f;
            }
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.setTitle("Texture 圆圈数：" + circles.size() + " 个 " + (1.0f / delta));
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
    boolean drawing;
    Random random;
    float counter;
    float threshold;
    float acc;
    public float total;
    TextureView view;
    ArrayList<Circle> circles;
    Activity activity;
    public CB cb;
}

interface CB {
    void cb(float total);
}