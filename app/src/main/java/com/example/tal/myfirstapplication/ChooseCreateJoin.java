package com.example.tal.myfirstapplication;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import static android.graphics.Paint.Align.*;

public class ChooseCreateJoin extends AppCompatActivity {

    ImageView table;
    ImageButton create;
    ImageButton join;
    Point po;
    Intent intent;
    MediaPlayer tableSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_create_join);

        join = (ImageButton) findViewById(R.id.join);
        create = (ImageButton) findViewById(R.id.create);
        table = (ImageView) findViewById(R.id.table);
        po =  new Point();
        this.getWindowManager().getDefaultDisplay().getSize(po);

        initLayout();
    }

    private void initLayout() {
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) join.getLayoutParams();
        lp.height = po.x/2;
        lp.width = po.x/2;
        lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        join.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) create.getLayoutParams();
        lp.height = po.x/2;
        lp.width = po.x/2;
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL);
        create.setLayoutParams(lp);

        lp = (RelativeLayout.LayoutParams) table.getLayoutParams();
        lp.width = po.x;
        lp.height = po.y;
        table.setVisibility(View.INVISIBLE);
        table.setLayoutParams(lp);

        tableSound = MediaPlayer.create(this, R.raw.table);
    }

    @Override
    protected void onStart() {
        super.onStart();
        setButtons();
    }

    private void setButtons(){
        float createY = create.getY();
        float joinY = join.getY();

        TranslateAnimation transAnimCreate = new TranslateAnimation(0, 0, po.y,createY);
        TranslateAnimation transAnimJoin = new TranslateAnimation(0, 0, -po.y,joinY);
        transAnimJoin.setStartOffset(1000);
        transAnimCreate.setStartOffset(1000);
        transAnimJoin.setDuration(500);
        transAnimCreate.setDuration(500);
        create.startAnimation(transAnimCreate);
        join.startAnimation(transAnimJoin);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_first, menu);
        return true;
    }
    public void createNewRoom(View view){

        intent = new Intent(this, CreateRoom.class);
        bouncingTable();
    }

    public void joinRoom(View view){
        intent = new Intent(this, JoinRoom.class);
        bouncingTable();
    }

    public void settings(){
        intent = new Intent(this,FirstLogIn.class);
        startActivity(intent);
    }

    private void bouncingTable(){
        table.setVisibility(View.VISIBLE);
        table.clearAnimation();
        tableSound.start();
        TranslateAnimation transAnim = new TranslateAnimation(0, 0, -table.getHeight(),0);
        transAnim.setStartOffset(500);
        transAnim.setDuration(1500);
        transAnim.setFillAfter(true);
        transAnim.setInterpolator(new BounceInterpolator());
        transAnim.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                table.clearAnimation();
                final int left = table.getLeft();
                final int top = table.getTop();
                final int right = table.getRight();
                final int bottom = table.getBottom();
                table.layout(left, top, right, bottom);

                startActivity(intent);
            }
        });
        table.startAnimation(transAnim);



    }

    private int getDisplayHeight() {
        return this.getResources().getDisplayMetrics().heightPixels;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            settings();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
