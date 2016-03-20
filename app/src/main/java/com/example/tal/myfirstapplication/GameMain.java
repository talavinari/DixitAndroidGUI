package com.example.tal.myfirstapplication;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.view.ViewGroup.LayoutParams;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class GameMain extends Activity implements View.OnClickListener, View.OnTouchListener,View.OnDragListener  {
    int size;
    boolean dragging;
    Context context;

    View draggedView;
    ImageView dropped;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_game_main);
        context = this;
        setCardsInPosition();
    }

    public void setCardsInPosition() {

        Point po = new Point();
        this.getWindowManager().getDefaultDisplay().getSize(po);
        size = po.x / 19;


        ImageView card1 = (ImageView) findViewById(R.id.card1);
        ImageView card2 = (ImageView) findViewById(R.id.card2);
        ImageView card3 = (ImageView) findViewById(R.id.card3);
        ImageView card4 = (ImageView) findViewById(R.id.card4);
        ImageView card5 = (ImageView) findViewById(R.id.card5);
        ImageView card6 = (ImageView) findViewById(R.id.card6);

        setAllCards(size * 2, size * 4);

        card1.setOnClickListener(this);
        card2.setOnClickListener(this);
        card3.setOnClickListener(this);
        card4.setOnClickListener(this);
        card5.setOnClickListener(this);
        card6.setOnClickListener(this);

        card1.setOnTouchListener(this);
        card2.setOnTouchListener(this);
        card3.setOnTouchListener(this);
        card4.setOnTouchListener(this);
        card5.setOnTouchListener(this);
        card6.setOnTouchListener(this);

        card1.setOnDragListener(this);
        card2.setOnDragListener(this);
        card3.setOnDragListener(this);
        card4.setOnDragListener(this);
        card5.setOnDragListener(this);
        card6.setOnDragListener(this);



    }

    private void setAllCards(int w, int h){
        ImageView card1 = (ImageView) findViewById(R.id.card1);
        ImageView card2 = (ImageView) findViewById(R.id.card2);
        ImageView card3 = (ImageView) findViewById(R.id.card3);
        ImageView card4 = (ImageView) findViewById(R.id.card4);
        ImageView card5 = (ImageView) findViewById(R.id.card5);
        ImageView card6 = (ImageView) findViewById(R.id.card6);

        card1.setLayoutParams(getLayoutParams(w,h,card1));
        card2.setLayoutParams(getLayoutParams(w,h,card2));
        card3.setLayoutParams(getLayoutParams(w,h,card3));
        card4.setLayoutParams(getLayoutParams(w,h,card4));
        card5.setLayoutParams(getLayoutParams(w,h,card5));
        card6.setLayoutParams(getLayoutParams(w,h,card6));


    }



    private RelativeLayout.LayoutParams getLayoutParams(int w, int h,View v){
        RelativeLayout.LayoutParams labelLayoutParams = new RelativeLayout.LayoutParams(w, h);
        labelLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        labelLayoutParams.bottomMargin = size;
        labelLayoutParams.leftMargin = size;
        switch (v.getResources().getResourceName(v.getId()).substring(v.getResources().getResourceName(v.getId()).length()-5)){
            case "card2":
                labelLayoutParams.addRule(RelativeLayout.RIGHT_OF,R.id.card1);
                break;
            case "card3":
                labelLayoutParams.addRule(RelativeLayout.RIGHT_OF,R.id.card2);
                break;
            case "card4":
                labelLayoutParams.addRule(RelativeLayout.RIGHT_OF,R.id.card3);
                break;
            case "card5":
                labelLayoutParams.addRule(RelativeLayout.RIGHT_OF,R.id.card4);
                break;
            case "card6":
                labelLayoutParams.addRule(RelativeLayout.RIGHT_OF,R.id.card5);
                break;
            default:
                break;
        }

        return labelLayoutParams;
    }

    private void calcSize(View v){

    }

    @Override
    public void onClick(View v) {
        if (v.getClass() == ImageView.class) {
            if (v.getLayoutParams().height < size * 5) {
                setAllCards(size,size*2);
                v.setLayoutParams(getLayoutParams(size*5,size*10,v));
            } else {
                setAllCards(size,size*2);
                v.setLayoutParams(getLayoutParams(size*2,size*4,v));
            }
        }
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        ClipboardManager clip = (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = clip.getPrimaryClip();
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDrag(data, shadowBuilder, v, 0);

            if (v.getClass() == ImageView.class) {
                if (v.getLayoutParams().height < size * 5) {
                    setAllCards(size,size*2);
                    v.setLayoutParams(getLayoutParams(size*5,size*10,v));
                } else {
                    setAllCards(size,size*2);
                    v.setLayoutParams(getLayoutParams(size*2,size*4,v));
                }
            }
            return true;
        } else {
            return false;
        }
    }



    @Override
    public boolean onDrag(View v, DragEvent event) {
        switch (event.getAction()) {
            case DragEvent.ACTION_DRAG_STARTED:
                draggedView = (View) event.getLocalState();
                dropped = (ImageView) draggedView;
                draggedView.setVisibility(View.INVISIBLE);
                break;
            case DragEvent.ACTION_DRAG_ENTERED:
                break;
            case DragEvent.ACTION_DRAG_EXITED:
                break;
            case DragEvent.ACTION_DROP:
                RelativeLayout.LayoutParams l = (RelativeLayout.LayoutParams) dropped.getLayoutParams();
                l.leftMargin = dropped.getLeft();
                l.bottomMargin = dropped.getBottom();
                v.setLayoutParams(l);
                break;
            case DragEvent.ACTION_DRAG_ENDED:
                break;
            default:
                break;
        }
        return true;
    }
}
