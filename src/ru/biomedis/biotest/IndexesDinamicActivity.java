package ru.biomedis.biotest;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;
import ru.biomedis.biotest.fragments.IndexesDinamic.IndexesDinamicFilterFragment;
import ru.biomedis.biotest.fragments.IndexesDinamic.IndexesDinamicFragment;
import ru.biomedis.biotest.fragments.IndexesDinamic.IndexesDinamicPreferencesDialog;
import ru.biomedis.biotest.fragments.dialogs.SimpleProgressDialog;
import ru.biomedis.biotest.fragments.dialogs.YesNoDialog;
import ru.biomedis.biotest.sql.ModelDataApp;
import ru.biomedis.biotest.sql.entity.MeasureData;
import ru.biomedis.biotest.util.Log;
import ru.biomedis.biotest.util.MidifyViews.BiotestToast;

import java.util.Date;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Map;

/**
 * Created by Anama on 15.12.2014.
 */
public class IndexesDinamicActivity extends BaseActivity
{
    private SharedPreferences preferences;
    private String INDEX_DINAMIC_PREFERENCES="INDEX_DINAMIC_PREFERENCES";
    private String IS_CREATED="IS_CREATED";
    private  IndexesDinamicFilterFragment filterFragment;//фрагмент разметки фильтра
    private IndexesDinamicFragment dinamicFragment;// фрагмент отображения динамики показателей
    private IndexesDinamicFragment.FilteredIndexes filteredIndexes;
    private Date minDate;
    private Date maxDate;
    private int profileId=0;
    private ModelDataApp modelDataApp;

public int getProfileID(){return profileId;}

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        profileId = getIntent().getIntExtra("ProfileId", 0);

        getActionBar().setDisplayShowHomeEnabled(false);//не показывать лого и заголовок
        getActionBar().setDisplayShowTitleEnabled(false);//не показывать в панели заголовок с названием программы.

        setContentView(R.layout.indexes_dinamic_activity);

         preferences = getSharedPreferences("INDEX_DINAMIC_PREFERENCES", MODE_PRIVATE);
        if(!preferences.contains(IS_CREATED))//если такой записи нет то создадим новый файл
        {
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(IS_CREATED,true);
            editor.commit();


        }




        filterFragment = (IndexesDinamicFilterFragment) getFragmentManager().findFragmentByTag("DINAMIC_FILTER");
        if(filterFragment==null) Log.v("!!!!!!!! Не обнаружен фрагмент разметки DINAMIC_FILTER в IndexesDinamicActivity типа IndexesDinamicFilterFragment!!!!!!!");
        else
        {
            filterFragment.setActionListener(dinamicExecListener);// слушатель конопки фрагмента фильтра
        }
        dinamicFragment = (IndexesDinamicFragment) getFragmentManager().findFragmentByTag("DINAMIC_CONTENT");
        if(dinamicFragment==null) Log.v("!!!!!!!! Не обнаружен фрагмент разметки DINAMIC_CONTENT в IndexesDinamicActivity типа IndexesDinamicFragment!!!!!!!");
        else
        {

            filteredIndexes = dinamicFragment.getFilteredIndexes();// получим индекс фильтра
            filteredIndexes.clearPreferencesFormDummy(preferences);
            filteredIndexes.fromPreferencesInit(preferences); //инициализация видимости индексов из настрое, это же действие нужно произвести при получении события от диалога установки настроек

        }

        modelDataApp=((BiotestApp)getApplicationContext()).getModelDataApp();


    }



  private   IndexesDinamicFilterFragment.ActionListener dinamicExecListener=  new IndexesDinamicFilterFragment.ActionListener()
    {
        @Override
        public void onAction(Date min, Date max)
        {
            minDate=min;
            maxDate=max;

            // теперь нужно из ббазы вытащить необходимые данные и направить их в dinamicFragment для отображения согласно фильтру. нужно учесть возможность динамически менять список отображаемых параметров.
            if(profileId!=0)
            {

                if(dinamicFragment.getFilteredIndexes().countIndexesVisibilities()==0)
                {

                    BiotestToast.makeMessageShow(IndexesDinamicActivity.this, getString(R.string.dynamic_param_not_selected_toast_message), Toast.LENGTH_SHORT, BiotestToast.WARNING_MESSAGE);
                    return;
                }

                SimpleProgressDialog spdlg=new SimpleProgressDialog();
                //spdlg.setTitle(getString(R.string.dynamics_calc_dlg_title));

                spdlg.show(getFragmentManager(),"DynamicsDLG");

                List<MeasureData> datas = modelDataApp.findDataByDateFilter(profileId, minDate, maxDate);
                if(datas!=null)
                {
                    if(datas.size()>2)
                    {
                        dinamicFragment.drawData(datas);
                        spdlg.dismiss();
                    } else
                    {
                        spdlg.dismiss();
                        BiotestToast.makeMessageShow(IndexesDinamicActivity.this, getString(R.string.id_less_data), Toast.LENGTH_SHORT, BiotestToast.WARNING_MESSAGE);
                    }
                }else {
                   spdlg.dismiss();
                    BiotestToast.makeMessageShow(IndexesDinamicActivity.this, getString(R.string.id_less_data), Toast.LENGTH_SHORT,BiotestToast.WARNING_MESSAGE);}




            }


        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.indexes_dinamic_activity_actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        Intent intent=null;
        switch(item.getItemId())
        {
            case R.id.filterProperties://нажата кнопка настроек выборки

                // откроем диалог настроек выборки. Также если там нет полей они будут созданны.
                //нужно передать туда FilteredIndexes для его заполнения.а значит фрагмент фильтра должен быть уже создан.

                IndexesDinamicPreferencesDialog dlg=IndexesDinamicPreferencesDialog.newInstance(filteredIndexes);
                dlg.setActionListener(new IndexesDinamicPreferencesDialog.ActionListener()
                {


                    @Override
                    public void onComplete()
                    {
                        // после срабатывания диалога мы имеем заполненный filteredIndexes
                        // теперь нужно сохранить настройки
                        savePreferencesFromIndexesFilter();
                        Log.v("КОМПЛИТ");

                        if(dinamicFragment.isDatasInited()) dinamicFragment.drawData();


                    }
                });
                dlg.show(getFragmentManager(),"prefDlg");

                break;





        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Сохранит настройки из filteredIndexes в preferences
     */
private void savePreferencesFromIndexesFilter()
{
    SharedPreferences.Editor editor = preferences.edit();
    for (Map.Entry<String, Boolean> entry : filteredIndexes.getIndexesVisibilities().entrySet()) {
        editor.putBoolean(entry.getKey(),entry.getValue());
    }
    editor.apply();

}
    /*
    @Override
    public void onBackPressed()
    {
        FragmentManager fragmentManager = getFragmentManager();
        YesNoDialog dlg = YesNoDialog.newInstance(getString(R.string.close),"",getString(R.string.close),getString(R.string.cancel));
        dlg.setActionListener(new YesNoDialog.ActionListener() {
            @Override
            public void cancel() {

            }

            @Override
            public void ok() {
                finish();
                overridePendingTransition(R.anim.open_activity,R.anim.close_activity);
            }
        });
        dlg.show(fragmentManager,"ExitDialog");


    }
*/
    @Override
    protected void onPause()
    {
        super.onPause();
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onStart()
    {
        super.onStart();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
    }
}
