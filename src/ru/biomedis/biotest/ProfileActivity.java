package ru.biomedis.biotest;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import ru.biomedis.biotest.fragments.dialogs.*;
import ru.biomedis.biotest.sql.ModelDataApp;
import ru.biomedis.biotest.sql.entity.Measure;
import ru.biomedis.biotest.sql.entity.Profile;
import ru.biomedis.biotest.util.FileUtil;
import ru.biomedis.biotest.util.Log;
import ru.biomedis.biotest.util.MidifyViews.BiotestToast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
//TODO архивные результаты измерений можно открывать в отдельной активности, формируя ее из набора фрагментов сверху вниз или с прокруткой
/**
 * Профили и архив измерений
 * Created by Anama on 15.10.2014.
 */
public class ProfileActivity extends BaseActivity {


  private  BiotestApp appContext=null;
  private ModelDataApp modelDataApp=null;

  private List<Profile> listProfile;//список профилей
  private ProfileListAdapter pAdapter;//адаптер для списка профилей
  private ListView profileListView;//листвью проофилей

    private List<Measure> listMeasures;//список измерения профиля
    private MeasureListAdapter mAdapter;//адаптор списка измерений
    private ListView measureListView;//вью списка измерений




    private Profile currentSelectedProfile=null;


    private Measure currentSelectedMeasure=null;


    private int selectedMeasurePosition=-1;
    private int  selectedProfilePosition=-1;

    private int bgNoSelectedItem;


    private ImageButton createProfileButton;
    private ImageButton editProfileButton;
    private ImageButton removeProfileButton;
    private ImageButton editMeasureButton;
    private ImageButton removeMeasureButton;
    private ImageButton  viewMeasureButton;
    private Context thisActivity;
    private MenuItem itemsave;



    /**
     * Обработчик нажатий кнопок действий со списками профилей и измерений.
     */
   private  View.OnClickListener onActionButton = new View.OnClickListener() {
        @Override
        public void onClick(View v)
        {
           switch( v.getId())
           {
               case R.id.createProfileButton:
                   createProfileDlg();
               break;
               case R.id.editProfileButton:
                   editProfileDlg();
                   break;
               case R.id.removeProfileButton:
                   removeProfileDlg();
                   break;
               case R.id.editMeasureButton:
                   editMeasureDlg();
                   break;
               case R.id.removeMeasureButton:
                   removeMeasureDlg();
                   break;
               case R.id.viewMeasureButton:
                   if(currentSelectedMeasure!=null) {
                       Intent intent = new Intent(thisActivity, MeasureDataActivity.class);
                       intent.putExtra("MeasureId",currentSelectedMeasure.getId());//передаем ID измерения в активность

                       if (thisActivity != null) thisActivity.startActivity(intent);
                       else Log.v(getString(R.string.error_create_intent_to_call_measure));
                   }
                   break;
           }



        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);



        thisActivity=this;


        getActionBar().setDisplayShowHomeEnabled(false);//не показывать лого и заголовок
        getActionBar().setDisplayShowTitleEnabled(false);//не показывать в панели заголовок с названием программы.



        appContext = (BiotestApp) this.getApplicationContext();
        modelDataApp = appContext.getModelDataApp();


         createProfileButton=(ImageButton)findViewById(R.id.createProfileButton);
         editProfileButton=(ImageButton)findViewById(R.id.editProfileButton);
         removeProfileButton=(ImageButton)findViewById(R.id.removeProfileButton);
         editMeasureButton=(ImageButton)findViewById(R.id.editMeasureButton);
         removeMeasureButton=(ImageButton)findViewById(R.id.removeMeasureButton);
         viewMeasureButton=(ImageButton)findViewById(R.id.viewMeasureButton);



        createProfileButton.setOnClickListener(onActionButton);
        editProfileButton.setOnClickListener(onActionButton);
        removeProfileButton.setOnClickListener(onActionButton);
        editMeasureButton.setOnClickListener(onActionButton);
        removeMeasureButton.setOnClickListener(onActionButton);
        viewMeasureButton.setOnClickListener(onActionButton);





        listProfile=modelDataApp.getListProfiles();
        if(listProfile==null) listProfile=new ArrayList<Profile>();

        pAdapter=new ProfileListAdapter(this);

        profileListView=(ListView)findViewById(R.id.profileListView);



        profileListView.setAdapter(pAdapter);



        measureListView=(ListView)findViewById(R.id.measureListView);
        if(listMeasures==null) listMeasures=new ArrayList<Measure>();
        mAdapter=new MeasureListAdapter(this);

        measureListView.setAdapter(mAdapter);



    editProfileButton.setEnabled(false);
    removeProfileButton.setEnabled(false);
    editMeasureButton.setEnabled(false);
    removeMeasureButton.setEnabled(false);
        viewMeasureButton.setEnabled(false);


/*
ListView, by design, does not pass perform click events on list items when those items contain FOCUSABLE views, regardless of how you configured any of its other flags (ListView actually protects the PerformClick method by first checking hasFocusable() on any list item).
 */

        profileListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                Profile profile =(Profile)parent.getItemAtPosition(position);

                        currentSelectedProfile=profile;
                List<Measure> lMeasures = modelDataApp.getListMeasures(profile);
                listMeasures.clear();
                if(lMeasures!=null) listMeasures.addAll(lMeasures);
                itemsave.setVisible(false);
                if(profile!=null)
                {
                    editProfileButton.setEnabled(true);
                    removeProfileButton.setEnabled(true);


                }else
                {
                    editProfileButton.setEnabled(false);
                    removeProfileButton.setEnabled(false);
                }

                editMeasureButton.setEnabled(false);
                removeMeasureButton.setEnabled(false);
                viewMeasureButton.setEnabled(false);

                selectedProfilePosition=position;

                pAdapter.notifyDataSetChanged();
                mAdapter.notifyDataSetChanged();
               if(selectedMeasurePosition!=-1) measureListView.setSelection(selectedMeasurePosition);


            }
        });



        measureListView.setItemsCanFocus(true);
measureListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Measure measure =   (Measure)parent.getItemAtPosition(position);

        currentSelectedMeasure=measure;


        if(measure!=null)
        {
            editMeasureButton.setEnabled(true);
            removeMeasureButton.setEnabled(true);
            viewMeasureButton.setEnabled(true);
            itemsave.setVisible(true);
        }else
        {
            editMeasureButton.setEnabled(false);
            removeMeasureButton.setEnabled(false);
            viewMeasureButton.setEnabled(false);
            itemsave.setVisible(false);
        }



        selectedMeasurePosition=position;
        mAdapter.notifyDataSetChanged();


    }
});



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
       inflater.inflate(R.menu.profile_actionbar_menu, menu);

         itemsave = menu.findItem(R.id.profileSeveToFile);
         itemsave.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {

        Intent intent=null;
        switch(item.getItemId())
        {
            case android.R.id.home: //нажата кнопка меню - домашняя активность

                break;
            case R.id.profileDinamics://запуск окна динамики показателей
                if(currentSelectedProfile!=null) {

                   if(modelDataApp.countMeasure(currentSelectedProfile.getId())>2)
                   {
                       intent = new Intent(thisActivity, IndexesDinamicActivity.class);
                       intent.putExtra("ProfileId",currentSelectedProfile.getId());//передаем ID измерения в активность

                       if (thisActivity != null) thisActivity.startActivity(intent);
                       else Log.v("Не удается создать интент для запуска IndexesDinamicActivity.class");
                   }else   BiotestToast.makeMessageShow(thisActivity, getString(R.string.ap_data_less), Toast.LENGTH_LONG, BiotestToast.WARNING_MESSAGE);



                }else  BiotestToast.makeMessageShow(thisActivity, getString(R.string.ap_profile_not_selected), Toast.LENGTH_LONG, BiotestToast.WARNING_MESSAGE);


                break;


                case R.id.profileSeveToFile:
                    if(!FileUtil.isExternalStorageWritable()) BiotestToast.makeMessageShow(this,getString(R.string.sdcard_not_avaliable),Toast.LENGTH_SHORT,BiotestToast.ERROR_MESSAGE);
                    if(currentSelectedMeasure!=null)
                    {


                        byte[] rawData = modelDataApp.getRawData(currentSelectedMeasure.getId());
                        if(rawData!=null)
                        {
                         String  nameProfile = currentSelectedProfile.getName();
                            SimpleDateFormat df=new SimpleDateFormat("yyyy_MM_dd_HH_mm");

                           try
                           {
                               FileUtil.writeFileSDBinary(BiotestApp.dirData, nameProfile.replace(" ","_")+"_"+df.format(currentSelectedMeasure.getDt()),rawData);
                               BiotestToast.makeMessageShow(this,"Данные успешно сохранены в директории "+BiotestApp.dirData+" на карте памяти. ",Toast.LENGTH_SHORT,BiotestToast.NORMAL_MESSAGE);

                           }catch (IOException ex)
                           {
                               ex.printStackTrace();
                               BiotestToast.makeMessageShow(this,getString(R.string.error_save_to_sd),Toast.LENGTH_SHORT,BiotestToast.ERROR_MESSAGE);
                           }
                        }
                    }

                break;

        }

        return super.onOptionsItemSelected(item);
    }




    private void editProfileDlg(){
        FragmentManager fragmentManager = getFragmentManager();
         if(currentSelectedProfile==null)return;

        EditProfileDialog dlg=EditProfileDialog.newInstance(currentSelectedProfile);
        dlg.setActionListener(new EditProfileDialog.ActionListener() {
            @Override
            public void onProfileSaved(Profile profile)
            {
                int indexOf = listProfile.indexOf(currentSelectedProfile);
                listProfile.set(indexOf,profile);
                pAdapter.notifyDataSetChanged();

            }

            @Override
            public void errorEdit() {

            }
        });
        dlg.show(fragmentManager,"EditProfileDialog");


    }
    private void removeProfileDlg(){

        FragmentManager fragmentManager = getFragmentManager();
        YesNoDialog dlg = YesNoDialog.newInstance(getString(R.string.ap_delete_profile_question),getString(R.string.ap_delete_profile_description),getString(R.string.delete),getString(R.string.cancel));
        dlg.setActionListener(new YesNoDialog.ActionListener() {
            @Override
            public void cancel() {

            }

            @Override
            public void ok() {
              if(currentSelectedProfile!=null)
              {
                  SimpleProgressDialog spdlg=new SimpleProgressDialog();
                  spdlg.setTitle(getString(R.string.delete));
                  spdlg.show(getFragmentManager(),"DelDlg");

                  modelDataApp.removeProfile(currentSelectedProfile);
                  listProfile.remove(currentSelectedProfile);

                  spdlg.dismiss();
                  currentSelectedProfile=null;
                  selectedProfilePosition=-1;


                  pAdapter.notifyDataSetChanged();

                  listMeasures.clear();
                  mAdapter.notifyDataSetChanged();

              }
            }
        });
        dlg.show(fragmentManager,"RemoveProfileDialog");
    }
    private void editMeasureDlg(){


        FragmentManager fragmentManager = getFragmentManager();
        if(currentSelectedMeasure==null)return;

        EditMeasureDialog dlg=EditMeasureDialog.newInstance(currentSelectedMeasure);
        dlg.setActionListener(new EditMeasureDialog.ActionListener() {
            @Override
            public void onMeasureSaved(Measure measure)
            {

                //TODO тут можно активизировать значек указывающий что есть заметка, можно даже сделать кнопочку заметки

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void errorEdit() {

            }
        });
        dlg.show(fragmentManager,"EditMeasureDialog");


    }
    private void removeMeasureDlg(){

        FragmentManager fragmentManager = getFragmentManager();
        YesNoDialog dlg = YesNoDialog.newInstance(getString(R.string.ap_delete_measure_question),getString(R.string.ap_delete_measure_description),getString(R.string.delete),getString(R.string.cancel));
        dlg.setActionListener(new YesNoDialog.ActionListener() {
            @Override
            public void cancel() {

            }

            @Override
            public void ok() {
               if(currentSelectedMeasure!=null)
               {
                   int i = modelDataApp.removeMeasure(currentSelectedMeasure);
                   if(i!=-1)
                   {

                       BiotestToast.makeMessageShow(getApplicationContext(), getString(R.string.ap_measure_success_delete), Toast.LENGTH_SHORT, BiotestToast.NORMAL_MESSAGE);


                       listMeasures.remove(currentSelectedMeasure);

                       currentSelectedMeasure=null;
                       selectedMeasurePosition=-1;

                       mAdapter.notifyDataSetChanged();




                   }else    BiotestToast.makeMessageShow(getApplicationContext(), getString(R.string.ap_delete_error), Toast.LENGTH_SHORT,BiotestToast.ERROR_MESSAGE);
               }
            }
        });
        dlg.show(fragmentManager,"RemoveMeasureDialog");


    }


    private void createProfileDlg()
    {
        NewProfileDialog dlg = new NewProfileDialog();

        dlg.setActionListener(new NewProfileDialog.ActionListener() {
            @Override
            public void onProfileCreated(Profile profile) {

            listProfile.add(profile);
                pAdapter.notifyDataSetChanged();
               /*
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {   pAdapter.notifyDataSetChanged();}
                });

*/

            }
        });
        dlg.show(getFragmentManager(),NewProfileDialog.TAG);
    }




    private class ProfileListAdapter extends ArrayAdapter<Profile>
    {
        public ProfileListAdapter(Context context) {
            super(context, android.R.layout.simple_list_item_2,listProfile);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Profile p = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(android.R.layout.simple_list_item_2, null);
            }
            ((TextView) convertView.findViewById(android.R.id.text1))
                    .setText(p.getName());
            ((TextView) convertView.findViewById(android.R.id.text2))
                    .setText(p.getComment());

            if(selectedProfilePosition==position)convertView.setBackgroundResource(R.color.slide_button_background_active);
            else  convertView.setBackgroundResource(android.R.color.transparent);

            return convertView;
        }
    }

    private class MeasureListAdapter extends ArrayAdapter<Measure>
    {
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm");
        private ImageView imageNote;
        int colorhint;



        View.OnClickListener onClickNote=new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
             int id =   (int)(((View)(v.getParent())).getTag());

                Measure measure1 = appContext.getModelDataApp().getMeasure(id);
                TextViewDialog dlg;
               if(measure1!=null)
               {
                   dlg = TextViewDialog.newInstance(getString(R.string.ap_measure_note), measure1.getComment());
                   dlg.show(getFragmentManager(),"NoteDlg");
               }


            }
        };








        public MeasureListAdapter(Context context) {
            super(context, R.layout.measures_list_item,listMeasures);


        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {


            Measure p = getItem(position);





            if (convertView == null)convertView = LayoutInflater.from(getContext()).inflate(R.layout.measures_list_item, null);


            ((TextView) convertView.findViewById(R.id.datatime)).setText(df.format(p.getDt()));

            imageNote=(ImageView)convertView.findViewById(R.id.imageNote);




            if(!p.getComment().isEmpty())
            {
                imageNote.setOnClickListener(onClickNote);//если это новый вью найдем в нем нашу картинку и привяжем обработчик клика.
                imageNote.setBackgroundResource(R.drawable.btn_view_note);
            }else
            {

                imageNote.setBackgroundResource(android.R.color.transparent);
                imageNote.setOnClickListener(null);
            }


            convertView.setTag(p.getId());//сохраним таг чтобы мы могли вытащить ID для получения результатов измерения

            if(selectedMeasurePosition==position)convertView.setBackgroundResource(R.color.slide_button_background_active);
            else  convertView.setBackgroundResource(android.R.color.transparent);

            return convertView;
        }
    }


}