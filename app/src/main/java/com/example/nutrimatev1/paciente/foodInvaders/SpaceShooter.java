package com.example.nutrimatev1.paciente.foodInvaders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.nutrimatev1.R;

import java.util.ArrayList;
import java.util.Random;

public class SpaceShooter extends View {
    Context context;
    Bitmap background, lifeImage;
    Handler handler;
    long UPDATE_MILLIS = 30;
    static int screenWidth, screenHeight;
    int points = 0;
    int life = 3;
    Paint scorePaint, levelPaint;
    int TEXT_SIZE = 80;
    int LEVEL_TEXT_SIZE = 120;
    boolean paused = false;
    OurSpaceship ourSpaceship;
    EnemySpaceship enemySpaceship;
    Random random;
    ArrayList<Shot> enemyShots, ourShots;
    boolean enemyExplosion = false;
    Explosion explosion;
    ArrayList<Explosion> explosions;
    long lastEnemyShotTime = 0;
    long enemyShotInterval = 2000;
    int lastMilestone = 0;
    int level = 1;
    boolean showLevelText = true;
    long levelTextStartTime = 0;

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public SpaceShooter(Context context) {
        super(context);
        this.context = context;
        random = new Random();
        enemyShots = new ArrayList<>();
        ourShots = new ArrayList<>();
        explosions = new ArrayList<>();
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        ourSpaceship = new OurSpaceship(context);
        enemySpaceship = new EnemySpaceship(context);
        background = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.background);
        lifeImage = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.life);
        handler = new Handler();
        scorePaint = new Paint();
        scorePaint.setColor(Color.RED);
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);

        levelPaint = new Paint();
        levelPaint.setColor(Color.WHITE);
        levelPaint.setTextSize(LEVEL_TEXT_SIZE);
        levelPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawText("Pt: " + points, 0, TEXT_SIZE, scorePaint);
        for (int i = life; i >= 1; i--) {
            canvas.drawBitmap(lifeImage,
                    screenWidth - lifeImage.getWidth() * i, 0, null);
        }
        if (life == 0) {
            paused = true;
            handler = null;
            Intent intent = new Intent(context, GameOver.class);
            intent.putExtra("points", points);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
        enemySpaceship.ex += enemySpaceship.enemyVelocity;
        if (enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() >= screenWidth) {
            enemySpaceship.enemyVelocity *= -1;
        }
        if (enemySpaceship.ex <= 0) {
            enemySpaceship.enemyVelocity *= -1;
        }

        if (System.currentTimeMillis() - lastEnemyShotTime >= enemyShotInterval) {
            Shot enemyShot = new Shot(context, enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() / 2, enemySpaceship.ey);
            enemyShots.add(enemyShot);
            lastEnemyShotTime = System.currentTimeMillis();
        }

        if (!enemyExplosion) {
            canvas.drawBitmap(enemySpaceship.getEnemySpaceship(), enemySpaceship.ex, enemySpaceship.ey, null);
        }
        if (ourSpaceship.isAlive = true) {
            if (ourSpaceship.ox > screenWidth - ourSpaceship.getOurSpaceshipWidth()) {
                ourSpaceship.ox = screenWidth - ourSpaceship.getOurSpaceshipWidth();
            } else if (ourSpaceship.ox < 0) {
                ourSpaceship.ox = 0;
            }
            canvas.drawBitmap(ourSpaceship.getOurSpaceship(), ourSpaceship.ox, ourSpaceship.oy, null);
        }

        for (int i = 0; i < enemyShots.size(); i++) {
            enemyShots.get(i).shy += 15;
            canvas.drawBitmap(enemyShots.get(i).getShot(), enemyShots.get(i).shx, enemyShots.get(i).shy, null);
            if ((enemyShots.get(i).shx >= ourSpaceship.ox)
                    && (enemyShots.get(i).shx <= ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth())
                    && (enemyShots.get(i).shy >= ourSpaceship.oy)
                    && (enemyShots.get(i).shy <= screenHeight)) {
                life--;
                enemyShots.remove(i);
                explosion = new Explosion(context, ourSpaceship.ox, ourSpaceship.oy);
                explosions.add(explosion);
            } else if (enemyShots.get(i).shy >= screenHeight) {
                enemyShots.remove(i);
            }
        }

        if (points >= lastMilestone + 10) {
            lastMilestone += 10;
            enemyShotInterval *= 0.8;
            enemyShotInterval = Math.max(250, enemyShotInterval);
            level++;
            showLevelText = true;
            levelTextStartTime = System.currentTimeMillis();
        }

        if (showLevelText) {
            canvas.drawText("LEVEL " + level, screenWidth / 2, screenHeight / 2, levelPaint);
            if (System.currentTimeMillis() - levelTextStartTime >= 2000) {
                showLevelText = false;
            }
        }

        for (int i = 0; i < ourShots.size(); i++) {
            ourShots.get(i).shy -= 15;
            canvas.drawBitmap(ourShots.get(i).getShot(), ourShots.get(i).shx, ourShots.get(i).shy, null);
            if ((ourShots.get(i).shx >= enemySpaceship.ex)
                    && (ourShots.get(i).shx <= enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth())
                    && (ourShots.get(i).shy <= enemySpaceship.getEnemySpaceshipHeight())
                    && (ourShots.get(i).shy >= enemySpaceship.ey)) {
                points++;
                ourShots.remove(i);
                explosion = new Explosion(context, enemySpaceship.ex, enemySpaceship.ey);
                explosions.add(explosion);
            } else if (ourShots.get(i).shy <= 0) {
                ourShots.remove(i);
            }
        }

        for (int i = 0; i < explosions.size(); i++) {
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame),
                    explosions.get(i).eX,
                    explosions.get(i).eY,
                    null);
            explosions.get(i).explosionFrame++;
            if (explosions.get(i).explosionFrame > 8) {
                explosions.remove(i);
            }
        }

        if (!paused) {
            handler.postDelayed(runnable, UPDATE_MILLIS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (ourShots.size() < 3) {
                Shot ourShot = new Shot(context, ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth() / 2, ourSpaceship.oy);
                ourShots.add(ourShot);
            }
        }
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ourSpaceship.ox = touchX;
        }
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            ourSpaceship.ox = touchX;
        }
        return true;
    }
}
