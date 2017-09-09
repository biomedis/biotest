package ru.biomedis.biotest.fragments.measureResults;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.graph.ArrayGraph;
import ru.biomedis.biotest.graph.HistogrammGraph;
import ru.biomedis.biotest.graph.SectorGraphImpl.SimpleSectorGraph;

import java.util.ArrayList;
import java.util.Random;

/**
 * Фрагмент показывающийся первым после запуска главной активности
 * Created by Anama on 14.10.2014.
 */
public class  HistogrammResult extends BaseResultFragment
{
    SimpleSectorGraph sector;
    HistogrammGraph   arrayGraph;
    public HistogrammResult() {
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
        View view = inflater.inflate(R.layout.histogramm_result_fragment, container, false);


     

        arrayGraph = (HistogrammGraph)view.findViewById(R.id.histogrammGraph);
        arrayGraph.setLabelX(getString(R.string.g_histgraph_labelx));
        arrayGraph.setLabelY(getString(R.string.g_histgraph_labely));

        arrayGraph.setData(getRawDataProcessor().getHistogramm());
        arrayGraph.setTitleDescription(getResources().getString(R.string.histogramm_title));
        arrayGraph.setDescription(getResources().getString(R.string.histogramm_description));


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
