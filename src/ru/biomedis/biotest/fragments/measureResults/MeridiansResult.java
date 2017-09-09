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
public class MeridiansResult extends BaseResultFragment
{

    IntegerSingleBarGraph mBar1;
    IntegerSingleBarGraph mBar2;
    IntegerSingleBarGraph mBar3;
    IntegerSingleBarGraph mBar4;
    IntegerSingleBarGraph mBar5;
    IntegerSingleBarGraph mBar6;
    IntegerSingleBarGraph mBar7;
    IntegerSingleBarGraph mBar8;
    IntegerSingleBarGraph mBar9;
    IntegerSingleBarGraph mBar10;
    IntegerSingleBarGraph mBar11;
    IntegerSingleBarGraph mBar12;




    public MeridiansResult() {
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
        View view = inflater.inflate(R.layout.meridians_result_fragment, container, false);







        mBar1 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar1);
        mBar2 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar2);
        mBar3 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar3);
        mBar4 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar4);
        mBar5 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar5);
        mBar6 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar6);
        mBar7 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar7);
        mBar8 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar8);
        mBar9 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar9);
        mBar10 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar10);
        mBar11 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar11);
        mBar12 = (IntegerSingleBarGraph)view.findViewById(R.id.mBar12);



        Log.v("ID = "+mBar6.getId()+" "+mBar6.getTitleDescription());


        mBar1.setTitleDescription(getResources().getString(R.string.p_m_title));
        mBar1.setDescription(getResources().getString(R.string.p_m_description));

        mBar2.setTitleDescription(getResources().getString(R.string.gi_m_title));
        mBar2.setDescription(getResources().getString(R.string.gi_m_description));


        mBar3.setTitleDescription(getResources().getString(R.string.e_m_title));
        mBar3.setDescription(getResources().getString(R.string.e_m_description));

        mBar4.setTitleDescription(getResources().getString(R.string.rp_m_title));
        mBar4.setDescription(getResources().getString(R.string.rp_m_description));


        mBar5.setTitleDescription(getResources().getString(R.string.c_m_title));
        mBar5.setDescription(getResources().getString(R.string.c_m_description));

        mBar6.setTitleDescription(getResources().getString(R.string.ig_m_title));
        mBar6.setDescription(getResources().getString(R.string.ig_m_description));

        mBar7.setTitleDescription(getResources().getString(R.string.v_m_title));
        mBar7.setDescription(getResources().getString(R.string.v_m_description));

        mBar8.setTitleDescription(getResources().getString(R.string.r_m_title));
        mBar8.setDescription(getResources().getString(R.string.r_m_description));

        mBar9.setTitleDescription(getResources().getString(R.string.mc_m_title));
        mBar9.setDescription(getResources().getString(R.string.mc_m_description));

        mBar10.setTitleDescription(getResources().getString(R.string.tr_m_title));
        mBar10.setDescription(getResources().getString(R.string.tr_m_description));

        mBar11.setTitleDescription(getResources().getString(R.string.vb_m_title));
        mBar11.setDescription(getResources().getString(R.string.vb_m_description));

        mBar12.setTitleDescription(getResources().getString(R.string.f_m_title));
        mBar12.setDescription(getResources().getString(R.string.f_m_description));



        ArrayList<IntegerSingleBarGraph> mArray =new ArrayList<IntegerSingleBarGraph>();
        mArray.add(mBar1);
        mArray.add(mBar2);
        mArray.add(mBar3);
        mArray.add(mBar4);
        mArray.add(mBar5);
        mArray.add(mBar6);
        mArray.add(mBar7);
        mArray.add(mBar8);
        mArray.add(mBar9);
        mArray.add(mBar10);
        mArray.add(mBar11);
        mArray.add(mBar12);

        for(IntegerSingleBarGraph itm:mArray )
        {
            itm.addLevel(5,"",R.color.meridian_bar_level_1);
            itm.addLevel(15,"",R.color.meridian_bar_level_2);
            itm.addLevel(35,"",R.color.meridian_bar_level_3);
            itm.addLevel(50,"",R.color.meridian_bar_level_4);
            itm.addLevel(101,"",R.color.meridian_bar_level_5);


        }


        ArrayList<Integer> meridians = getRawDataProcessor().getSpectrum().getMeridians();

        mBar1.setData(meridians.get(6)); mBar1.setExtraDescription(exstraDescription(meridians.get(6)));
        mBar2.setData(meridians.get(7)); mBar2.setExtraDescription(exstraDescription(meridians.get(7)));
        mBar3.setData(meridians.get(3)); mBar3.setExtraDescription(exstraDescription(meridians.get(3)));
        mBar4.setData(meridians.get(2)); mBar4.setExtraDescription(exstraDescription(meridians.get(2)));
        mBar5.setData(meridians.get(10)); mBar5.setExtraDescription(exstraDescription(meridians.get(10)));
        mBar6.setData(meridians.get(11)); mBar6.setExtraDescription(exstraDescription(meridians.get(11)));
        mBar7.setData(meridians.get(5)); mBar7.setExtraDescription(exstraDescription(meridians.get(5)));
        mBar8.setData(meridians.get(4)); mBar8.setExtraDescription(exstraDescription(meridians.get(4)));
        mBar9.setData(meridians.get(1)); mBar9.setExtraDescription(exstraDescription(meridians.get(1)));
        mBar10.setData(meridians.get(0)); mBar10.setExtraDescription(exstraDescription(meridians.get(0)));
        mBar11.setData(meridians.get(9)); mBar11.setExtraDescription(exstraDescription(meridians.get(9)));
        mBar12.setData(meridians.get(8)); mBar12.setExtraDescription(exstraDescription(meridians.get(8)));




        return view;
    }

    /**
     * Выдаст строку с оценкой состояния, ее можно вставить перед описанием
     * @param val
     * @return
     */
    private String exstraDescription(int val)
    {
        StringBuilder res=new StringBuilder();
        res.append("<p style='text-align:center;'>");
        if(val<= 5) res.append(val+"% &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.meridian_bar_level_1)).substring(2)+"'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;"+getResources().getString(R.string.significant_depletion));
        else if(val> 5 && val< 15)res.append(val+"% &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.meridian_bar_level_2)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.slight));
        else if(val>= 15 && val< 35)res.append(val+"% &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.meridian_bar_level_3)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.rate));
        else if(val>= 35 && val< 50)res.append(val+"% &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.meridian_bar_level_4)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.slight_depletion));
        else if(val>= 50 && val< 101)res.append(val+"% &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.meridian_bar_level_5)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + getResources().getString(R.string.voltage));
            res.append("</p>");
        Log.v(res.toString());
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
