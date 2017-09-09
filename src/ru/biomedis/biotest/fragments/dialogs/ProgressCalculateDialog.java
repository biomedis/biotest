package ru.biomedis.biotest.fragments.dialogs;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.DialogFragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.util.Log;

import java.util.concurrent.ExecutionException;

/**
 * Фрагмент выводит незакрываемый диалог. Который производит в фоне вычисления. После вычисления
 * Вычисление происходит по команде execute()
 * Created by Anama on 05.11.2014.
 */
public class ProgressCalculateDialog extends DialogStyling
{
    private ImageView myProgressBar;
    private int myProgress = 0;
    private RawDataProcessor rdp;
    private TextView progressInfo;
    private TextView progressPercent;
    private TextView topInfoTextView;
    private boolean defaultConstructor=false;
    private String info="-1";
    private String topInfo="-1";
    private  AnimatorSet animator;

    private  ActionListener actionListener=null;

    private static String EXTRA_DATA="ru.biomedis.progresscalcdlg.rawdata";

    /**
     * Указывает что вызывается версия без RDP это true. иначе false
     * @param defaultConstructor
     */
    private void setIsDefaultConstructor(boolean defaultConstructor)
    {
        this.defaultConstructor = defaultConstructor;
    }

    public static ProgressCalculateDialog newInstance(RawDataProcessor rdp)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATA, rdp);


        ProgressCalculateDialog dlg=new ProgressCalculateDialog();
        dlg.setArguments(args);
        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);

        dlg.setIsDefaultConstructor(false);
        return dlg;
    }

    /**
     * RawDataProcessor должжен быть установлен позже    public void setRdp(RawDataProcessor rdp)
     * @return
     */
    public static ProgressCalculateDialog newInstance()
    {



        ProgressCalculateDialog dlg=new ProgressCalculateDialog();
        dlg.setIsDefaultConstructor(true);
        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);

        return dlg;
    }

    public void setRdp(RawDataProcessor rdp)
    {
        this.rdp = rdp;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       if(defaultConstructor==false) rdp= (RawDataProcessor)getArguments().getSerializable(EXTRA_DATA);

       this.setCancelable(false);

        Log.v("onCreate");
}



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        styling();
        getDialog().setTitle(getString(R.string.pcd_title));


        View view = inflater.inflate(R.layout.progress_calc_dialog, container, true);



        myProgressBar= (ImageView)view.findViewById(R.id.progressBar);
        progressInfo=(TextView)view.findViewById(R.id.progressInfo);
      //  progressPercent=(TextView)view.findViewById(R.id.progressPercent);
        topInfoTextView=(TextView)view.findViewById(R.id.may_out_sensor);
        progressInfo.setText(info);
        animateProgress(myProgressBar);

        return view;
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


    /**
     * Установит текст информации в диалоге
     * @param info
     */
    public void setProgressInfo(String info){this.info=info;if(progressInfo!=null) progressInfo.setText(info);}

    /**
     * Установит информацию наверху диалога
     * @param info
     */
    public void setTopInfo(String info)
    {
        topInfo=info;
        if(topInfoTextView!=null) topInfoTextView.setText(topInfo);
    }

    /**
     * Установит число прогресса
     * @param progress
     */
    public void setProgress(int progress)
    {
        this.myProgress = progress;
      // if(progressPercent!=null) this.progressPercent.setText(progress+"%");

    }

    public void setActionListener(ActionListener actionListener)
    {
        this.actionListener=actionListener;
    }

    public void removeActionListener()
    {
        this.actionListener=null;
    }

    public interface ActionListener
    {
        public void start();
        public void cancel();
        public void completed();
        public void onReady();

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.v("onStart");

        if(!topInfo.equals("-1")) setTopInfo(topInfo);
        if(!info.equals("-1")) setProgressInfo(info);
        if(!animator.isStarted())if(!animator.isRunning())
        {
            animator.start();
        }
        if(actionListener!=null)actionListener.onReady();
    }

    /**
     * Запускает расчет
     */
    public void execute()
    {
        CalculationThread calc=new CalculationThread();
        calc.execute();
    }

    class CalculationThread extends AsyncTask<Void, Void, Void>
     {
         @Override
         protected Void doInBackground(Void... params) {
            try
            {

                rdp.calcR_R();
            }catch (Exception e )
            {
                e.printStackTrace();
                this.cancel(true);

                return null;
            }



             return null;
         }

         @Override
         protected void onPreExecute() {
             super.onPreExecute();
             actionListener.start();
         }

         @Override
         protected void onPostExecute(Void aVoid) {

             super.onPostExecute(aVoid);

             actionListener.completed();
         }

         @Override
         protected void onCancelled(Void aVoid) {
             //super.onCancelled(aVoid);
             actionListener.cancel();


         }
     }

    @Override
    public void onResume()
    {
        super.onResume();
      //  int height=getDialog().getWindow().getDecorView().getHeight();
       // int width=getDialog().getWindow().getDecorView().getWidth();

      //  Window window = getDialog().getWindow();

      //  window.setLayout((int)pxFromDp(500), height);
      //  window.setGravity(Gravity.CENTER);
    }

    public void closeDLG(){ if(animator!=null) animator.cancel(); dismiss();}

}
