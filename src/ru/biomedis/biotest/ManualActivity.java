package ru.biomedis.biotest;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import org.xmlpull.v1.XmlPullParser;
import ru.biomedis.biotest.fragments.ManualContent;
import ru.biomedis.biotest.util.HTMLContentList;

import java.util.EmptyStackException;
import java.util.HashMap;

import java.util.Map;
import java.util.Stack;

/**
 * Активность мануалов по программе
 * Created by Anama on 15.10.2014.
 */
public class ManualActivity extends Activity
{
    private String[] titles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private String title="Руководство";

    private Map<String,String> content=new HashMap<String, String>();
    private ActionBarDrawerToggle mDrawerToggle;
    private HTMLContentList contentList;
    private Stack<Integer> contentPageStack=new Stack<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual);



        //titles = getResources().getStringArray(R.array.manuals_items);
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.mDrawerList = (ListView) findViewById(R.id.left_drawer);

        getActionBar().setIcon(null);
       // getActionBar().setDisplayHomeAsUpEnabled(true);
       // getActionBar().setHomeButtonEnabled(true);

        // объект управляющий drawer
        this.mDrawerToggle = new ActionBarDrawerToggle(this,  this.mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //getActionBar().setTitle(title);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
               // getActionBar().setTitle(mDrawerTitle);//установит название раздела
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };


         this.contentList=new HTMLContentList(this,R.xml.manual_pages);//получим список страниц с мануалами
         this.titles=contentList.getNameList();//получим список имен для меню

        this.mDrawerLayout.setDrawerListener( this.mDrawerToggle);//листнер событий navigationDrawer
        // Set the adapter for the list view
        this.mDrawerList.setAdapter(new ArrayAdapter<String>(this,R.layout.drawer_list_tem, titles));//адаптер списка меню в навигации по траницам
        // Set the list's click listener
        this.mDrawerList.setOnItemClickListener(new DrawerItemClickListener());//обработчик кликов по списку в навигации
        // enable ActionBar app icon to behave as action to toggle nav drawer

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayUseLogoEnabled(false);




        if (savedInstanceState == null) {
            this.selectItem(0);
        }


    }

    /**
     * Обрабатывает список материалов из XML файла
     * @param xmlResID
     */
    private void parseContentListXML(int xmlResID)
    {




    }


    /* Called whenever we call invalidateOptionsMenu() */
    //для кнопок акшен бара
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);

      // for(int i=0;i<menu.size();i++) menu.getItem(i).setVisible(!drawerOpen);//скрыть экшен кнопки или показать

       return super.onPrepareOptionsMenu(menu);
    }


    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            selectItem(position);
        }
    }



    private void setHtmlContent()
    {

    }

    /** Перелючение фрагментов*/
    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        ManualContent fragment;
        FragmentManager fragmentManager = getFragmentManager();
        ManualContent  mf= (ManualContent)fragmentManager.findFragmentById(R.id.content_frame);
        if(mf==null)
        {
            fragment = new ManualContent();
            Bundle b=new Bundle();//установим стартовую страницу
            b.putString(ManualContent.EXSTRA_START_URL, "file:///android_asset/"+contentList.getBaseDir()+"/"+contentList.getStartPage().getDocName());
            fragment.setArguments(b);
            fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();


        }else
        {
            mf.loadPage("file:///android_asset/"+contentList.getBaseDir()+"/"+contentList.getPage(position).getDocName());
        }

        contentPageStack.push(position);







        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(titles[position]);
        mDrawerLayout.closeDrawer(mDrawerList);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.manuals_actionbar, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // Handle action buttons
        switch(item.getItemId()) {
            case R.id.action_exit:

            finish();

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void setTitle(CharSequence title) {

        getActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed()
    {
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        if(drawerOpen==true)mDrawerLayout.closeDrawers();
            else
        {


              try
              {
                   contentPageStack.pop();//извлечем текущую
                   selectItem(contentPageStack.pop());

              }catch (EmptyStackException ex)
              {
                  super.onBackPressed();
                  return;
              }




        }
    }



}