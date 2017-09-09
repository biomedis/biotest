package ru.biomedis.biotest;

import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import ru.biomedis.biotest.BaseActivity;
import ru.biomedis.biotest.BiotestApp;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.fragments.dialogs.ProgressCalculateDialog;
import ru.biomedis.biotest.fragments.dialogs.SimpleProgressDialog;
import ru.biomedis.biotest.fragments.measureResults.ResultManager;
import ru.biomedis.biotest.sql.entity.Measure;
import ru.biomedis.biotest.sql.entity.MeasureData;
import ru.biomedis.biotest.util.Log;
import ru.biomedis.biotest.util.MidifyViews.BiotestToast;

import java.util.List;

/**
 * Активность отображает фрагменты результатов измерения как архивную величину.
 * Created by Anama on 09.12.2014.
 */
public class MeasureDataActivity extends BaseActivity
{

    private ResultManager rm;
    private BiotestApp    appContext;
    private RawDataProcessor rdp;
    private  int measureId=-1;
    private Runnable runnable;
    private SimpleProgressDialog dlgSimple;
    private ProgressCalculateDialog pDlg;
    private FragmentManager fm;
    private LinearLayout mResultsContainer;
    private    List<ResultManager.FragmentResultContainer> fragmentResultContainers;
    private TextView note;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayShowHomeEnabled(false);//не показывать лого и заголовок
        getActionBar().setDisplayShowTitleEnabled(false);//не показывать в панели заголовок с названием программы.
        Log.v("onCreate");

         this.measureId = this.getIntent().getIntExtra("MeasureId",-1);

        rm= new ResultManager(this);//создаем менеджер фрагментов результатов измерения

        this.setContentView(R.layout.measured_data_activity);

        note=(TextView)findViewById(R.id.note);

        appContext=(BiotestApp)getApplicationContext();

        mResultsContainer=(LinearLayout)findViewById(R.id.mResultsContainer);
        setupDialog();


    }
private void setupDialog()
{
    Log.v("setupDialog");
     fm = getFragmentManager();
    final ProgressCalculateDialog dlg = ProgressCalculateDialog.newInstance();
    pDlg=dlg;
    dlg.setProgressInfo(getString(R.string.ma_prepare_data_to_view));
    dlg.setTopInfo("");
    dlg.setActionListener(new ProgressCalculateDialog.ActionListener()
    {
        @Override
        public void start()
        {
            //в обработчиках можно сделать изменение надписей в диалоге
            dlg.setProgress(50);
        }

        @Override
        public void cancel()
        {
            BiotestToast.makeMessageShow(getApplicationContext(), getString(R.string.pa_view_measure_canceled), Toast.LENGTH_SHORT, BiotestToast.NORMAL_MESSAGE);

            dlg.closeDLG();
        }

        @Override
        public void completed()
        {
           dlg.setProgress(100);
          //  BiotestToast.makeMessageShow(getApplicationContext(), getString(R.string.pa_cacl_completed), Toast.LENGTH_SHORT,BiotestToast.NORMAL_MESSAGE);
            Log.v("Выыод фрагментов");
            rm.clearContainer(R.id.mResultsContainer);
            fragmentResultContainers = rm.addFragments(R.id.mResultsContainer);
            Measure mData = appContext.getModelDataApp().getMeasure(measureId);
            note.setText(mData.getComment());
            dlg.closeDLG();

            //здесь мы заполним фрагментами наш шаблон.


            /////


        }

        @Override
        public void onReady()
        {
            //здесь нужно запускать вычисления, чтобы RDP был уже инициализирован
            // в нашем случае мы принудительно инициализируем RDP значит можно это не использовать
        }
    });
}

    private void StartCompute(int delay){

        runnable = new Runnable() {
            public void run() {


                if(measureId!=-1)//если мы получили адекватный ID
                {

                    //получим данные
                    MeasureData mData = appContext.getModelDataApp().getMeasureData(measureId);//извлекаем данные об измерении по ID измерения
                    if (mData != null) {


                        pDlg.setProgress(10);
                        List<RawDataProcessor.Point<Integer>> rr = mData.getRr();//получим RR данные.
                        rdp = new RawDataProcessor(rr, mData.getSpectrArray(), mData.getHR());
                        rm.setRawDataProcessor(rdp);
                        pDlg.setRdp(rdp);//передаем данные в диалог

                        //создаем RawDataProcessor на основе наших данных
                        //передаем его в ProgressCalculateDialog
                        // ожидаем результатов и по окончании заполняем данными тут все.


                        pDlg.execute();
                    }

                }




            }
        };
        new Handler().postDelayed(runnable, delay * 300);
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.v("onStart");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("onRestart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("onResume");
        // если фрагменты уже есть то мы ничего не делаем иначе запустим расчет
        if(!rm.isContainedFragments(R.id.mResultsContainer))
        {
            pDlg.show(fm, "ProgressCalculateDlg");//покажем диалог расчета
            StartCompute(1);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("onResume");
    }



    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
