package ru.biomedis.biotest.fragments.IndexesDinamic.GraphFragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.graph.MultiLineImpl.MultiLineDouble;
import ru.biomedis.biotest.sql.entity.MeasureData;
import ru.biomedis.biotest.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Anama on 19.12.2014.
 */
public class SpectrumFragment extends BaseGraphFragment
{
    private TextView title;
    private MultiLineDouble graph;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.dinamic_graph_spectr,container,false);
        title=(TextView)view.findViewById(R.id.titleView);
        graph =(MultiLineDouble)view.findViewById(R.id.multiLine);

        if(getDatas()!=null)initGraph();



        return view;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        graph.redraw();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }


    @Override
    void initGraph()
    {
        Log.v("init---");
        List<Double> lf=new ArrayList<Double>();
        List<Double> vlf=new ArrayList<Double>();
        List<Double> hf=new ArrayList<Double>();

        for (MeasureData data : getDatas())
        {
            lf.add(data.getLF());
            vlf.add(data.getVLF());
            hf.add(data.getHF());

        }


        try {
            graph.setDatas("LF", getResources().getColor(R.color.lf),lf);//#ff06ff02
            graph.setDatas("HF",getResources().getColor(R.color.hf),hf);//#ff0019ff
            graph.setDatas("VLF", getResources().getColor(R.color.vlf),vlf);//#fffffe08


        } catch (Exception e) {
            e.printStackTrace();
        }
        graph.setLabelX(getString(R.string.g_time));
        graph.setLabelY("");
        title.setText(getString(R.string.g_spec));
        Log.v("---init");
    }
}
