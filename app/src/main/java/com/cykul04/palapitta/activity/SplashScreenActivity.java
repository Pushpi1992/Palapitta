package com.cykul04.palapitta.activity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import com.cykul04.palapitta.R;
import com.cykul04.palapitta.utils.Prefs;

public class SplashScreenActivity extends Activity {

    private LinearLayout linearLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        // Remember that you should never show the action bar if the
        // status bar is hidden, so hide that too if necessary.

        // load the animation
        //gif_iv = (AnimatedGifImageView) findViewById(R.id.gif_iv);
        Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.animation_fade_in);
       // animFadeIn.setAnimationListener(this);
        linearLayout = (LinearLayout) findViewById(R.id.layout_linear);

        animateContentVisible();
        // start the animation
        linearLayout.setVisibility(View.VISIBLE);
        linearLayout.startAnimation(animFadeIn);


        int SPLASH_TIME_OUT = 5000;
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                /*if (Prefs.getBoolean(Prefs.LOGGEDIN,false)){
                    startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                }else if (!Prefs.getBoolean(Prefs.PAYTM,false) && Prefs.getBoolean(Prefs.REGISTERED,false)){
                    startActivity(new Intent(SplashScreenActivity.this, PaytmAndProfileActivity.class));
                }else {
                    startActivity(new Intent(SplashScreenActivity.this, RegistrationActivity.class));
                }*/
                if (!Prefs.getBoolean(Prefs.REGISTERED,false)){
                    startActivity(new Intent(SplashScreenActivity.this,RegistrationActivity.class));
                    SplashScreenActivity.this.finish();
                }else {
                    startActivity(new Intent(SplashScreenActivity.this,MainActivity.class));
                    SplashScreenActivity.this.finish();
                }
                // This method will be executed once the timer is over
                // Start your app main activity
               /* if (Prefs.getBoolean(Prefs.REGISTERED,false)){
                    if (!Prefs.getBoolean(Prefs.QUIZ,false)) {
                        startActivity(new Intent(SplashScreenActivity.this, DepartmentActivity.class));
                        SplashScreenActivity.this.finish();
                    }else {
                        startActivity(new Intent(SplashScreenActivity.this, QuizActivity.class));
                        SplashScreenActivity.this.finish();
                        finish();
                    }
                }else {
                    if (!Prefs.getBoolean(Prefs.REGISTERED, false)) {
                        startActivity(new Intent(SplashScreenActivity.this, RegistrationActivity.class));
                        SplashScreenActivity.this.finish();
                    } else if (Prefs.getBoolean(Prefs.REGISTERED, false) && !Prefs.getBoolean(Prefs.RSVP_SUBMITTED, false)) {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        SplashScreenActivity.this.finish();
                    } else if (Prefs.getBoolean(Prefs.REGISTERED, false) && Prefs.getBoolean(Prefs.RSVP_SUBMITTED, false)) {
                        startActivity(new Intent(SplashScreenActivity.this, DepartmentActivity.class));
                        SplashScreenActivity.this.finish();
                    } else {
                        startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                        SplashScreenActivity.this.finish();
                    }
                }
*/
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
    private void animateContentVisible() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
//                            // If lollipop use reveal animation. On older phones use fade animation.
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                            // get the center for the animation circle
                            final int cx = (linearLayout.getLeft() + linearLayout.getRight()) / 2;
                            final int cy = (linearLayout.getTop() + linearLayout.getBottom()) / 2;

                            // get the final radius for the animation circle
                            int dx = Math.max(cx, linearLayout.getWidth() - cx);
                            int dy = Math.max(cy, linearLayout.getHeight() - cy);
                            float finalRadius = (float) Math.hypot(dx, dy);

                            try {
                                Animator animator = ViewAnimationUtils.createCircularReveal(linearLayout, cx, cy, 0, finalRadius);
                                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                                animator.setDuration(1250);
                                linearLayout.setVisibility(View.VISIBLE);
                                animator.start();
                            } catch (Exception e) {
                               // startHomeActivity();
                            }

                        } else {
                            linearLayout.setAlpha(0f);
                            linearLayout.setVisibility(View.VISIBLE);
                            linearLayout.animate()
                                    .alpha(1f)
                                    .setDuration(1000)
                                    .setListener(null);
                        }
                    }
                }, 330);
            }
        });


    }
}
