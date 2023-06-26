package com.example.savethebunny;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;

import androidx.core.content.res.ResourcesCompat;

import java.util.ArrayList;
import java.util.Random;


public class GameView extends View {

    Bitmap background, ground, rabbit;
    Rect rectBackground, rectGround;
    Context context;
    Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint textPaint = new Paint();
    Paint healthPaint = new Paint();
    float TEXT_SIZE = 120;
    int points = 0;
    int life = 3;
    static int dWidth, dHeigth;
    Random random;
    float rabbitX, rabbitY;
    float oldX;
    float oldRabbitX;
    ArrayList<Bomb> bombs ;
    ArrayList<Explosion> explosions ;
    public GameView(Context context) {
        super(context);
        this.context = context;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        ground = BitmapFactory.decodeResource(getResources(), R.drawable.ground);
        rabbit = BitmapFactory.decodeResource(getResources(), R.drawable.rabbit);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeigth = size.y;
        rectBackground = new Rect(0,0,dWidth,dHeigth);
        rectGround = new Rect(0,dHeigth - ground.getHeight(),dWidth,dHeigth);
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        textPaint.setColor(Color.rgb(225,165,0));
        textPaint.setTextSize(TEXT_SIZE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTypeface(ResourcesCompat.getFont(context, R.font.kenney_blocks));
        healthPaint.setColor(Color.GREEN);
        random = new Random();
        rabbitX = dWidth / 2 - rabbit.getWidth() / 2;
        rabbitY = dHeigth - ground.getHeight() - rabbit.getHeight();
        bombs = new ArrayList<>();
        explosions = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            Bomb bomb = new Bomb(context);
            bombs.add(bomb);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(background, null, rectBackground, null);
        canvas.drawBitmap(ground,null,rectGround,null);
        canvas.drawBitmap(rabbit,rabbitX,rabbitY,null);
        for(int i = 0; i<bombs.size();i++){
            canvas.drawBitmap(bombs.get(i).getBomb(bombs.get(i).bombFrame),bombs.get(i).bombX, bombs.get(i).bombY, null);
            bombs.get(i).bombFrame++;
            if(bombs.get(i).bombFrame > 2){
                bombs.get(i).bombFrame = 0;
            }
            bombs.get(i).bombY += bombs.get(i).bombVelocity;
            if(bombs.get(i).bombY + bombs.get(i).getBombHeight() >= dHeigth - ground.getHeight()){
                points += 10;
                Explosion explosion = new Explosion(context);
                explosion.explosionX = bombs.get(i).bombX;
                explosion.explosionY = bombs.get(i).bombY;
                explosions.add(explosion);
                bombs.get(i).resetPosition();
            }
        }

        for(int i = 0; i < bombs.size(); i++){
            if(bombs.get(i).bombX + bombs.get(i).getBombWidth() >= rabbitX
            && bombs.get(i).bombX <= rabbitX + rabbit.getWidth()
                    && bombs.get(i).bombY + bombs.get(i).getBombWidth() >= rabbitY
            && bombs.get(i).bombY + bombs.get(i).getBombWidth() <= rabbitY + rabbit.getHeight()){
                life--;
                bombs.get(i).resetPosition();
                if(life == 0){
                    Intent intent = new Intent(context, GameOver.class);
                    intent.putExtra("points",points);
                    context.startActivity(intent);
                    ((Activity) context).finish();;
                }
            }
        }
        for (int i = 0; i < explosions.size(); i++){
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).explosionX,
                    explosions.get(i).explosionY, null);
            explosions.get(i).explosionFrame++;
            if(explosions.get(i).explosionFrame > 3){
                explosions.remove(i);
            }
        }

        if(life == 2){
            healthPaint.setColor(Color.YELLOW);
        }else if(life == 1){
            healthPaint.setColor(Color.RED);
        }

        canvas.drawRect(dWidth-200,30,dWidth-200+60*life, 80,healthPaint);
        canvas.drawText(""  + points, 20, TEXT_SIZE, textPaint);
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        if(touchY >= rabbitY){
            int action = event.getAction();
            if(action == MotionEvent.ACTION_DOWN){
                oldX = event.getX();
                oldRabbitX = rabbitX;
            }
            if(action == MotionEvent.ACTION_MOVE){
                float shift = oldX - touchX;
                float newRabbitX = oldRabbitX - shift;
                if(newRabbitX <= 0)
                    rabbitX = 0;
                else if(newRabbitX >= dWidth - rabbit.getWidth())
                    rabbitX = dWidth - rabbit.getWidth();
                else
                    rabbitX = newRabbitX;
            }
        }
        return true;
    }
}
