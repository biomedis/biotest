package ru.biomedis.biotest.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.graph.ArrayGraph;
import ru.biomedis.biotest.graph.SectorGraph;
import ru.biomedis.biotest.graph.SectorGraphImpl.SimpleSectorGraph;

import java.util.ArrayList;
import java.util.Random;

/**
 * Фрагмент показывающийся первым после запуска главной активности
 * Created by Anama on 14.10.2014.
 */
public class Startup extends Fragment
{
    SimpleSectorGraph sector;
    ArrayGraph arrayGraph;
    public Startup() {
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
        View view = inflater.inflate(R.layout.startup_fragment, container, false);


         //sector = (SimpleSectorGraph)view.findViewById(R.id.sectorGraph1);
        // sector.addDataItem(170.0,"LF",R.color.lf);
        // sector.addDataItem(110.0,"HF",R.color.hf);
        // sector.addDataItem(150.0,"VLF",R.color.vlf);

        arrayGraph = (ArrayGraph)view.findViewById(R.id.arrayGraph1);
        ArrayList<Double> d=new ArrayList<Double>();

        Random r=new Random() ;


        for(int i=0;i<300;i++)d.add( r.nextDouble()*100);

        arrayGraph.addAllData(d);


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
