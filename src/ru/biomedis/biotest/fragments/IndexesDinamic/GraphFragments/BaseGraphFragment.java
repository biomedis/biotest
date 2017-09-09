package ru.biomedis.biotest.fragments.IndexesDinamic.GraphFragments;

import android.app.Fragment;
import ru.biomedis.biotest.sql.entity.MeasureData;

import java.util.List;

/**
 * Необходимо инициализировать до присоединения к узлу  public void setDatas(List<MeasureData> data)
 * Created by Anama on 19.12.2014.
 */
public abstract class BaseGraphFragment extends Fragment
{
    private List<MeasureData> datas;
    public void setDatas(List<MeasureData> data){datas=data;}
    public List<MeasureData> getDatas(){return datas;}

    /**
     * В этом методе данные преобразуются в формат который принимает график.
     */
    abstract void initGraph();

  // public  abstract void redraw(List<MeasureData> datas);
}
