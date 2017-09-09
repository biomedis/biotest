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
import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.graph.ArrayGraph;
import ru.biomedis.biotest.graph.SectorGraphImpl.SimpleSectorGraph;

import java.util.ArrayList;
import java.util.Random;

/**
 * Фрагмент показывающийся первым после запуска главной активности
 * Created by Anama on 14.10.2014.
 */
public class IntervalsResult extends BaseResultFragment
{
    SimpleSectorGraph sector;
    ArrayGraph arrayGraph;
    TextView hr;
    public IntervalsResult() {
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


        hr= (TextView)view.findViewById(R.id.hr);
        hr.setText(getRawDataProcessor().getHR()+"");
        arrayGraph = (ArrayGraph)view.findViewById(R.id.arrayGraph1);
        arrayGraph.setLabelX(getString(R.string.g_arraygraph_labelx));
        arrayGraph.setLabelY(getString(R.string.g_arraygraph_label_y));

        ArrayList<Double> d=new ArrayList<Double>();

        arrayGraph.setTitleDescription(getResources().getString(R.string.RR_title));
        arrayGraph.setDescription(getResources().getString(R.string.RR_description));

        for (RawDataProcessor.Point<Integer> l : this.getRawDataProcessor().getRR()) {
            d.add((double)l.getY());
        }


        arrayGraph.setDescription(getResources().getString(R.string.RR_description));

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
