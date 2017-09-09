package ru.biomedis.biotest.calculate;

import android.os.AsyncTask;
import ru.biomedis.biotest.RawDataProcessor;

/**
 * Created by Anama on 06.11.2014.
 */
public class PulseProcessor  extends AsyncTask<RawDataProcessor,Integer,Integer> {





    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Integer i) {
        super.onPostExecute(i);
    }

    @Override
    protected void onProgressUpdate(Integer[] values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled(Integer i) {
        super.onCancelled(i);
    }



    @Override
    protected Integer doInBackground(RawDataProcessor ... rdp) {
        return null;
    }
}
