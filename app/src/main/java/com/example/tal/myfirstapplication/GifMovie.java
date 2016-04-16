package com.example.tal.myfirstapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Movie;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.io.InputStream;

/**
 * Created by gront on 16/04/2016.
 */
public class GifMovie extends View {
    Movie movie;
    private long movieStart;
    private int gifId;

    public GifMovie(Context context, InputStream is) {
        super(context);
        movie = Movie.decodeStream(is);
    }

    public GifMovie(Context context) {
        super(context);
        initializeView();
    }

    public GifMovie(Context context, AttributeSet attrs) {
        super(context,attrs);
        initializeView();
    }

    public GifMovie(Context context, AttributeSet attrs, int defStyle) {
        super(context,attrs,defStyle);
        initializeView();
    }

    private void initializeView(){
        InputStream is = getContext().getResources().openRawResource(R.raw.crown2);
        movie = Movie.decodeStream(is);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(Color.TRANSPARENT);
        super.onDraw(canvas);
        long now = SystemClock.uptimeMillis();
        if (movieStart == 0) {
            movieStart = now;
        }
        if (movie != null){
            int relTime = (int) ((now - movieStart)% movie.duration());
            movie.setTime(relTime);
            movie.draw(canvas, (getHeight() - movie.height())/2, (getWidth() - movie.width())/2);
            this.invalidate();
        }
    }

    public void setGifResource(int resId){
        this.gifId = resId;
        initializeView();
    }

    public int getGifResource() {
        return gifId;
    }
}
