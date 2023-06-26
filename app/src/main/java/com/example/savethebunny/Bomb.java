package com.example.savethebunny;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class Bomb {
    Bitmap bomb[] = new Bitmap[3];
    int bombFrame = 0;
    int bombX, bombY, bombVelocity;
    Random random;

    public Bomb(Context context){
        bomb[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb);
        bomb[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb);
        bomb[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomb);
        random = new Random();
        resetPosition();
    }

    public Bitmap getBomb(int bombFrame){
        return  bomb[bombFrame];
    }

    public int getBombWidth(){
        return bomb[0].getWidth();
    }

    public int getBombHeight(){
        return bomb[0].getHeight();
    }

    public void resetPosition(){
        bombX = random.nextInt(GameView.dWidth - getBombWidth());
        bombY = -200 + random.nextInt(600) * -1;
        bombVelocity = 35 + random.nextInt(16);
    }
}
