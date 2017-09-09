package ru.biomedis.biotest.fragments.IndexesDinamic;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.fragments.IndexesDinamic.GraphFragments.*;
import ru.biomedis.biotest.sql.entity.MeasureData;
import ru.biomedis.biotest.util.Log;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * По умолчанию на экране выводится описание работы фильтра.
 * Параметры выборки сохраняются в настройках активности и в любой момент можно их поменять или считать и инициализировать FilteredIndexes из сохраненных параметров.
 * Сам фрагмент только отображает.
 * Мы вызвать должны drawData(List<MeasureData> dataList) для отображения и removeData(String name)или addData(String name) для коррекции отображения
 * Created by Anama on 13.12.2014.
 */
public class IndexesDinamicFragment  extends Fragment
{

    private FilteredIndexes filteredIndexes;
    private  List<MeasureData> dataList;
    private ViewGroup content;
    /**
     * Получает объект параметров который задаст нам тип выборкипараметров
     * @return
     */
    public FilteredIndexes getFilteredIndexes()
    {
        return filteredIndexes;
    }

    /**
     * Запускает просчет динамики и отображения на экране с учетом FilteredIndexes которые установили из вне
     */
    public void viewDinamic()
    {

    }

    public boolean isDatasInited()
    {
        if(dataList!=null)return true;
        else return false;
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        dataList=null;

    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        filteredIndexes=new FilteredIndexes();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view =  inflater.inflate(R.layout.indexes_dinamic_fragment,container,false);

        return view;
    }




    /**
     * Перезаполнить список отображения  на основе данных которые были установленны ранее
     */
public void drawData()
{
   if(dataList!=null) this.drawData(dataList);
}
    /**
     * Удалит старую прорисовку и создаст новую.
     * @param dataList
     */
    public void drawData(List<MeasureData> dataList)
    {

//TODO будем обновлять все фрагменты при изменении настроек!!!, позже ожет быть сделать частичную
        this.dataList=dataList;
        this.removeAllData();

        FragmentManager fragmentManager = getFragmentManager();
        BaseGraphFragment frag=null;
        FragmentTransaction transaction;
        for (Map.Entry<String, Boolean> entry : filteredIndexes.getIndexesVisibilities().entrySet()) {
            if(entry.getValue()==true)//если у нас установленно отображение данного фрагмента в настройках
            {

                //
               //добавим фрагмент к контейнеру
                // проверим если такой уже есть то нужно просто переинициализировать его, если нет то создать новый
                try {
                    frag=filteredIndexes.getIndexesFragmentView().get(entry.getKey()).newInstance();
                    frag.setDatas(dataList);

                     transaction=getFragmentManager().beginTransaction();
                   transaction.add(R.id.dinamicViewContainer,frag,entry.getKey());

                    transaction.commit();
                } catch (java.lang.InstantiationException e) {
                    e.printStackTrace();
                    Log.v("Ошибка добавления  фрагмента addFragment()");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    Log.v("Ошибка добавления фрагмента addFragment()");
                }

            }else
            {
                //проверим есть ли такой в списке, если есть удалим его из фрагментов
            }
        }


    }

    /**
     * Удалит из отображения данные по имени из FilteredIndexes
     * @param name
     */
    public void removeData(String name)
    {
        if(this.dataList==null) return;

        FragmentManager fragmentManager = getFragmentManager();

        Fragment fragment = fragmentManager.findFragmentByTag(name);
        if(fragment!=null)
        {

            FragmentTransaction transaction= fragmentManager.beginTransaction();
            transaction.remove(fragment);
            transaction.commit();
        }


    }

    public void removeAllData()
    {

        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment;
        FragmentTransaction transaction;
        for (Map.Entry<String, Boolean> entry : filteredIndexes.getIndexesVisibilities().entrySet()) {
            fragment=  fragmentManager.findFragmentByTag(entry.getKey());
            if(fragment!=null)
            {
                transaction= fragmentManager.beginTransaction();
                transaction.remove(fragment);
                transaction.commit();
            }
        }


    }
    /**
     * Добавит из отображения данные по имени из FilteredIndexes
     * @param name
     */
    public void  addData(String name)
    {
        if(this.dataList==null) return;
        if(!filteredIndexes.getIndexesFragmentView().containsKey(name)) return;

        FragmentManager fragmentManager = getFragmentManager();
        BaseGraphFragment fragment = (BaseGraphFragment)fragmentManager.findFragmentByTag(name);
        FragmentTransaction transaction;

        if(fragment==null)
        {
           //если фрагмента еще не было добавленно, то добавим фрагмент


            try {
                fragment=filteredIndexes.getIndexesFragmentView().get(name).newInstance();
                fragment.setDatas(dataList);

                transaction=getFragmentManager().beginTransaction();
                transaction.add(R.id.dinamicViewContainer,fragment,name);

                transaction.commit();
            } catch (java.lang.InstantiationException e) {
                e.printStackTrace();
                Log.v("Ошибка добавления  фрагмента addFragment()");
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                Log.v("Ошибка добавления фрагмента addFragment()");
            }

        }



    }


    /**
     * Инкапсулирует индексы для отображения в динамике и выборка по датам и из какого профиля брать
     * По умолчанию все параметры отключенны выборки отключены и ничего не будет отображаться
     * Класс заполняется параметрами снаружи
     */
    public class FilteredIndexes implements Serializable
    {


        //параметры смотреть в MeasureData
        private Map<String,Boolean> indexes=new HashMap<String, Boolean>();
        private Map<String,String> indexesDescription=new HashMap<String, String>();
        private Map<String,Class<? extends BaseGraphFragment>> indexesFragmentView=new HashMap<String, Class<? extends BaseGraphFragment>>();


        private Date minDate;//мин дата для выборки из базы
        private Date maxDate;
        private int profileID;// ID профиля из которого будет выборка


        public FilteredIndexes()
        {
            indexes.put("SPEC",false); indexesDescription.put("SPEC", getString(R.string.id_spectr_param)); indexesFragmentView.put("SPEC", SpectrumFragment.class);
            indexes.put("SI",false); indexesDescription.put("SI", getString(R.string.id_is));         indexesFragmentView.put("SI", SIFragment.class);
            indexes.put("BAK",false); indexesDescription.put("BAK", getString(R.string.id_bak));                 indexesFragmentView.put("BAK", BAKFragment.class);
            indexes.put("BE",false); indexesDescription.put("BE", getString(R.string.id_tp));        indexesFragmentView.put("BE", BEFragment.class);
            indexes.put("IN",false); indexesDescription.put("IN", getString(R.string.id_in));   indexesFragmentView.put("IN", INFragment.class);
            indexes.put("HR",false); indexesDescription.put("HR", getString(R.string.id_hr)); indexesFragmentView.put("HR", HRFragment.class);



        }

        /**
         * Список фрагментов реализующий отображение динамики показателей
         * @return
         */
        public Map<String,Class<? extends BaseGraphFragment>> getIndexesFragmentView()
        {
            return indexesFragmentView;
        }

        /**
         * Возвратит описание индекса
         * @param name
         * @return
         */
        public String getIndexDescription(String name){if(indexesDescription.containsKey(name)) return indexesDescription.get(name); else return "";}

        /**
         * Возвратит список индексов для отображения
         * @return
         */
        public final Map<String,Boolean> getIndexesVisibilities(){return indexes;}


        /**
         * Возвратит колличество установленных в TRUE индексов для отображени
         * @return
         */
        public final int countIndexesVisibilities()
        {
            int count=0;
            for (Map.Entry<String, Boolean> entry : getIndexesVisibilities().entrySet()) {
              if(entry.getValue()==true) count++;
            }
            return count;
        }

        /**
         * Обновит индекс
         * @param name
         * @param visible
         * @return true если все нормально или false если накого индекса нет
         */
        public boolean  updateIndexVisibility(String name,boolean visible)
        {
            if(getIndexesVisibilities().containsKey(name)){indexes.put(name, visible);return true;}
           else return false;
        }

        /**
         * Вернет видимость указанного индекса
         * @param name
         * @return
         */
        public boolean getIndexVisibility(String name){return getIndexesVisibilities().get(name);}

        /**
         * Инициализирует список видимости индексов, если параметров нет то останутся такиеже значения по умолчанию те false
         * @param sp
         */
        public void fromPreferencesInit(SharedPreferences sp)
        {

            for (Map.Entry<String, Boolean> entry : getIndexesVisibilities().entrySet())
                    if(sp.contains( entry.getKey())) this.updateIndexVisibility(entry.getKey(),sp.getBoolean(entry.getKey(),false));





        }

        /**
         * Удалит пустые записи в файле, если их нет в классе Filter
         * @param sp
         */
        public void clearPreferencesFormDummy(SharedPreferences sp)
        {
            SharedPreferences.Editor editor = sp.edit();
            for (Map.Entry<String, ?> entry : sp.getAll().entrySet()) {

                if(!getIndexesVisibilities().containsKey(entry.getKey()))editor.remove(entry.getKey());

            }
            editor.apply();

        }

        public int getProfileID()
        {
            return profileID;
        }

        public void setProfileID(int profileID)
        {
            this.profileID = profileID;
        }

        public Date getMaxDate()
        {
            return maxDate;
        }

        public void setMaxDate(Date maxDate)
        {
            this.maxDate = maxDate;
        }

        public Date getMinDate()
        {
            return minDate;
        }

        public void setMinDate(Date minDate)
        {
            this.minDate = minDate;
        }


    }
}
