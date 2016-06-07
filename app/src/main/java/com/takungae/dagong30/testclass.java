package com.takungae.dagong30;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

/**
 * Created by m on 6/4/16.
 */
public class testclass extends ScrollView {



    //declare class member variables
    private GestureDetector mGestureDetector;
    private View.OnTouchListener mGestureListener;
    private boolean mIsScrolling = false;

    public testclass(Context context) {
        super(context);
    }



    /*
    public void initGestureDetection() {
        // Gesture detection
        mGestureDetector = new GestureDetector(new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                handleDoubleTap(e);
                return true;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                handleSingleTap(e);
                return true;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                // i'm only scrolling along the X axis
                mIsScrolling = true;
                handleScroll(Math.round((e2.getX() - e1.getX())));
                return true;
            }

            @Override
            *//**
             * Don't know why but we need to intercept this guy and return true so that the other gestures are handled.
             * https://code.google.com/p/android/issues/detail?id=8233
             *//*
            public boolean onDown(MotionEvent e) {
                Log.d("GestureDetector --> onDown");
                return true;
            }
        });

        mGestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                if (mGestureDetector.onTouchEvent(event)) {
                    return true;
                }

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(mIsScrolling ) {
                        Log.d("OnTouchListener --> onTouch ACTION_UP");
                        mIsScrolling  = false;
                        handleScrollFinished();
                    };
                }

                return false;
            }
        };

        // attach the OnTouchListener to the image view
        mImageView.setOnTouchListener(mGestureListener);
    }

    */

}
