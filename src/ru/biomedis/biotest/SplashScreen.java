package ru.biomedis.biotest;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import ru.biomedis.biotest.util.Log;

/*
Стоит сделать плавную анимацию всех элементов. После чего у нас происходит переход к проге.
 Те ПОявляется название. Потом проявляется логотип плавно. После переход
 */


public class SplashScreen extends Activity {
   private Runnable runnable;
private  AnimatorSet animator;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.spash);

         animator = (AnimatorSet)AnimatorInflater.loadAnimator(this, R.animator.heart_animation);
        ImageView heart =(ImageView)findViewById(R.id.heart);


        animator.addListener(new Animator.AnimatorListener()
        {
            private boolean mCanceled;

            @Override
            public void onAnimationStart(Animator animation) {
                mCanceled = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCanceled = true;

            }

            @Override
            public void onAnimationRepeat(Animator animation)
            {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (!mCanceled) {
                    animation.start();
                }
            }
        });
        animator.setTarget(heart);
        animator.setStartDelay(300);




        StartMainScreen(3);

    }

    @Override
    protected void onStart()
    {
        super.onStart();

        FrameLayout fl=(FrameLayout)findViewById(R.id.splashroot);
        LinearLayout logo=(LinearLayout)findViewById(R.id.logiid);

        // LayerDrawable background =(LayerDrawable)fl.getBackground();w
        LayerDrawable background =(LayerDrawable)getResources().getDrawable(R.drawable.splash_background);


        if(!animator.isStarted())if(!animator.isRunning())animator.start();


       // heart.animate().setDuration(1000).setStartDelay(300).scaleX((float)1.4).scaleY((float)1.4).setInterpolator(new AnticipateOvershootInterpolator(5));


        GradientDrawable gradBel =(GradientDrawable)background.getDrawable(2);
        GradientDrawable gradFial =(GradientDrawable)background.getDrawable(3);
        // GradientDrawable gradBel =  (GradientDrawable)background.findDrawableByLayerId(R.id.fon_bel);
        //GradientDrawable gradFial = (GradientDrawable) background.findDrawableByLayerId(R.id.fon_fiolet);

        float density = getResources().getDisplayMetrics().density;
        int widthPixels = getResources().getDisplayMetrics().widthPixels;
        int heightPixels = getResources().getDisplayMetrics().heightPixels;

        gradBel.setGradientRadius((float )(heightPixels/2*1.4));
      if(logo.getMeasuredWidth()!=0)  gradFial.setGradientRadius((float )(logo.getMeasuredWidth()));
    }

    /**
     * Стартует основной экран с заданной задержкой
     * @param delay Задержка в mc
     */
    private void StartMainScreen(int delay){

         runnable = new Runnable() {
            public void run() {

                    Intent i = new Intent(SplashScreen.this, MainActivity.class);
                    startActivity(i);
                    overridePendingTransition(R.anim.open_activity,R.anim.close_activity);
                    animator.cancel();
                    finish();
            }
        };
        new Handler().postDelayed(runnable, delay * 1000);// 3000 - время в милисекундах
    }



    @Override
    public void onBackPressed()
    {

    }

}
