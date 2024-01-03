package com.rmit.bookflowapp.util;

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.rmit.bookflowapp.R;

public class TranslateAnimationUtil implements View.OnTouchListener {
    private GestureDetector mGestureDetector;
    public TranslateAnimationUtil(Context context, View view) {
        mGestureDetector = new GestureDetector(context, new SimpleGestureDetector(view));
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return mGestureDetector.onTouchEvent(event);
    }

    public static void fadeInViewStatic(View view) {
        if (view == null || view.getVisibility() == View.VISIBLE) {
            return;
        }

        AlphaAnimation fadeInAnimation = new AlphaAnimation(0.0f, 1.0f);
        fadeInAnimation.setDuration(200); // Adjust the duration as needed
        fadeInAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(fadeInAnimation);
    }

    public static void fadeOutViewStatic(View view) {
        if (view == null || view.getVisibility() == View.GONE) {
            return;
        }

        AlphaAnimation fadeOutAnimation = new AlphaAnimation(1.0f, 0.0f);
        fadeOutAnimation.setDuration(200); // Adjust the duration as needed
        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        view.startAnimation(fadeOutAnimation);
    }

    public class SimpleGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private View mViewAnimation;
        private boolean isFinishAnimation = true;

        public SimpleGestureDetector(View mViewAnimation) {
            this.mViewAnimation = mViewAnimation;
        }

        @Override
        public boolean onScroll(@Nullable MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
            if (distanceY > 0) {
                hiddenView();
            } else {
                showView();
            }

            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        private void hiddenView() {
            if (mViewAnimation == null || mViewAnimation.getVisibility() == View.GONE) {
                return;
            }

            Animation animationDown = AnimationUtils.loadAnimation(mViewAnimation.getContext(), R.anim.go_up);
            animationDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mViewAnimation.setVisibility(View.VISIBLE);
                    isFinishAnimation = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mViewAnimation.setVisibility(View.GONE);
                    isFinishAnimation = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            if (isFinishAnimation) {
                mViewAnimation.startAnimation(animationDown);
            }
        }

        private void showView() {
            if (mViewAnimation == null || mViewAnimation.getVisibility() == View.VISIBLE) {
                return;
            }

            Animation animationUp = AnimationUtils.loadAnimation(mViewAnimation.getContext(), R.anim.go_down);
            animationUp.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    mViewAnimation.setVisibility(View.VISIBLE);
                    isFinishAnimation = false;
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isFinishAnimation = true;
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            if (isFinishAnimation) {
                mViewAnimation.startAnimation(animationUp);
            }
        }


    }
}
