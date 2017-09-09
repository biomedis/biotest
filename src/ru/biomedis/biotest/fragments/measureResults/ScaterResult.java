package ru.biomedis.biotest.fragments.measureResults;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.graph.ArrayGraph;
import ru.biomedis.biotest.graph.ScaterGraph;
import ru.biomedis.biotest.graph.SectorGraphImpl.SimpleSectorGraph;

import java.util.ArrayList;

/**
 * Фрагмент показывающийся первым после запуска главной активности
 * Created by Anama on 14.10.2014.
 */
public class ScaterResult extends BaseResultFragment
{
    SimpleSectorGraph sector;
    ScaterGraph arrayGraph;
    TextView hr;
    public ScaterResult() {
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
        View view = inflater.inflate(R.layout.scater_result, container, false);



        arrayGraph = (ScaterGraph)view.findViewById(R.id.scaterGraph);
        arrayGraph.setLabelX(getString(R.string.g_arraygraph_label_y));
        arrayGraph.setLabelY(getString(R.string.g_arraygraph_label_y));


      final   FrameLayout fm=(FrameLayout)view.findViewById(R.id.scater_container);
        arrayGraph.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener()
        {
            @Override
            public void onGlobalLayout()
            {
                int pWidth = fm.getWidth();
                int pHeight = fm.getHeight();

                int pLength;
                if(pWidth>=pHeight){
                    pLength = pHeight;
                }else{
                    pLength = pWidth;
                }

                ViewGroup.LayoutParams pParams = fm.getLayoutParams();
                pParams.width = pLength;
                pParams.height = pLength;
                fm.setLayoutParams(pParams);
                fm.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                //fm.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        ArrayList<Double> d=new ArrayList<Double>();

        arrayGraph.setTitleDescription(getResources().getString(R.string.scatter_title));
        arrayGraph.setDescription(getResources().getString(R.string.scatter_description));

        for (RawDataProcessor.Point<Integer> l : this.getRawDataProcessor().getRR()) {
            d.add((double)l.getY());
        }




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
