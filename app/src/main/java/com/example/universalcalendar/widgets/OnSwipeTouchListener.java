package com.example.universalcalendar.widgets;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class OnSwipeTouchListener implements View.OnTouchListener {
    private GestureDetector gestureDetector;

    public void onClick() {
    }

    public void onDoubleClick() {
    }

    public void onLongClick() {
    }

    public void onSwipeDown() {
    }

    public void onSwipeLeft() {
    }

    public void onSwipeRight() {
    }

    public void onSwipeUp() {
    }

    public OnSwipeTouchListener(Context context) {
        this.gestureDetector = new GestureDetector(context, new GestureListener());
    }

    public boolean onTouch(View view, MotionEvent motionEvent) {
        return this.gestureDetector.onTouchEvent(motionEvent);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_THRESHOLD = 100;
        private static final int SWIPE_VELOCITY_THRESHOLD = 100;

        public boolean onDown(MotionEvent motionEvent) {
            return true;
        }

        private GestureListener() {
        }

        public boolean onSingleTapUp(MotionEvent motionEvent) {
            OnSwipeTouchListener.this.onClick();
            return super.onSingleTapUp(motionEvent);
        }

        public boolean onDoubleTap(MotionEvent motionEvent) {
            OnSwipeTouchListener.this.onDoubleClick();
            return super.onDoubleTap(motionEvent);
        }

        public void onLongPress(MotionEvent motionEvent) {
            OnSwipeTouchListener.this.onLongClick();
            super.onLongPress(motionEvent);
        }

        public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
            try {
                float y = motionEvent2.getY() - motionEvent.getY();
                float x = motionEvent2.getX() - motionEvent.getX();
                if (Math.abs(x) > Math.abs(y)) {
                    if (Math.abs(x) <= 100.0f || Math.abs(f) <= 100.0f) {
                        return false;
                    }
                    if (x > 0.0f) {
                        OnSwipeTouchListener.this.onSwipeRight();
                        return false;
                    }
                    OnSwipeTouchListener.this.onSwipeLeft();
                    return false;
                } else if (Math.abs(y) <= 100.0f || Math.abs(f2) <= 100.0f) {
                    return false;
                } else {
                    if (y > 0.0f) {
                        OnSwipeTouchListener.this.onSwipeDown();
                        return false;
                    }
                    OnSwipeTouchListener.this.onSwipeUp();
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}

