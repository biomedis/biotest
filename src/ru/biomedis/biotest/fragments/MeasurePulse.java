package ru.biomedis.biotest.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import ru.biomedis.biotest.*;
import ru.biomedis.biotest.fragments.dialogs.YesNoDialog;
import ru.biomedis.biotest.graph.LineGraph;
import ru.biomedis.biotest.sql.ModelDataApp;
import ru.biomedis.biotest.sql.entity.Measure;
import ru.biomedis.biotest.sql.entity.Profile;
import ru.biomedis.biotest.util.Log;
import ru.biomedis.biotest.util.MidifyViews.BiotestToast;
import ru.biomedis.biotest.util.USBUtil;

import java.util.*;

/**
 * Created by Anama on 14.10.2014.
 */
public class MeasurePulse extends Fragment implements USBUtil.IUsbEventHandler
{

    Resources res=null;
    public USBUtil usbutil;//usb класс хелпер
    public UsbDevice targetDevice=null;//наш датчик
    private  Button btnStart;
    private  Button btnreq;
    private boolean isTested=false;//включение режима измерения(данные будут собираться в приемник, иначе мы просто будем видеть график)
    private RawDataProcessor rawDataProcessor=null;
    private Timer device_timer;//таймер вызова чтения с устройства
    private  byte[] buffer;//буфер чтения из устройства
    private LineGraph linegraph;

    ///private Activity activity;
    private Timer measureTimer;
    private TextView mTimer;
    private int tmr=0;
    private int testTime =0;

    boolean isRequestedPermission=false;
    private LinearLayout lastTimeLayout;
  private AppOptions mainOptions;

    public MeasurePulse() {
        super();
        Log.v("CTR-------");
    }


    /////////  Обработчики событий жизненного цикла фрагмента //////////
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.v("onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        Log.v("onCreate");
        super.onCreate(savedInstanceState);

        this.res=this.getResources();

        mainOptions = ((BiotestApp) getActivity().getApplicationContext()).getMainOptions();
        usbutil=new USBUtil(this.getActivity());
        usbutil.addEventHandler(this);//слушатель событий usb


        HashMap<String, UsbDevice> usbMap = usbutil.enumerate();

        Iterator< UsbDevice > deviceIterator = usbMap.values().iterator();


        while(deviceIterator.hasNext())
        {
            UsbDevice device = deviceIterator.next();
            if(device.getProductId()==1 & device.getVendorId()==64598) targetDevice=device;

        }
        usbutil.registerUSBReciever();
        if(targetDevice!=null)
        {
            Log.v("Устройство есть из onCreate");
            isRequestedPermission=true;
            usbutil.requestPermission(targetDevice);//если разрешение есть он просто не покажет диалог
            Log.v("Запрос разрешения из onCreate");


        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("onCreateView");
        View view = inflater.inflate(R.layout.mesure_fragment, container, false);

        btnStart=(Button)view.findViewById(R.id.buttonStart);
        btnStart.setEnabled(false);

        lastTimeLayout= (LinearLayout)view.findViewById(R.id.last_time_layout);


        btnreq=(Button)view.findViewById(R.id.buttonRequestUsbPermission);
       if(targetDevice==null) btnreq.setVisibility(View.VISIBLE);

        testTime =res.getInteger(R.integer.testTime);

        linegraph=  (LineGraph) view.findViewById(R.id.lineGraph);
        linegraph.setMaxCountPointForDraw(res.getInteger(R.integer.drawWindow));
        linegraph.setNumDrawingElements(res.getInteger(R.integer.numDrawingElements));

        mTimer=(TextView)view.findViewById(R.id.measureTimer);
        eventRecievers();


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.v("onViewCreated");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v("onActivityCreated");
    }

    @Override
    public void onStart() {
        Log.v("onStart");
        super.onStart();
        btnStart.setEnabled(false);
        usbutil.registerUSBReciever();


    }



    @Override
    public void onResume() {
        super.onResume();
        Log.v("onResume");

        if(!isRequestedPermission )
        {
            if(targetDevice!=null)
            {
                if(!usbutil.isPermission(targetDevice)) {
                    Log.v("Устройство есть из onResume");
                    isRequestedPermission = true;
                    usbutil.requestPermission(targetDevice);//если разрешение есть он просто не покажет диалог
                    Log.v("Запрос разрешения из onResume");
                }else this.onPermission(targetDevice,true);

            }

        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("onSaveInstanceState");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v("onConfigurationChanged");
    }



    @Override
    public void onPause() {
        super.onPause();
        Log.v("onPause");
    }

    @Override
    public void onStop()
    {
        Log.v("onStop");
        super.onStop();
        usbutil.unRegisterUSBReciever();
        if(isTested)stopWriteData();
        if(device_timer!=null) stopDataMining();


    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.v("onDestroyView");
    }
    @Override
    public void onDetach() {
        super.onDetach();
        Log.v("onDetach");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v("onDestroy");

    }




    @Override
    public void onPermission(UsbDevice device, boolean permisson)
    {


        //this.btnStart.setEnabled(permisson);
        if(permisson)btnreq.setVisibility(View.INVISIBLE);
        else btnreq.setVisibility(View.VISIBLE);

    Log.v("Устройство"+ device+" , разрешение ="+permisson);

     if(permisson)
     {
         //это сработает, если у нас уже готов виджет. Те разрешение пришло позже готовности виджета
         startDataMining();//это событие может быть раньше чем отобразится виджет, тогда у нес не будет инициализированные данные в виджете отображения.Используем событие из виджета.
     }

        isRequestedPermission=false;


    }

    @Override
    public void onDetach(UsbDevice device) {
        Log.v("Устройство"+ device+" извлечено");
        targetDevice=null;
        if(isTested)stopWriteData();
        if(device_timer!=null)stopDataMining();
        this.btnStart.setEnabled(false);
        btnreq.setVisibility(View.VISIBLE);
        lastTimeLayout.setVisibility(View.INVISIBLE);




    }


    /////////  Кастомный код //////////


    /**
     * Создает измерение. Вызываем после того как прошло время измерения и мы сохраняем данные для расчетов. Расчеты вызываются далее.
     */
    private Measure createMeasure(){
        Measure measure=null;
        BiotestApp appContext = (BiotestApp) getActivity().getApplicationContext();
        ModelDataApp modelDataApp = appContext.getModelDataApp();
        Profile activeProfile = modelDataApp.getActiveProfile();
       if(activeProfile!=null) {

           Calendar c1 = Calendar.getInstance();
           Date date = c1.getTime();
           measure = modelDataApp.createMeasure(activeProfile, "", date);

       }

    return measure;
    }






  private class TimerTaskCustom extends TimerTask
  {


     private int timerCount =0;
      private CTask cTask;

      public int getTimerCount()
      {
          return timerCount;
      }

      public void setTimerCount(int timerCount)
      {
          this.timerCount = timerCount;
      }

      public TimerTaskCustom(int timerCount)
      {
          this.timerCount = timerCount;
          cTask=new CTask();
      }

      class CTask implements Runnable
      {
          @Override
          public void run()
          {

              if(timerCount==0)
              {
                  completeWriteData();

                  return;
              }
              timerCount--;

              mTimer.setText(timerCount+"");
          }
      }


      @Override
    public void run() {

        getActivity().runOnUiThread(cTask);

    }
}



    /**
     * Включает запись данных  для расчетов
     */
    private void startWriteData()
    {

        System.gc();
        isTested=true;
        btnStart.setText(getString(R.string.fm_stop));
        btnStart.setEnabled(true);
        lastTimeLayout.setVisibility(View.VISIBLE);

        if(rawDataProcessor!=null)rawDataProcessor.clearData();
        if(measureTimer!=null){ measureTimer.cancel();measureTimer=null;}

        TimerTaskCustom ttc=new TimerTaskCustom(testTime);
        this.actionListener.onStartMeasure();
        measureTimer=new Timer();
        measureTimer.schedule(ttc,0L,1000L);


       /*

        tmr=testTime;

        this.actionListener.onStartMeasure();
        measureTimer=new Timer();
        measureTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run()
                    {

                        if(tmr==0)
                        {
                            completeWriteData();

                            return;
                        }
                        tmr--;
                        mTimer.setText(tmr+"");
                    }
                });

            }
        }, 0L, 1000L);

        */
    }

    /**
     * Инициирование измерение с внешними данными. Минуя работу с датчиком.
     * @param rawData сырые данные.
     */
    public void completeMeasureFromExternal(byte[] rawData)
    {
        RawDataProcessor rdpe=new RawDataProcessor(R.integer.testTime,R.integer.dataReadPeriodUSB_mc,rawData);
        if(actionListener!=null)actionListener.onCompletedMeasure(rdpe);
    }


    /**
     * Остановка записи данных с выполнением события onCompletedMeasure
     */
    private void completeWriteData()
    {
        if(measureTimer!=null){ measureTimer.cancel();measureTimer=null;}
        isTested=false;
        btnStart.setText(getString(R.string.start));

        if(actionListener!=null)actionListener.onCompletedMeasure(rawDataProcessor);
        tmr=testTime;
        mTimer.setText("-");

    }
    /**
     * Остановка записи данных с очисткой данных, событие завершения измерения не вызывается, вызываетс отмена измерения
     */
    private void stopWriteData()
    {
        isTested=false;
        btnStart.setText(getString(R.string.start));
        lastTimeLayout.setVisibility(View.INVISIBLE);
        if(measureTimer!=null){ measureTimer.cancel();measureTimer=null;}
        tmr=testTime;
        mTimer.setText("-");
        if(actionListener!=null)actionListener.onCanceledMeasure();

    }


private void stopDataMining()
{
   // isTested=false;
    btnStart.setText(getString(R.string.start));
    device_timer.cancel();
    device_timer=null;


}




    private int max=0;
    private int min=256;
    private int drawWindowCount=0;
    private int counter=0;
    private float lowLevelSignalMax=(float)0.35;
    private float lowLevelSignaMin=(float)0.2;
    private float lowLevelSignal=(float)0.35;//порог нижний

    private class AddDrawPoint implements Runnable
    {
        short val=0;

        public void setVal(short val)
        {
            this.val = val;
        }

        @Override
        public void run()
        {
            linegraph.addDrawPoint(val);
            linegraph.setHiPorogFloat(mainOptions.getFloat("lowPorogPulse").getValue());
        }
    }
private AddDrawPoint addDrawPointRun=new AddDrawPoint();

//массив буферный чтобы исключить перезапись значений при отставании таймера
private AddDrawPoint buffArray[]={new AddDrawPoint(),new AddDrawPoint(),new AddDrawPoint(),new AddDrawPoint(),new AddDrawPoint(),new AddDrawPoint(),new AddDrawPoint(),new AddDrawPoint()};
private int bufferPointer=0;



    /**
     * Начать читать данные с датчика и показывать их
     */
    private void startDataMining()
    {
             isTested=false;//чтобы наверняка
            if(targetDevice==null){ Toast.makeText(this.getActivity(), getString(R.string.usb_not_installed), Toast.LENGTH_LONG).show();return;}

        drawWindowCount= getResources().getInteger(R.integer.drawWindow);//сколько отсчетов берется для прорисовки на графики по всей оси х. 512 в программе

        final MainActivity main= (MainActivity)this.getActivity();

             rawDataProcessor=new RawDataProcessor(R.integer.testTime,R.integer.dataReadPeriodUSB_mc);//при новом измерении у нас новый набор данных



            buffer =  usbutil.getHelper(targetDevice).getInbuffer();

            //нужно не забывать таймер останавливать
            device_timer =    new Timer();
            device_timer.schedule(new TimerTask() {
                @Override
                public void run() {

                    int status=-1;



                    if(!linegraph.isReady())return;//проверка проинициализировал ли себя виджет отображения

                    if(targetDevice==null) {this.cancel();return;}
                    if(usbutil==null) {this.cancel();return;}


                    try {
                        status = usbutil.getHelper(targetDevice).bulkTransfer(USBUtil.EndpointDir.ENDPOINT_IN,2,300);
                    }
                    catch (Exception ex)
                    {
                      ex.printStackTrace();
                        this.cancel();return;
                    }



                    if(status>0)
                    {
                        short temp =buffer[0]<0?(short)(buffer[0]+256):buffer[0];


                        if(isTested) { counter=0;min=256;max=0;}// если измеряем. то обнуляем эти переменные, тк мы могли нажать старт тогда когда они чем-то заполненны.
                        if(!isTested)if(counter<drawWindowCount/2)//считаем макс и мин в течении окна прорисовки(половины), отрабатывает только вне измерения.
                        {

                                counter++;

                                if(temp>max) { max=temp; }
                                if(temp<min)min=temp;

                        }else
                        {



                          final float  coeffPower= (float)(max-min)/255;// находим макс сигнала и минумум за 256 отсчетов. Если были зашкаливания или не дотянули до уровня то коэффициент это отразит (255 это максимльное число сдатчика)



                            //изменим статус кнопки старта в зависимости от уровня сигнала
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run()
                                {
                                 //если коэф >0.75 слишком велик. Если меньше 0.35  то слабый, между - нормальный
                                    if(coeffPower> 0.75)
                                    {
                                        btnStart.setText(getString(R.string.fm_biggest_signal));
                                        btnStart.setEnabled(false);

                                    }else if(coeffPower >mainOptions.getFloat("lowPorogPulse").getValue())
                                    {
                                        btnStart.setText(getString(R.string.start));
                                        if(main.isActiveProfile()) btnStart.setEnabled(true);
                                        else  btnStart.setEnabled(false);

                                    }else
                                    {
                                        btnStart.setText(getString(R.string.fm_low_signal));
                                        btnStart.setEnabled(false);
                                    };
                                }
                            });
                            counter=0;min=256;max=0;

                        }

                        //с датчика идут целые числа от 0 до 255. Если мы получили <0 то мы на самом деле имеем число >127 и мы просто прибавим 256
                        //Log.v("ЕСТЬ ДАННЫЕ С ЮСБ USB");


                       if(isTested) rawDataProcessor.addRawData(temp);

                   if(linegraph!=null && getActivity()!=null)
                   {

                       //организуем циклическую перестановку объектов , чтобы точно не терять данные. Это было когда использовался 1 объект, тк мы не успевали применить данные, а уже появлялись новые и старые перетерались
                       if(bufferPointer>=buffArray.length) bufferPointer=0;
                       int tPointer=bufferPointer;
                       bufferPointer++;
                       buffArray[tPointer].setVal(temp);
                       getActivity().runOnUiThread(buffArray[tPointer]);
                   }


                    }

                }
            }, 0L, 4);



    }






    private void eventRecievers()
    {




        //кнопка старта
        this.btnStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {

                if(isTested==true)
                {

                    FragmentManager fragmentManager = getFragmentManager();
                    YesNoDialog dlg = YesNoDialog.newInstance(getString(R.string.ma_cancel_measure_question),getString(R.string.ma_close_app_desc),getString(R.string.yes),getString(R.string.no));
                    dlg.setActionListener(new YesNoDialog.ActionListener() {
                        @Override
                        public void cancel() {

                        }

                        @Override
                        public void ok() {
                            stopWriteData();
                        }
                    });
                    dlg.show(fragmentManager,"ExitDialog");




                }else
                {
                    FragmentManager fragmentManager = getFragmentManager();
                    YesNoDialog dlg = YesNoDialog.newInstance(getString(R.string.ma_start_measure), getString(R.string.ma_start_measure_description), getString(R.string.yes), getString(R.string.no));
                    dlg.setActionListener(new YesNoDialog.ActionListener() {
                        @Override
                        public void cancel() {

                        }

                        @Override
                        public void ok() {
                            startWriteData();
                        }
                    });
                    dlg.show(fragmentManager,"ExitDialog");




                }




            }
        });




        btnreq.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                targetDevice = null;
                HashMap<String, UsbDevice> usbMap = usbutil.enumerate();
                Iterator<UsbDevice> deviceIterator = usbMap.values().iterator();
                while (deviceIterator.hasNext()) {
                    UsbDevice device = deviceIterator.next();
                    if (device.getProductId() == 1 & device.getVendorId() == 64598) targetDevice = device;

                }

                if (targetDevice != null)
                {
                   if(!usbutil.isPermission(targetDevice)) {isRequestedPermission=true; usbutil.requestPermission(targetDevice);}


                  } else {
                    BiotestToast.makeMessageShow(getActivity(), getString(R.string.ma_not_nistalled_sensor_q), Toast.LENGTH_SHORT, BiotestToast.WARNING_MESSAGE);
                    //Toast.makeText(getActivity(),getString(R.string.ma_not_nistalled_sensor_q),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private ActionListener actionListener;
    public void setActionListener(ActionListener a){actionListener=a;}

    public interface ActionListener
    {
        public void onCompletedMeasure(RawDataProcessor data);
        public void onCanceledMeasure();
        public void onStartMeasure();
    }












}
