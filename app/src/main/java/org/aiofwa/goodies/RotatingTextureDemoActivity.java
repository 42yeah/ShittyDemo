package org.aiofwa.goodies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.TextureView;
import android.widget.AbsoluteLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;


public class RotatingTextureDemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rotating_texture_demo);
        final TextureView textureView = findViewById(R.id.textureView);
        final TextureRenderThread thread = new TextureRenderThread(textureView,  this, 0.0f);
        final Random random = new Random(System.currentTimeMillis());
        final AbsoluteLayout layout = findViewById(R.id.absolute);
        threads = new ArrayList<>();
        threads.add(thread);
        thread.cb = new CB() {
            @Override
            public void cb(float total) {
                int i = Math.round(total) + 1;
                if (i % 10 == 0) {
                    thread.total++;
                    final TextureView textureView1 = new TextureView(RotatingTextureDemoActivity.this);
                    textureView1.setX(textureView.getX() + random.nextInt(50));
                    textureView1.setY(textureView.getY() + random.nextInt(50));
                    textureView1.setLayoutParams(textureView.getLayoutParams());
                    TextureRenderThread thread1 = new TextureRenderThread(textureView1,  RotatingTextureDemoActivity.this, 0.0f);
                    thread1.start();
                    threads.add(thread1);
                    RotatingTextureDemoActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            layout.addView(textureView1);
                        }
                    });
                }
            }
        };
        thread.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Intent intent = new Intent(this, MainActivity.class);
        for (TextureRenderThread thread : threads) {
            thread.halt();
        }
        startActivity(intent);
        finish();
        return super.onTouchEvent(event);
    }

    ArrayList<TextureRenderThread> threads;
}
