package ru.biomedis.biotest;

import android.app.*;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import ru.biomedis.biotest.fragments.*;
import ru.biomedis.biotest.fragments.dialogs.*;
import ru.biomedis.biotest.fragments.measureResults.ResultManager;
import ru.biomedis.biotest.sql.ModelDataApp;
import ru.biomedis.biotest.sql.entity.Measure;
import ru.biomedis.biotest.sql.entity.MeasureData;
import ru.biomedis.biotest.sql.entity.Profile;
import ru.biomedis.biotest.util.FileUtil;
import ru.biomedis.biotest.util.Gesture.FrameSwipeLayout;
import ru.biomedis.biotest.util.Gesture.SwipeDetector;
import ru.biomedis.biotest.util.Log;
import ru.biomedis.biotest.fragments.SlideMenu;
import ru.biomedis.biotest.util.MidifyViews.BiotestToast;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

//TODO:

/**
 * Стартовый фрагмент сразу измерение. Выбирается профиль по умолчанию. Если пользоватеь хочет может на жать смену профиля и в диаоговом окне смсенить профиль.
 * Измерения внутри профиля помечаются датами и временем. Пользователь может писать комментарий
 * Измерение доступно сразу при включении программы без лишних телодвижений
 *
 * Created by Anama on 24.09.2014.
 */
public class MainActivity extends BaseActivity implements SlideMenu.OnSlideMenuListner,FrameSwipeLayout.ActionListener
{


    private  SlideMenu slideMenu;
   private TextView title;
   private ActionBar actionBar;
   private Button activeProfileButton;
   private Profile activeProfile;
   private ModelDataApp  modelDataApp=null;
   private RawDataProcessor data=null;

private  MessageFragment mfrg;
    private View actionBarNav;
    private ResultManager resultManager;
    private   SlideMenu.MenuItem menuItemMeasure;
    private FrameSwipeLayout contentlayout;

   private  AppOptions mainOptions;



    private static final int PICKFILE_RESULT_CODE = 1;// код для открытия менеджера файлов
private boolean iiFormOnPause=false;


    /**
     * Фикс исчезновения кнопки overflow button в  actionbar
     */
    private void getOverflowMenu() {

        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


private Activity getThis(){return this;}
    /**
     * Текущие данные. Данные последнего измерения
     * @return
     */
    public RawDataProcessor getData() {
        return data;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        getOverflowMenu();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);// запрет перехода в спящий режим
        // getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.setDebug(false);//откл или вкл дебаг отладки вывод

         mainOptions = ((BiotestApp) getApplicationContext()).getMainOptions();


        resultManager=new ResultManager(this);// управляет фрагментами результатов

        modelDataApp = ((BiotestApp)getApplicationContext()).getModelDataApp();


        activeProfile = modelDataApp.getActiveProfile();

         contentlayout= (FrameSwipeLayout)findViewById(R.id.contentContainer);

        contentlayout.setActionListener(this);

        actionBar=getActionBar();
        actionBar.setDisplayShowHomeEnabled(false);//не показывать лого и заголовок
        actionBar.setDisplayShowTitleEnabled(false);//не показывать в панели заголовок с названием программы.

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.main_actionbar_nav);
        actionBarNav=actionBar.getCustomView();
        activeProfileButton=(Button)actionBarNav.findViewById(R.id.activeProfileButton);//кнопка открытия диалога выбора активного профиля

        actionBar.show();




       Resources res=this.getResources();




        slideMenu = (SlideMenu)getFragmentManager().findFragmentById(R.id.slideMenu);//найдем объект фрагмента меню

         menuItemMeasure = slideMenu.addMenuItem().setTitle(getString(R.string.sm_measure)).setActionId(getString(R.string.sm_measure)).build();



       slideMenu.setActiveItem(menuItemMeasure, false);//выберем активный пункт

        //если нет ни одного профиля то мы не покажем фрагмент измерения пока не создадим профиль
        if(activeProfile==null) mfrg=(MessageFragment)this.replaceContentFragment(MessageFragment.class);
        else
        {
            MeasurePulse mp1 = (MeasurePulse)this.replaceContentFragment(MeasurePulse.class);//покажем фрагмент с измерением
            mp1.setActionListener(this.measureActionListener);
        }









        //создадим меню из списка результатов
        for (ResultManager.FragmentResultContainer itm : resultManager.getResFragments()) {
            slideMenu.addMenuItem().setTitle(itm.getMenuName()).setActionId(itm.getMenuName()).setEnabled(false).build();
        }




        //временный код.
        Log.v("create");

        initEventHandlers();
    }



    public void initEventHandlers()
    {

       this.activeProfileButton.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               FragmentManager fragmentManager = getFragmentManager();

               String tag = v.getTag().toString();

               if(tag.equals("selectDlg"))
               {
                   SelectProfileDialog dlg = SelectProfileDialog.newInstance(getString(R.string.dp_title),getString(R.string.dp_desc));
                   dlg.setActionListener(new SelectProfileDialog.ActionListener() {
                       @Override
                       public void cancel() {

                       }

                       @Override
                       public void selected(Profile profile) {
                           if(profile!=null)
                           {
                               BiotestToast.makeMessageShow(getThis(), getString(R.string.dp_act) + profile.getName(), Toast.LENGTH_SHORT,BiotestToast.NORMAL_MESSAGE);
                               activeProfile=profile;

                               activeProfileButton.setText(getString(R.string.dp_profile) + profile.getName());


                               if(slideMenu.getActiveMenuItem()!=menuItemMeasure) {openMenuItem(menuItemMeasure, android.R.animator.fade_in, android.R.animator.fade_out);slideMenu.setActiveItem(menuItemMeasure,false);}
                               else openMenuItem(menuItemMeasure, android.R.animator.fade_in, android.R.animator.fade_out);


                           }else  BiotestToast.makeMessageShow(getThis(), getString(R.string.dp_profile_not_set), Toast.LENGTH_SHORT,BiotestToast.WARNING_MESSAGE);


                       }
                   });
                   dlg.show(fragmentManager,"ExitDialog");
               }else  if(tag.equals("createDlg"))
               {
                   NewProfileDialog dlg = new NewProfileDialog();

                   dlg.setActionListener(new NewProfileDialog.ActionListener() {
                       @Override
                       public void onProfileCreated(Profile profile) {
                           activeProfile = profile;
                           activeProfileButton.setText(getString(R.string.dp_profile)+profile.getName());
                           activeProfileButton.setTag("selectDlg");
                           if(slideMenu.getActiveMenuItem()!=menuItemMeasure) {openMenuItem(menuItemMeasure, android.R.animator.fade_in, android.R.animator.fade_out);slideMenu.setActiveItem(menuItemMeasure,false);}
                           else openMenuItem(menuItemMeasure, android.R.animator.fade_in, android.R.animator.fade_out);
                       }
                   });
                   dlg.show(fragmentManager,NewProfileDialog.TAG);
               }


           }
       });



    }

public boolean isActiveProfile(){if(activeProfile!=null) return true;else return false;}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
         MenuInflater inflater = getMenuInflater();
         inflater.inflate(R.menu.startup_activity_actionbar_menu, menu);
        MenuItem item = menu.findItem(R.id.actionDebugMeasure);

        if(item!=null)  item.setVisible(Log.isDebug());// видимость пункта мею для загрузки файла.

       // if(item!=null)  item.setVisible(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
      boolean  isNotStarted=true;
        Intent intent=null;
        switch(item.getItemId())
        {
          case android.R.id.home: //нажата кнопка меню - домашняя активность

            break;
            case R.id.actionProfiles:
                intent=new Intent(this,ProfileActivity.class);

            break;
            case R.id.actionGuide:
                intent=new Intent(this,ManualActivity.class);

                break;
            case R.id.actionTexts:
                intent=new Intent(this,TextsActivity.class);

                break;
            case R.id.actionDebugMeasure://здесь откроем окно выбора файла для загрузки в качестве измерения. Обработчик результата в onActivityResult

                if(activeProfile==null)
                {
                    BiotestToast.makeMessageShow(this, getString(R.string.ma_need_selected_active_profile), Toast.LENGTH_SHORT,BiotestToast.WARNING_MESSAGE);

                }else {

    /*
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("file/*");
                    intent.putExtra("return-data", true);
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    PackageManager pkManager = getPackageManager();
                    List<ResolveInfo> activities = pkManager.queryIntentActivities(intent, 0);
                    if (activities.size() > 1) {
                        // Create and start the chooser
                        Intent chooser = Intent.createChooser(intent, "Open with");
                        startActivityForResult(chooser, PICKFILE_RESULT_CODE);

                    } else if(activities.size() ==1){
                        startActivityForResult(intent, PICKFILE_RESULT_CODE);
                    }else BiotestToast.makeMessageShow(this,getString(R.string.filemanager_error),Toast.LENGTH_SHORT, BiotestToast.WARNING_MESSAGE);

                    */
                    GenericSelectDialog dlg=GenericSelectDialog.newInstance(getString(R.string.select_file),getString(R.string.select_file_description));
                    List<String> fileDirApp = FileUtil.getFileNamesFromDir(mainOptions.getString("fileDirApp").getValue());

                    dlg.setActionListener(new GenericSelectDialog.ActionListener()
                    {
                                @Override
                                public void cancel()
                                {

                                }

                                @Override
                                public void selected(String item)
                                {


                                    FragmentManager fragmentManager = getFragmentManager();
                                    YesNoDialog dlg = YesNoDialog.newInstance(getString(R.string.ma_create_profile_title),getString(R.string.ma_path_to_file)+ item,getString(R.string.yes),getString(R.string.cancel));
                                    dlg.setActionListener(new YesNoDialog.ActionListener() {
                                        @Override
                                        public void cancel() {

                                        }

                                        @Override
                                        public void ok() {
                                            byte[] formSD = FileUtil.readFormSD(mainOptions.getString("fileDirApp").getValue()+"/"+item, true);
                                            if(formSD!=null)completeMeasureFromExternal(formSD);
                                            else  BiotestToast.makeMessageShow(getThis(), getString(R.string.ma_error_load_data_from_file), Toast.LENGTH_SHORT, BiotestToast.ERROR_MESSAGE);
                                        }
                                    });


                                    dlg.show(fragmentManager,"MeasureExternalDialog");

                                }
                    });


                    if(fileDirApp!=null)
                    {
                        dlg.setListStrings(fileDirApp);
                        dlg.show(getFragmentManager(), "SELECTDLG");
                    } else {
                        Log.v("Ошибка получения файлов");

                    }





                    isNotStarted = false;//чтоб 2 раза не запустить активность
                }
                break;

            case R.id.options:
                MainPreferencesDialog dlg= MainPreferencesDialog.newInstance();
                dlg.show(getFragmentManager(),"OptDLG");
                break;

        }
        if(intent!=null && isNotStarted) { this.startActivity(intent);return true;}
        return super.onOptionsItemSelected(item);
    }


    /**
     * Обработка нажатий Slide menu
     * @param item
     */
    @Override
    public void onSelectedItem(SlideMenu.MenuItem item) {

       this.openMenuItem(item,android.R.animator.fade_in,android.R.animator.fade_out);

    }

    /**
     * Открывает указанный пункт меню(загружает фрагмент). Учтен стартовый фрагмент измеренияю(метод специализирован под реализацию)
     * @param item
     * @return возвратит фрагмент загруженный
     */
    private Fragment openMenuItem(SlideMenu.MenuItem item)
    {

        if(item.getActionId()==menuItemMeasure.getActionId())
        {
            MeasurePulse mp = (MeasurePulse)this.replaceContentFragment(MeasurePulse.class);
            mp.setActionListener(this.measureActionListener);

            if(!isActiveProfile())BiotestToast.makeMessageShow(this,getString(R.string.dp_profile_not_set),Toast.LENGTH_SHORT,BiotestToast.WARNING_MESSAGE);
            return mp;
        }else
        {

            String name = null;
            for (ResultManager.FragmentResultContainer itm : resultManager.getResFragments()) {
                if (itm.getMenuName().equals(item.getActionId())) {
                    name = itm.getMenuName();
                    break;
                }
            }

            return resultManager.replaceFragment(name, R.id.contentContainer);
        }

    }
    /**
     * Открывает указанный пункт меню(загружает фрагмент). Учтен стартовый фрагмент измеренияю(метод специализирован под реализацию)
     * @param item
     * @param showAnim  id ресурса аниматора для появления нового фрагмента
     * @param hideAnim id ресурса аниматора для для исчезновения предыдущего
     * @return возвратит фрагмент загруженный
     */
    private Fragment openMenuItem(SlideMenu.MenuItem item,int showAnim,int hideAnim)
    {

        if(item.getActionId()==menuItemMeasure.getActionId())
        {
            MeasurePulse mp = (MeasurePulse)this.replaceContentFragment(MeasurePulse.class,R.id.contentContainer,showAnim,hideAnim);
            mp.setActionListener(this.measureActionListener);
            if(!isActiveProfile())BiotestToast.makeMessageShow(this,getString(R.string.dp_profile_not_set),Toast.LENGTH_SHORT,BiotestToast.WARNING_MESSAGE);
            return mp;
        }else
        {

            String name = null;
            for (ResultManager.FragmentResultContainer itm : resultManager.getResFragments()) {
                if (itm.getMenuName().equals(item.getActionId())) {
                    name = itm.getMenuName();
                    break;
                }
            }

            return resultManager.replaceFragment(name, R.id.contentContainer,showAnim,hideAnim);
        }




    }
    /**
     * Загрузит следующий фрагмент(по списку меню)
     * @param showAnim  id ресурса аниматора для появления нового фрагмента
     * @param hideAnim id ресурса аниматора для для исчезновения предыдущего
     * @return возвратит фрагмент загруженный
     */
    private Fragment nextFragment(int showAnim,int hideAnim)
    {
        SlideMenu.MenuItem item = this.slideMenu.getNextMenuItem();
        if(item!=null)
        {
            if(!item.isEnabled()) return null;
            slideMenu.setActiveItem(item,false);
           return  this.openMenuItem(item,showAnim,hideAnim);


        }

        return null;
    }
    /**
     * Загрузит предыдущий фрагмент(по списку меню)
     * @param showAnim  id ресурса аниматора для появления нового фрагмента
     * @param hideAnim id ресурса аниматора для для исчезновения предыдущего
     * @return возвратит фрагмент загруженный или ничего в случае если у нас сейчас крайний элемент
     */
    private Fragment prevFragment(int showAnim,int hideAnim)
    {
        SlideMenu.MenuItem item = this.slideMenu.getPrevMenuItem();
        if(item!=null)
        {
            if(!item.isEnabled()) return null;
            Log.v("Item = "+item.getTitle());
            slideMenu.setActiveItem(item,false);
            return this.openMenuItem(item,showAnim,hideAnim);


        }
        return null;
    }




    public Fragment addContentFragment(Class<? extends Fragment> fragmentClass)
    {

        return null;
    }

    public Fragment replaceContentFragment(Class<? extends Fragment> fragmentClass)
    {
        return this.replaceContentFragment(fragmentClass, R.id.contentContainer);
    }

    public void removeContentFragment()
    {
       this.removeContentFragment(R.id.contentContainer);
    }










    @Override
    protected void onStop() {
        super.onStop();

        Log.v("onStop");



    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v("onStart");
        activeProfile = modelDataApp.getActiveProfile();
        if(activeProfile!=null)
        {
            //профили есть , можно выбрать другой
            activeProfileButton.setText(getString(R.string.dp_profile) + activeProfile.getName());
            activeProfileButton.setTag("selectDlg");



        }else
        {
            int countProfiles = modelDataApp.getCountProfiles();
            if(countProfiles==0)  {
                //профилей нет
                activeProfileButton.setTag("createDlg");
                activeProfileButton.setText(getString(R.string.cp_create_btn));
            }
            else
            {
                //профили есть но не выбран актвный
                activeProfileButton.setTag("selectDlg");
                activeProfileButton.setText(getString(R.string.dp_desc));
            }
        }





    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.v("onRestart");

    }

    @Override
    protected void onResume() {
        super.onResume();


        Log.v("onResume");

        //если у нас профиль выбран то мы проверим установлен ли фрагмент сообщения. Если да то заменим на главный экран вызвав первый пункт меню
        activeProfile=  modelDataApp.getActiveProfile();
        if(activeProfile!=null)
        {
            if(isConteinedFragmentsClass(R.id.contentContainer,MessageFragment.class) )  openMenuItem(menuItemMeasure, android.R.animator.fade_in, android.R.animator.fade_out);
        }else
        {
           if(slideMenu.getActiveMenuItem()!=menuItemMeasure) {openMenuItem(menuItemMeasure, android.R.animator.fade_in, android.R.animator.fade_out);slideMenu.setActiveItem(menuItemMeasure,false);}

            mfrg=(MessageFragment)this.replaceContentFragment(MessageFragment.class);
        }

       if(iiFormOnPause)
       {
       //    SlideMenu.MenuItem item = slideMenu.getActiveMenuItem();
       //if(item==menuItemMeasure)slideMenu.setActiveItem(menuItemMeasure,true);//выберем активный пункт

       }

        iiFormOnPause=false;


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.v("onRestoreInstanceState");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.v("onSaveInstanceState");
       // outState.putString("fragment");



    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("onPause");
        iiFormOnPause=true;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v("onConfigurationChanged");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v("Уничтожен!");


    }



    @Override
    public void onBackPressed()
    {
        FragmentManager fragmentManager = getFragmentManager();
        YesNoDialog dlg = YesNoDialog.newInstance(getString(R.string.ma_close_app_title),getString(R.string.ma_close_app_desc),getString(R.string.close),getString(R.string.cancel));
        dlg.setActionListener(new YesNoDialog.ActionListener()
        {
            @Override
            public void cancel()
            {

            }

            @Override
            public void ok()
            {
                finish();
                overridePendingTransition(R.anim.open_activity, R.anim.close_activity);
            }
        });
        dlg.show(fragmentManager, "ExitDialog");


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case PICKFILE_RESULT_CODE:
            {
                if (resultCode == RESULT_OK)
                {
                    Uri chosenImageUri = data.getData();
                   // final Cursor cursor = getContentResolver().query( chosenImageUri, null, null, null, null );
                   // cursor.moveToFirst();
                   // final String filePath = cursor.getString(0);
                  //  cursor.close();

                    FragmentManager fragmentManager = getFragmentManager();
                    YesNoDialog dlg = YesNoDialog.newInstance(getString(R.string.ma_create_profile_title),getString(R.string.ma_path_to_file)+ data.getData().getPath(),getString(R.string.yes),getString(R.string.cancel));
                    dlg.setActionListener(new YesNoDialog.ActionListener() {
                        @Override
                        public void cancel() {

                        }

                        @Override
                        public void ok() {
                            byte[] formSD = FileUtil.readFormSD(data.getData().getPath(), false);
                            if(formSD!=null)completeMeasureFromExternal(formSD);
                            else  BiotestToast.makeMessageShow(getThis(), getString(R.string.ma_error_load_data_from_file), Toast.LENGTH_SHORT, BiotestToast.ERROR_MESSAGE);
                        }
                    });


                    dlg.show(fragmentManager,"MeasureExternalDialog");

                    ///Log.v("Путь к выбранному файлу "+data.getData().getPath());

                }
                break;
            }
        }

    }

private void setEnabledSlideMenuMeasureItems(boolean val)
{
    final boolean v=val;
    runOnUiThread(new Runnable() {
        @Override
        public void run()
        {

            for (ResultManager.FragmentResultContainer itm : resultManager.getResFragments()) {
                slideMenu.getItemByAction(itm.getMenuName()).setEnabled(v);
            }


        }
    });
}






    MeasurePulse.ActionListener measureActionListener =new MeasurePulse.ActionListener() {
        @Override
        public void onCompletedMeasure(RawDataProcessor rdata)
        {
            data=rdata;
            resultManager.setRawDataProcessor(data);
           if(rdata!=null )
           {
               //BiotestToast.makeMessageShow(getThis(), getString(R.string.ma_measure_success), Toast.LENGTH_SHORT, BiotestToast.NORMAL_MESSAGE);

              FragmentManager fm=getFragmentManager();
               final ProgressCalculateDialog dlg= ProgressCalculateDialog.newInstance(rdata);
               dlg.setProgressInfo(getString(R.string.ma_prepare_data_to_view));
               dlg.setActionListener(new ProgressCalculateDialog.ActionListener() {
                   @Override
                   public void start() {
                       // сделаем некативными пункты меню
                       setEnabledSlideMenuMeasureItems(false);
                   }

                   @Override
                   public void cancel() {
                    data=null;
                       BiotestToast.makeMessageShow(getThis(), getString(R.string.ma_canceled_measure), Toast.LENGTH_SHORT, BiotestToast.ERROR_MESSAGE);
                       setEnabledSlideMenuMeasureItems(false);
                       dlg.closeDLG();
                   }

                   @Override
                   public void completed() {

                       BiotestToast.makeMessageShow(getThis(), getString(R.string.ma_measure_ready), Toast.LENGTH_SHORT, BiotestToast.NORMAL_MESSAGE);

                       setEnabledSlideMenuMeasureItems(true);


                        // здесь у нас получен результат и все вычесленно. Можно сохранить данные в базу

                            //измерение у нас получено от MeasurePulse, через параметр обработчика

                       Measure measure = createMeasure();

                       if(measure!=null)
                       {

                           MeasureData measureData = modelDataApp.createMeasureData(measure, data);//создаст в базе данные о измерении из RawDataProcessor, который заполнили в ProgressCalculateDialog
                           if(measureData!=null)  BiotestToast.makeMessageShow(getThis(), getString(R.string.ma_data_save), Toast.LENGTH_SHORT, BiotestToast.NORMAL_MESSAGE);
                           else BiotestToast.makeMessageShow(getApplicationContext(), getString(R.string.ma_error_save_data), Toast.LENGTH_SHORT,BiotestToast.ERROR_MESSAGE);

                          if(slideMenu.getActiveMenuItem()!=menuItemMeasure) slideMenu.setActiveItem(menuItemMeasure,true);//выберем активный пункт

                       }else
                       {

                           BiotestToast.makeMessageShow(getThis(), getString(R.string.ma_error_save2), Toast.LENGTH_SHORT,BiotestToast.ERROR_MESSAGE);
                           dlg.closeDLG();
                       }

                       //сохранение результатов измерения в базе данных
                       // здесь мы строем интерфейс для отображения результатов
                       dlg.closeDLG();

                       System.gc();

                   }

                   @Override
                   public void onReady()
                   {
                       dlg.execute();
                   }


               });

               dlg.show(fm, "ProgressCalculateDlg");


           }
            else   BiotestToast.makeMessageShow(getThis(), getString(R.string.ma_error_save_3), Toast.LENGTH_SHORT, BiotestToast.ERROR_MESSAGE);

            //вот тут бы вывести диалог с ожиданием, который не закрыть. Паралельно идет расчет. Он сам закроет, когда все разместит в интерфейсе

        }

        @Override
        public void onCanceledMeasure() {
            BiotestToast.makeMessageShow(getThis(), getString(R.string.ma_canceled_measure), Toast.LENGTH_SHORT, BiotestToast.NORMAL_MESSAGE);
            data=null;
            setEnabledSlideMenuMeasureItems(false);
        }

        @Override
        public void onStartMeasure() {
            setEnabledSlideMenuMeasureItems(false);
        }
    };


    /**
     * Создает измерение. Вызываем после того как прошло время измерения и мы сохраняем данные для расчетов. Расчеты вызываются далее.
     */
    private Measure createMeasure(){
        Measure measure=null;
        BiotestApp appContext = (BiotestApp) getApplicationContext();
        ModelDataApp modelDataApp = appContext.getModelDataApp();
        Profile activeProfile = modelDataApp.getActiveProfile();
        if(activeProfile!=null) {

            Calendar c1 = Calendar.getInstance();
            Date date = c1.getTime();
            measure = modelDataApp.createMeasure(activeProfile, "", date);

        }

        return measure;
    }

    /**
     * Инициирование измерение с внешними данными. Минуя работу с датчиком.
     * @param rawData сырые данные.
     */
    public void completeMeasureFromExternal(byte[] rawData)
    {
        RawDataProcessor rdpe=new RawDataProcessor(R.integer.testTime,R.integer.dataReadPeriodUSB_mc,rawData);
        if(measureActionListener!=null)measureActionListener.onCompletedMeasure(rdpe);
    }


    /**
     * Получает свайп по контенту
     * @param type
     */
    @Override
    public void swipe(int type)
    {
       switch (type)
       {
           case FrameSwipeLayout.LEFT_TO_RIGHT:
              this.prevFragment(R.animator.slide_from_left,R.animator.slide_to_right);
               //this.prevFragment(android.R.animator.fade_in,android.R.animator.fade_out);
           break;
           case FrameSwipeLayout.RIGHT_TO_LEFT:
             this.nextFragment(R.animator.slide_from_right,R.animator.slide_to_left);
               //this.nextFragment( android.R.animator.fade_in,android.R.animator.fade_out);
               break;
           case FrameSwipeLayout.TOP_TO_BOTTOM:
               Log.v("TOP_TO_BOTTOM");
               break;
           case FrameSwipeLayout.BOTTOM_TO_TOP:
               Log.v("BOTTOM_TO_TOP");
               break;

       }
    }

}