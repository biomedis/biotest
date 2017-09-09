package ru.biomedis.biotest;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import ru.biomedis.biotest.util.Log;

/**
 * Created by Anama on 30.10.2014.
 */
public class BaseActivity extends Activity {


    /**
     * Заменит в контейнере фраuменты на этот c анимацией
     * @param fragmentClass
     * @param containerResourceId  id контейнера
     * @param showAnim  id ресурса аниматора для появления нового фрагмента
     * @param hideAnim id ресурса аниматора для для исчезновения предыдущего
     * @return
     */
    public Fragment replaceContentFragment(Class<? extends Fragment> fragmentClass,int containerResourceId,int showAnim,int hideAnim)
    {
        Fragment frag=null;
        try {
            frag=fragmentClass.newInstance();
            FragmentTransaction transaction=getFragmentManager().beginTransaction();
             if(showAnim!=-1 && hideAnim!=-1) transaction.setCustomAnimations(showAnim,hideAnim);
            transaction.replace(containerResourceId, frag);

            transaction.commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
            Log.v("Ошибка перемещения фрагмента replaceContentFragment()");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.v("Ошибка перемещения фрагмента replaceContentFragment()");
        }
        return frag;
    }



    /**
     * Заменит в контейнере форакменты на этот
     * @param fragmentClass
     * @param containerResourceId
     * @return
     */
    public Fragment replaceContentFragment(Class<? extends Fragment> fragmentClass,int containerResourceId)
    {
       return this.replaceContentFragment(fragmentClass,containerResourceId,-1,-1);
    }


    /**
     *
     * @param containerResourceId
     * @param showAnim  id ресурса аниматора для появления нового фрагмента
     * @param hideAnim id ресурса аниматора для для исчезновения предыдущего
     */
    public void removeContentFragment(int containerResourceId,int showAnim,int hideAnim)
    {
        Fragment frag=getFragmentManager().findFragmentById(containerResourceId);
        if(frag==null)
        {
            Log.v("Фрагмент не найден при удалении removeContentFragment()");
            return;
        }
        FragmentTransaction transaction=getFragmentManager().beginTransaction();
       if(showAnim!=-1 && hideAnim!=-1)transaction.setCustomAnimations(showAnim,hideAnim);
        transaction.remove(frag);
        transaction.commit();
    }

    public void removeContentFragment(int containerResourceId)
    {
     this.removeContentFragment(containerResourceId, -1, -1);
    }


/**
    * добавляет фрагмент в контейнер
    * @param fragmentClass класс фрагмента
    * @param containerResourceId идентификатор ресурса ID контейнера
    * @param tag присваеваемый таг этому фрагменту
 * @param showAnim  id ресурса аниматора для появления нового фрагмента
 * @param hideAnim id ресурса аниматора для для исчезновения предыдущего
    * @return Вернет созданный фрагмент
    */

    public Fragment addContentFragment(Class<? extends Fragment> fragmentClass,int containerResourceId,String tag,int showAnim,int hideAnim)
    {
        Fragment frag=null;
        try {
            frag=fragmentClass.newInstance();
            FragmentTransaction transaction=getFragmentManager().beginTransaction();
            if(showAnim!=-1 && hideAnim!=-1) transaction.setCustomAnimations(showAnim,hideAnim);
            transaction.add(containerResourceId,frag,tag);

            transaction.commit();
        } catch (InstantiationException e) {
            e.printStackTrace();
            Log.v("Ошибка добавления  фрагмента addFragment()");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            Log.v("Ошибка добавления фрагмента addFragment()");
        }
        return frag;
    }

    /**
     * добавляет фрагмент в контейнер
     * @param fragmentClass класс фрагмента
     * @param containerResourceId идентификатор ресурса ID контейнера
     * @param tag присваеваемый таг этому фрагменту
     * @return Вернет созданный фрагмент
     */

    public Fragment addContentFragment(Class<? extends Fragment> fragmentClass,int containerResourceId,String tag)
    {

        return this.addContentFragment(fragmentClass,containerResourceId,tag,-1,-1);
    }

    /**
     * Удаляет все фрагменты контейнера
     *
     * @param containerResourceId ресурсный id контейнера
     */
    public void removeContentFragments(int containerResourceId)
    {
        Fragment frag=getFragmentManager().findFragmentById(containerResourceId);

        while(frag!=null)
        {
            FragmentTransaction transaction=getFragmentManager().beginTransaction();
            transaction.remove(frag);
            transaction.commit();
            frag=getFragmentManager().findFragmentById(containerResourceId);
        }




    }
    /**
     * Проверка существют ли фрагменты в контейнере
     *
     * @param containerResourceId ресурсный id контейнера
     *  @return true если есть фрагменты в контейнере
     */
    public boolean isConteinedFragments(int containerResourceId)
    {
        Fragment frag=getFragmentManager().findFragmentById(containerResourceId);

       if(frag!=null) return true;
        else return false;




    }

    /**
     * Проверка существют ли фрагменты в контейнере
     *
     * @param containerResourceId ресурсный id контейнера
     *  @return true если есть фрагменты в контейнере
     */
    public boolean isConteinedFragmentsClass(int containerResourceId,Class<? extends Fragment> fragmentClass)
    {
        Fragment frag=getFragmentManager().findFragmentById(containerResourceId);
        if(frag==null) return false;


        if(frag.getClass() == fragmentClass) return true;
        else return false;






    }


}
