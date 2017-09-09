package ru.biomedis.biotest.fragments.IndexesDinamic.GraphFragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.graph.SingleLineImpl.SingleLineInteger;
import ru.biomedis.biotest.sql.entity.MeasureData;
import ru.biomedis.biotest.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anama on 19.12.2014.
 */
public class SIFragment extends BaseGraphFragment
{
    private SingleLineInteger graph;
    private List<Integer> graphData=new ArrayList<Integer>();
    private TextView title;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view=inflater.inflate(R.layout.dinamic_graph_si,container,false);
        graph=  (SingleLineInteger)view.findViewById(R.id.singleLine);
        graph.addLevel(1,"",R.color.bar_level_5);
        graph.addLevel(9,"",R.color.bar_level_1);


        graph.setTitleDescription(getString(R.string.g_si));
        StringBuilder strb=new StringBuilder();
        strb.append("<p><span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_5)).substring(2)+"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;"+getString(R.string.in_bar_30)+"</p>");
        strb.append("<p><span style='background-color:#"+Integer.toHexString(getResources().getColor(R.color.bar_level_1)).substring(2)+"'>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</span>&nbsp;"+getString(R.string.ic_bar_1_4)+"</p>");


        graph.setDescription(strb.toString());

        title=(TextView)view.findViewById(R.id.titleView);
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
        for (MeasureData data : getDatas()) graphData.add(data.getSI());

        graph.setData(graphData);
        graph.setLabelX(getString(R.string.g_time));
        graph.setLabelY("");
        title.setText(getString(R.string.g_si));
        Log.v("---init");
    }
}
