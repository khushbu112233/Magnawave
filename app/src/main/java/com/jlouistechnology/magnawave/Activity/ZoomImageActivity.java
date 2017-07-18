package com.jlouistechnology.magnawave.Activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jlouistechnology.magnawave.R;

import pl.polidea.view.ZoomView;

public class ZoomImageActivity extends AppCompatActivity {



    ZoomView zoomView;
    LinearLayout main_container;
    ImageView image;
    FrameLayout frameLayout;
    View v;
    int temp=0;
    int touch_status=0;
    float xCoOrdinate, yCoOrdinate;
    String prev_event="";
    String curr_event="";
    int prev_y=0;
    int curr_y=0;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zoom_image);
        byte[] byteArray = getIntent().getByteArrayExtra("image");
        Bitmap bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        v = ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.zoomableview, null, false);
        v.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

        zoomView = new ZoomView(this);
        zoomView.setMaxZoom(1.5f);
        Log.e("zoom",zoomView.getMaxZoom()+"");
        zoomView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        zoomView.addView(v);

        main_container = (LinearLayout) findViewById(R.id.main_container);
        image=(ImageView)v.findViewById(R.id.image);
        frameLayout=(FrameLayout)v.findViewById(R.id.frame_layout);
        main_container.addView(zoomView);
        image.setImageBitmap(bmp);
        zoomView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {


                int action = MotionEventCompat.getActionMasked(event);
// Get the index of the pointer associated with the action.
                int index = MotionEventCompat.getActionIndex(event);
                int xPos = -1;
                int yPos = -1;

                //   Log.d(DEBUG_TAG,"The action is " + actionToString(action));

                float scale;
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {

                    temp=0;
                    prev_y=(int)event.getY();
                    Log.e("action","ACTION_DOWN"+" "+event.getX()+" "+event.getY());
                    Log.e("actionmy","ACTION_down :----------"+event.getX()+" "+event.getY()+" "+prev_y+" "+temp);
                }
                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    temp=2;


                    if(prev_y==(int)event.getY())
                    {
                        temp=0;
                        Log.e("actionmy","ACTION_UP1 :********"+event.getX()+" "+event.getY()+" "+prev_y+" "+temp);
                    }
                    else if((prev_y-(int)event.getY()<5) && ((prev_y-(int)event.getY())>-5))
                    {
                        temp=0;
                        Log.e("actionmy","ACTION_UP2 :********"+event.getX()+" "+event.getY()+" "+prev_y+" "+temp+":::::::::"+(prev_y-(int)event.getY()));
                    }
                    else if((int)event.getY()-prev_y<5 && (int)event.getY()-prev_y>-5)
                    {
                        temp=0;
                        Log.e("actionmy","ACTION_UP3 :********"+event.getX()+" "+event.getY()+" "+prev_y+" "+temp+":::::::::"+((int)event.getY()-prev_y));
                    }
                    else {
                        temp=1;
                        Log.e("actionmy","ACTION_UP4 :********"+event.getX()+" "+event.getY()+" "+prev_y+" "+temp);
                    }

                    //prev_y=0;
                    if(temp==0)
                    {
                        if (event.getPointerCount() == 1) {


                            if (prev_event.equals("single")) {

                                Toast.makeText(ZoomImageActivity.this, "action up", Toast.LENGTH_LONG).show();
                                touch_status=0;


                                ImageView iv = new ImageView(ZoomImageActivity
                                        .this);
                                iv.setImageResource(R.mipmap.ic_launcher);
                                int id = 1;
                                // iv.setId(id);

                                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(40, 40);
                                float d = getResources().getDisplayMetrics().density;

                                params.leftMargin = (int) (event.getX());
                                params.topMargin = (int) (event.getY());
                                frameLayout.addView(iv, params);

                            }
                        }

                    }
                    // first finger lifted

                }
                if(event.getAction()==MotionEvent.ACTION_POINTER_UP)
                {
                    // second finger lifted
                    Log.e("action","ACTION_POINTER_UP");
                }
                if(event.getAction()==MotionEvent.ACTION_POINTER_DOWN)
                {
                    // first and second finger down
                    Log.e("action","ACTION_POINTER_DOWN");
                }
                if(event.getAction()==MotionEvent.ACTION_MOVE)
                {
                    Log.e("action","ACTION_MOVE-----------"+prev_y);
                   /* if(prev_y==0)
                    {
                        temp=0;
                        prev_y=(int)event.getY();
                    }
                    else {

                        if(prev_y==(int)event.getY())
                        {
                            temp=0;
                            prev_y=(int)event.getY();

                        }
                        else {
                            temp=1;
                            prev_y=(int)event.getY();
                        }
                    }*/





                    Log.e("action","ACTION_MOVE"+event.getX()+" "+event.getY()+" "+prev_y);
                }

                if (event.getPointerCount() > 1) {
                    Log.d("action","Multitouch event"+event.getPointerCount());
                    // The coordinates of the current screen contact, relative to
                    // the responding View or Activity.
                    xPos = (int)MotionEventCompat.getX(event, index);
                    yPos = (int)MotionEventCompat.getY(event, index);
                    touch_status=0;
                    temp=1;
                    prev_event="multi";

                } else {
                    // Single touch event
                    Log.d("action","Single touch event"+event.getPointerCount());
                    xPos = (int)MotionEventCompat.getX(event, index);
                    yPos = (int)MotionEventCompat.getY(event, index);
                    touch_status=touch_status+1;
                    prev_event="single";
                    if(touch_status>2)
                    {
                        touch_status=2;
                    }
                }
                Log.e("touch_status","touch_status"+" "+touch_status);
                return false;
            }
        });



    }
}
