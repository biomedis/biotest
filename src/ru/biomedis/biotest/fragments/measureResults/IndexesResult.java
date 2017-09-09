package ru.biomedis.biotest.fragments.measureResults;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.graph.ArrayGraph;
import ru.biomedis.biotest.graph.SectorGraphImpl.SimpleSectorGraph;
import ru.biomedis.biotest.graph.SingleBarGraphImpl.IntegerSingleBarGraph;
import ru.biomedis.biotest.util.Log;

import java.util.ArrayList;

/**
 * Фрагмент показывающийся первым после запуска главной активности
 * Created by Anama on 14.10.2014.
 */
public class IndexesResult extends BaseResultFragment
{

    IntegerSingleBarGraph indexbar1;
    IntegerSingleBarGraph indexbar2;
    IntegerSingleBarGraph indexbar3;
    IntegerSingleBarGraph indexbar4;
    TextView indexIN;
    TextView indexIS;
    TextView indexBAK;
    TextView indexBE;






    public IndexesResult() {
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
        View view = inflater.inflate(R.layout.indexes_result_fragment, container, false);




        indexbar1 = (IntegerSingleBarGraph)view.findViewById(R.id.indexBar1);
        indexbar2 = (IntegerSingleBarGraph)view.findViewById(R.id.indexBar2);
        indexbar3 = (IntegerSingleBarGraph)view.findViewById(R.id.indexBar3);
        indexbar4 = (IntegerSingleBarGraph)view.findViewById(R.id.indexBar4);



        indexbar1.setTitleDescription(getResources().getString(R.string.in_title));
        indexbar1.setDescription(getResources().getString(R.string.in_description));

        indexbar2.setTitleDescription(getResources().getString(R.string.is_title));
        indexbar2.setDescription(getResources().getString(R.string.is_description));

        indexbar3.setTitleDescription(getResources().getString(R.string.bak_title));
        indexbar3.setDescription(getResources().getString(R.string.bak_description));

        indexbar4.setTitleDescription(getResources().getString(R.string.be_title));
        indexbar4.setDescription(getResources().getString(R.string.be_description));


        indexIN=(TextView)view.findViewById(R.id.indexIN);
        indexIS=(TextView)view.findViewById(R.id.indexIS);
        indexBAK=(TextView)view.findViewById(R.id.indexBAK);
        indexBE=(TextView)view.findViewById(R.id.indexBE);


        int in = getRawDataProcessor().getIN();
        indexIN.setText(in+"");

       // if(in>300)in=300;
        indexbar1.setExtraDescription(extraDescriptionIN(in));
        indexbar1.setData(in);
        indexbar1.addLevel(30,"",R.color.bar_level_2);
        indexbar1.addLevel(60,"",R.color.bar_level_3);
        indexbar1.addLevel(120,"",R.color.bar_level_1);
        indexbar1.addLevel(200,"",R.color.bar_level_4);
        indexbar1.addLevel(3000,"",R.color.bar_level_5);

         int is = getRawDataProcessor().getIS();
         indexIS.setText(is+"");
         if(is>8) is=8;
        indexbar2.setExtraDescription(extraDescriptionIS(is));

        indexbar2.setData(is);
        indexbar2.addLevel(1,"",R.color.bar_level_5);
        indexbar2.addLevel(9,"",R.color.bar_level_1);



        int bak = getRawDataProcessor().getBAK();
        indexBAK.setText(bak+"");
        indexbar3.setExtraDescription(extraDescriptionBAK(bak));

        indexbar3.setData(bak);
        indexbar3.addLevel(33,"",R.color.bar_level_2);
        indexbar3.addLevel(66,"",R.color.bar_level_1);
        indexbar3.addLevel(101,"",R.color.bar_level_5);



        int be = getRawDataProcessor().getBE();
        indexBE.setText(be+"");
        if(be>10000) be=10000;
        indexbar4.setExtraDescription(extraDescriptionBE(be));

        indexbar4.setData(be);
        indexbar4.addLevel(300,"",R.color.bar_level_5);
        indexbar4.addLevel(700,"",R.color.bar_level_4);
        indexbar4.addLevel(1500,"",R.color.bar_level_3);
        indexbar4.addLevel(3000,"",R.color.green_level_2);
        indexbar4.addLevel(4000,"",R.color.green_level_3);
        indexbar4.addLevel(6000,"",R.color.green_level_1);
        indexbar4.addLevel(10001,"",R.color.bar_level_2);











        return view;
    }
    /**
     * Выдаст строку с оценкой состояния, ее можно вставить перед описанием
     * @param val
     * @return
     */
    private String extraDescriptionIN(int val)
    {
        StringBuilder res=new StringBuilder();
        res.append("<p style='text-align:center;'>");
        if(val< 30) res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_2)).substring(2)+"'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;"+getResources().getString(R.string.in_bar_30));
        else if(val>= 30 && val< 60)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_3)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.in_bar_60));
        else if(val>= 60 && val< 120)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_1)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.in_bar_120));
        else if(val>= 120 && val< 200)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_4)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.in_bar_200));
        else if(val>= 200)res.append(val+" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_5)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.in_bar_201));
        res.append("</p>");

        return res.toString() ;
    }

    /**
     * Выдаст строку с оценкой состояния, ее можно вставить перед описанием
     * @param val
     * @return
     */
    private String extraDescriptionIS(int val)
    {
        StringBuilder res=new StringBuilder();
        res.append("<p style='text-align:center;'>");
        if(val< 1) res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_5)).substring(2)+"'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;"+getResources().getString(R.string.in_bar_30));
        else if(val>1)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_1)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.ic_bar_1_4));
        else res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_2)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.ic_bar_balance));
        res.append("</p>");

        return res.toString() ;
    }


    /**
     * Выдаст строку с оценкой состояния, ее можно вставить перед описанием
     * @param val
     * @return
     */
    private String extraDescriptionBAK(int val)
    {
        StringBuilder res=new StringBuilder();
        res.append("<p style='text-align:center;'>");
        if(val< 33) res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_2)).substring(2)+"'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;"+getResources().getString(R.string.ksh_bar_33));
        else if(val>= 33 && val< 66)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_1)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.ksh_bar_66));
        else if(val>= 66 )res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_5)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.ksh_bar_66_100));
        res.append("</p>");

        return res.toString() ;
    }
    /**
     * Выдаст строку с оценкой состояния, ее можно вставить перед описанием
     * @param val
     * @return
     */
    private String extraDescriptionBE(int val)
    {

        String[] stringArray = getResources().getStringArray(R.array.tp_list);

        StringBuilder res=new StringBuilder();
        res.append("<p style='text-align:center;'>");
        if(val< 300) res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_5)).substring(2)+"'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;"+stringArray[0]);
        else if(val>= 300 && val< 700)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_4)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + stringArray[1]);
        else if(val>= 700 && val<1500)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_3)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + stringArray[2]);
        else if(val>= 1500 && val<3000)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.green_level_2)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + stringArray[3]);

        else if(val>= 3000 && val<4000)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.green_level_3)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + stringArray[4]);

        else if(val>= 4000 && val< 6000)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.green_level_1)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + stringArray[5]);
        else if(val>= 6000 && val< 10000)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_2)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + stringArray[6]);
        else if(val>= 10000)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_2)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + stringArray[6]);
        res.append("</p>");

        return res.toString() ;
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
        indexbar1.redraw();
        indexbar2.redraw();
        indexbar3.redraw();
        indexbar4.redraw();
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



    /////////  Кастомный код //////////
















}
