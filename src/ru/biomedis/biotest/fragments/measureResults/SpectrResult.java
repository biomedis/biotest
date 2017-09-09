package ru.biomedis.biotest.fragments.measureResults;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.graph.ArrayGraph;
import ru.biomedis.biotest.graph.SectorGraphImpl.SimpleSectorGraph;
import ru.biomedis.biotest.graph.SpectrGraph;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Random;

/**
 * Фрагмент показывающийся первым после запуска главной активности
 * Created by Anama on 14.10.2014.
 */
public class SpectrResult extends BaseResultFragment
{
    private  SimpleSectorGraph sector1;
    private SimpleSectorGraph sector2;
    private SpectrGraph arrayGraph;

    private TextView hf_to_lf;
    private TextView hf;
    private TextView lf;
    private TextView tp;

    private TextView vlfProc;
    private TextView lfProc;
    private TextView hfProc;


    public SpectrResult() {
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
        View view = inflater.inflate(R.layout.spectr_result_fragment, container, false);


         sector1 = (SimpleSectorGraph)view.findViewById(R.id.sectorGraph1);
         sector2 = (SimpleSectorGraph)view.findViewById(R.id.sectorGraph2);

         sector1.addDataItem(getRawDataProcessor().getSpectrum().getLF(),"LF",R.color.lf);
         sector1.addDataItem(getRawDataProcessor().getSpectrum().getHF(),"HF",R.color.hf);

         sector2.addDataItem(getRawDataProcessor().getSpectrum().getLF(),"LF",R.color.lf);
         sector2.addDataItem(getRawDataProcessor().getSpectrum().getHF(),"HF",R.color.hf);
         sector2.addDataItem(getRawDataProcessor().getSpectrum().getVLF(), "VLF", R.color.vlf);

        sector1.setTitleDescription(getResources().getString(R.string.sector_LF_HF_title));
        sector1.setDescription(getResources().getString(R.string.sector_LF_HF_description));

        sector2.setTitleDescription(getResources().getString(R.string.sector_full_title));
        sector2.setDescription(getResources().getString(R.string.sector_full_description));


        arrayGraph = (SpectrGraph)view.findViewById(R.id.spectrGraph);

        arrayGraph.setLabelX(getString(R.string.g_spectrgraph_labelx));
        arrayGraph.setLabelY(getString(R.string.g_spectrgraph_labely));

        arrayGraph.setData(getRawDataProcessor().getSpectrum());

        arrayGraph.setTitleDescription(getResources().getString(R.string.spectr_title));
        arrayGraph.setDescription(getResources().getString(R.string.spectr_description));

       Double lf_hf = getRawDataProcessor().getSpectrum().getLF()/getRawDataProcessor().getSpectrum().getHF();
        sector1.setExtraDescription(extraDescriptionHF_LF(lf_hf));

        hf_to_lf=(TextView)view.findViewById(R.id.lf_to_hf);
        hf_to_lf.setText("LF/HF = "+String.format("%.2f", lf_hf));


        lf=(TextView)view.findViewById(R.id.lfText);
        lf.setText( "LF = "+String.format("%.2f", getRawDataProcessor().getSpectrum().getLF())+" 1/мс2");

        hf=(TextView)view.findViewById(R.id.hfText);
        hf.setText( "HF = "+String.format("%.2f", getRawDataProcessor().getSpectrum().getHF())+" 1/мс2");

        tp=(TextView)view.findViewById(R.id.tpText);
        tp.setText( "TP = "+String.format("%.2f", getRawDataProcessor().getSpectrum().getTP())+" 1/мс2");



        hfProc=(TextView)view.findViewById(R.id.hfProc);
        hfProc.setText( "HF = "+String.format("%.2f", getRawDataProcessor().getSpectrum().calcProc(getRawDataProcessor().getSpectrum().getHF())) + " %");

        lfProc=(TextView)view.findViewById(R.id.lfProc);
        lfProc.setText( "LF = "+String.format("%.2f", getRawDataProcessor().getSpectrum().calcProc(getRawDataProcessor().getSpectrum().getLF()))+" %");

        vlfProc=(TextView)view.findViewById(R.id.vlfProc);
        vlfProc.setText( "VLF = "+String.format("%.2f", getRawDataProcessor().getSpectrum().calcProc(getRawDataProcessor().getSpectrum().getVLF()))+" %");

        return view;
    }

    /**
     * Выдаст строку с оценкой состояния, ее можно вставить перед описанием
     * @param val
     * @return
     */
    private String extraDescriptionHF_LF(double val)
    {
        String[] relative_array = getResources().getStringArray(R.array.relative_array);
        StringBuilder res=new StringBuilder();
        res.append("<p style='text-align:center;'>");
        if(val< 1) res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_4)).substring(2)+"'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;"+relative_array[2]);
        else if(val>2)res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_5)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" +relative_array[1]);
        else if(val>=1.0 && val<=2.0) res.append(" &nbsp;<span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_1)).substring(2) + "'> &nbsp;&nbsp;&nbsp;&nbsp;</span> &nbsp;" + relative_array[0]);
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
        //sector.redraw();
        arrayGraph.redraw();
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
