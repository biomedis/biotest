package ru.biomedis.biotest.fragments.dialogs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.util.Log;


/**
 * Created by Anama on 10.12.2014.
 */
public class SimpleProgressDialog extends DialogStyling
{
    private ImageView myProgressBar;

    private  AnimatorSet animator;

private String title="";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);


        this.setCancelable(false);

    }

    public void setTitle(String title)
    {
        this.title=title;
    }


    private void animateProgress(View view)
    {
        animator = (AnimatorSet) AnimatorInflater.loadAnimator(this.getActivity(), R.animator.heart_animation);



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
        animator.setTarget(view);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        styling();




        View view = inflater.inflate(R.layout.simple_progress_dialog, container, true);
        myProgressBar= (ImageView)view.findViewById(R.id.progressBar);
        animateProgress(myProgressBar);

        return view;
    }


    public void closeDLG(){dismiss();}


    @Override
    public void onStart() {
        super.onStart();

        if(!animator.isStarted())if(!animator.isRunning())animator.start();


        if(this.title.isEmpty())
        {
            getDialog().setTitle(getString(R.string.sp_desc));
        }else  getDialog().setTitle(this.title);
    }




}
