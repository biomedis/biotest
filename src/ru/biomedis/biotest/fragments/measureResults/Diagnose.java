package ru.biomedis.biotest.fragments.measureResults;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.fragments.dialogs.TextViewDialog;


/**
 * Created by Anama on 16.01.2015.
 */
public class Diagnose extends BaseResultFragment
{
    private TextView tp_index;
    private TextView tp_diagnose;
    private TextView tp_full;
    private TextView vlf_full;
    private TextView vlf_index;
    private TextView vlf_diagnose;
    private TextView lf_index;
    private TextView lf_diagnose;
    private TextView hf_index;
    private TextView hf_diagnose;
    private TextView balance_full;
    private TextView    hf_lf_index;
    private TextView  hf_lf_diagnose;

    private TextView  centr_full;
    private TextView  ic_index;
    private TextView  ic_diagnose;
    private TextView  stress_full;
    private TextView  in_index;
    private TextView  in_diagnose;



    public Diagnose() {
        super();
    }


    /////////  Обработчики событий жизненного цикла фрагмента //////////
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.measure_ressult_diagnose, container, false);

        tp_index= (TextView)view.findViewById(R.id.tp_index);
        tp_diagnose= (TextView)view.findViewById(R.id.tp_diagnose);
        tp_full= (TextView)view.findViewById(R.id.tp_full);
        vlf_full= (TextView)view.findViewById(R.id.vlf_full);
        vlf_index= (TextView)view.findViewById(R.id.vlf_index);
        vlf_diagnose= (TextView)view.findViewById(R.id.vlf_diagnose);
        lf_diagnose= (TextView)view.findViewById(R.id.lf_diagnose);
        lf_index= (TextView)view.findViewById(R.id.lf_index);
        hf_index= (TextView)view.findViewById(R.id.hf_index);
        hf_diagnose= (TextView)view.findViewById(R.id.hf_diagnose);

        balance_full=(TextView)view.findViewById(R.id.balance_full);
        hf_lf_index=(TextView)view.findViewById(R.id.hf_lf_index);
        hf_lf_diagnose=(TextView)view.findViewById(R.id.hf_lf_diagnose);


        centr_full= (TextView)view.findViewById(R.id.centr_full);
        ic_index= (TextView)view.findViewById(R.id.ic_index);
        ic_diagnose= (TextView)view.findViewById(R.id.ic_diagnose);
        stress_full= (TextView)view.findViewById(R.id.stress_full);
        in_index= (TextView)view.findViewById(R.id.in_index);
        in_diagnose= (TextView)view.findViewById(R.id.in_diagnose);

        ///////// TP ///////
        String[] tpArray = getResources().getStringArray(R.array.tp_list);
        String[] lfArray = getResources().getStringArray(R.array.lf_list);
        String[] vlfArray = getResources().getStringArray(R.array.vlf_list);
        String[] hfArray = getResources().getStringArray(R.array.hf_list);
        String[] relative_array = getResources().getStringArray(R.array.relative_array);
        String[] inArray = getResources().getStringArray(R.array.stress_array);
        String[] center_array = getResources().getStringArray(R.array.center_array);




        tp_index.setText(String.format("%.1f", getRawDataProcessor().getSpectrum().getTP()));
        if(getRawDataProcessor().getSpectrum().getTP()<300)
        {
            tp_diagnose.setText(tpArray[0]);
        }else  if(getRawDataProcessor().getSpectrum().getTP()<700 && getRawDataProcessor().getSpectrum().getTP()>=300)
        {
            tp_diagnose.setText(tpArray[1]);
        }
        else   if(getRawDataProcessor().getSpectrum().getTP()<1500 && getRawDataProcessor().getSpectrum().getTP()>=700)
        {
            tp_diagnose.setText(tpArray[2]);
        }
        else   if(getRawDataProcessor().getSpectrum().getTP()<3000 && getRawDataProcessor().getSpectrum().getTP()>=1500)
        {
            tp_diagnose.setText(tpArray[3]);
        }
        else   if(getRawDataProcessor().getSpectrum().getTP()<4000 && getRawDataProcessor().getSpectrum().getTP()>=3000)
        {
            tp_diagnose.setText(tpArray[4]);
        }
        else   if(getRawDataProcessor().getSpectrum().getTP()<6000 && getRawDataProcessor().getSpectrum().getTP()>=4000)
        {
            tp_diagnose.setText(tpArray[5]);
        } else
        {
            tp_diagnose.setText(tpArray[6]);
        }
        tp_full.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //диалог описания
                TextViewDialog dlg=TextViewDialog.newInstance(getString(R.string.tp_dlg_title), getResources().getString(R.string.tp_full_text));
                dlg.show(getFragmentManager(),"TP_FULL_DLG");

            }
        });
//////////////////////////////////////////////


          /////// HF VLF LF /////////
        hf_index.setText( String.format("%.1f", getRawDataProcessor().getSpectrum().getHF()));
        if(getRawDataProcessor().getSpectrum().getHF()<300 )
        {
            hf_diagnose.setText(hfArray[0]);
        }
        else   if(getRawDataProcessor().getSpectrum().getHF()<700 && getRawDataProcessor().getSpectrum().getHF()>=300)
        {
            hf_diagnose.setText(hfArray[1]);
        }
        else   if( getRawDataProcessor().getSpectrum().getHF()>=700)
        {
            hf_diagnose.setText(hfArray[2]);
        }

       lf_index.setText(String.format("%.1f", getRawDataProcessor().getSpectrum().getLF())+"");
        if(getRawDataProcessor().getSpectrum().getLF()<300 )
        {
            lf_diagnose.setText(lfArray[0]);
        }
        else   if(getRawDataProcessor().getSpectrum().getLF()<700 && getRawDataProcessor().getSpectrum().getLF()>=300)
        {
            lf_diagnose.setText(lfArray[1]);
        }
        else   if( getRawDataProcessor().getSpectrum().getLF()>=700)
        {
            lf_diagnose.setText(lfArray[2]);
        }

        vlf_index.setText(String.format("%.1f",getRawDataProcessor().getSpectrum().getVLF()));
        if(getRawDataProcessor().getSpectrum().getVLF()<700 )
        {
           vlf_diagnose.setText(vlfArray[0]);
        }
        else   if(getRawDataProcessor().getSpectrum().getVLF()<1300 && getRawDataProcessor().getSpectrum().getVLF()>=700)
        {
            vlf_diagnose.setText(vlfArray[1]);
        }
        else   if( getRawDataProcessor().getSpectrum().getVLF()>=1300)
        {
            vlf_diagnose.setText(vlfArray[2]);
        }


        vlf_full.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TextViewDialog dlg=TextViewDialog.newInstance(getString(R.string.mr_dlg_title), getResources().getString(R.string.vlf_lf_hf_full_text1)+getResources().getString(R.string.vlf_lf_hf_full_text2));
                dlg.show(getFragmentManager(),"VLF_FULL_DLG");

            }
        });
        ////////////////////////////////

            /////HF_LF /////

        double hf_lf = getRawDataProcessor().getSpectrum().getLF() / getRawDataProcessor().getSpectrum().getHF();
        hf_lf_index.setText(" = "+String.format("%.1f",hf_lf));
        if(hf_lf>=1.0 && hf_lf<=2.0)
        {
            hf_lf_diagnose.setText(relative_array[0]);
        }else  if(hf_lf>2.0 )
        {
            hf_lf_diagnose.setText(relative_array[1]);
        }else  if(hf_lf<1.0 )
        {
            hf_lf_diagnose.setText(relative_array[2]);
        }
        balance_full.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TextViewDialog dlg=TextViewDialog.newInstance(getString(R.string.balance_dlg_title), getResources().getString(R.string.relative_full));
                dlg.show(getFragmentManager(),"BALANCE_FULL_DLG");

            }
        });
        //////////////////

///// Централизация управления ритмом /////
        double ic = getRawDataProcessor().getIS();

        ic_index.setText(""+String.format("%.1f",ic));

        if(ic>1 )
        {
           ic_diagnose.setText(center_array[0]);

        }else  if(ic<1.0 )
        {
            ic_diagnose.setText(center_array[1]);
        }else  if(ic ==1.0 )
        {
            ic_diagnose.setText(center_array[2]);
        }


       centr_full.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TextViewDialog dlg=TextViewDialog.newInstance(getString(R.string.centr_dlg_title), getResources().getString(R.string.center_rithm_full));
                dlg.show(getFragmentManager(),"IC_FULL_DLG");
            }
       });

        /////////////
        // IN //

        double in = getRawDataProcessor().getIN();

        in_index.setText(""+String.format("%.1f",in));

         if(in<30.0 )
        {
            in_diagnose.setText(inArray[0]);
        }else  if(in<60 && in>=30)
        {
            in_diagnose.setText(inArray[1]);
        }else  if(in<120 && in>=60)
        {
            in_diagnose.setText(inArray[2]);
        }else  if(in<200 && in>=120 )
        {
            in_diagnose.setText(inArray[3]);
        }
        else  if(in>=200 )
        {
            in_diagnose.setText(inArray[4]);
        }


        stress_full.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                TextViewDialog dlg=TextViewDialog.newInstance(getString(R.string.stress_dlg_title), getResources().getString(R.string.stress_full));
                dlg.show(getFragmentManager(),"IN_FULL_DLG");
            }
        });

        ///

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //sector.redraw();

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


}
