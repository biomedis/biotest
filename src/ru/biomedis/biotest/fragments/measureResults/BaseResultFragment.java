package ru.biomedis.biotest.fragments.measureResults;

import android.app.Fragment;
import ru.biomedis.biotest.RawDataProcessor;

/**
 * Created by Anama on 20.11.2014.
 */
public abstract class BaseResultFragment extends Fragment
{

    private RawDataProcessor rawDataProcessor;
    protected  static  String fragName="";

    public static String getFragName(){return fragName;}

    public RawDataProcessor getRawDataProcessor() {
        return rawDataProcessor;
    }

    public void setRawDataProcessor(RawDataProcessor rawDataProcessor) {
        this.rawDataProcessor = rawDataProcessor;
    }




}
