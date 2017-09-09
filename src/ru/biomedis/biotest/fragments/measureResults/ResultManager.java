package ru.biomedis.biotest.fragments.measureResults;

import android.app.Fragment;
import ru.biomedis.biotest.BaseActivity;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.RawDataProcessor;
import ru.biomedis.biotest.fragments.DividerFragment;
import ru.biomedis.biotest.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Касс управляет фраментами результатов измерения.
 * можно создавать и использовать в любой активности
 * Для правильной рабоы нужно только добавить в  public ResultManager(BaseActivity ctx) вызов  resFrag.add(new FragmentResultContainer(Класс фрагмента отображения.class,"Имя пункта меню"));
 * Created by Anama on 20.11.2014.
 */
public class ResultManager
{
    private RawDataProcessor rawDataProcessor;
    private List<FragmentResultContainer> resFrag=new ArrayList<FragmentResultContainer>();
    private BaseActivity ctx;

  public  class FragmentResultContainer
    {
        private Class<? extends Fragment> fragment;
        private String menuName;
        private Fragment instance;

        FragmentResultContainer(Class<? extends Fragment> fragment, String menuName) {
            this.fragment = fragment;
            this.menuName = menuName;
        }

        public Class<? extends Fragment> getFragment() {
            return fragment;
        }

        public String getMenuName() {
            return menuName;
        }

        public Fragment getInstance()
        {
            return instance;
        }

        private void setInstance(Fragment instance)
        {
            this.instance = instance;
        }
    }


    public void setRawDataProcessor(RawDataProcessor rawDataProcessor) {
        this.rawDataProcessor = rawDataProcessor;
    }

    public RawDataProcessor getRawDataProcessor() {
        return rawDataProcessor;
    }


    public ResultManager(BaseActivity ctx)
    {
        this.ctx=ctx;

        if(!(ctx  instanceof BaseActivity)) {throw new RuntimeException("Контекст активности("+ctx.getClass().getCanonicalName()+") не уноследован от BaseActivity");}
        resFrag.add(new FragmentResultContainer(IntervalsResult.class,ctx.getString(R.string.sm_rr)));
        resFrag.add(new FragmentResultContainer(HistogrammResult.class,ctx.getString(R.string.sm_hist)));
        resFrag.add(new FragmentResultContainer(SpectrResult.class,ctx.getString(R.string.sm_spectrum)));
        resFrag.add(new FragmentResultContainer(IndexesResult.class,ctx.getString(R.string.sm_indexes)));
        resFrag.add(new FragmentResultContainer(MeridiansResult.class,ctx.getString(R.string.sm_meridians)));
        resFrag.add(new FragmentResultContainer(ScaterResult.class,ctx.getString(R.string.sm_scaterogramm)));
        resFrag.add(new FragmentResultContainer(Diagnose.class,ctx.getString(R.string.sm_diagnose)));


    }

    /**
     * Список фрагментв результатов
     * @return
     */
    public List<FragmentResultContainer> getResFragments() {
        return resFrag;
    }


    /**
     *
     * @param nameMenu
     * @param contextResourceId
     * @param showAnim  id ресурса аниматора для появления нового фрагмента
     * @param hideAnim id ресурса аниматора для для исчезновения предыдущего
     * @return
     */
    public Fragment replaceFragment(String nameMenu,int contextResourceId,int showAnim,int hideAnim)
    {




        FragmentResultContainer fc=null;
      for(FragmentResultContainer itm:this.getResFragments()) if(itm.getMenuName().equals(nameMenu)){fc=itm;break;}


        Fragment fragment = this.ctx.replaceContentFragment(fc.getFragment(), contextResourceId,showAnim,hideAnim);
        if(!(fragment instanceof BaseResultFragment)){throw new RuntimeException("Целевой фрагмент не наследует BaseResultFragment("+fragment.getClass().getCanonicalName()+") не уноследован от BaseActivity");}
        ((BaseResultFragment)fragment).setRawDataProcessor(this.rawDataProcessor);

        fc.setInstance(fragment);//установим обхъекст фрагметра


        return fragment;
    }
    /**
     * Испльзует функцинал BaseActivity для замены фрагментов в активнсти.
     * @param nameMenu имя пункта меню из списка менеджера
     * @param contextResourceId id куда вставить фрагмент
     * @return
     */
    public Fragment replaceFragment(String nameMenu,int contextResourceId)
    {




        FragmentResultContainer fc=null;
        for(FragmentResultContainer itm:this.getResFragments()) if(itm.getMenuName().equals(nameMenu)){fc=itm;break;}


        Fragment fragment = this.ctx.replaceContentFragment(fc.getFragment(), contextResourceId);
        if(!(fragment instanceof BaseResultFragment)){throw new RuntimeException("Целевой фрагмент не наследует BaseResultFragment("+fragment.getClass().getCanonicalName()+") не уноследован от BaseActivity");}
        ((BaseResultFragment)fragment).setRawDataProcessor(this.rawDataProcessor);

        fc.setInstance(fragment);//установим обхъекст фрагметра


        return fragment;
    }

    /**
     *
     * @param contextResourceId
     * @param showAnim  id ресурса аниматора для появления нового фрагмента
     * @param hideAnim id ресурса аниматора для для исчезновения предыдущего
     * @return
     */
    public  List<FragmentResultContainer> addFragments(int contextResourceId,int showAnim,int hideAnim)
    {

        Fragment fm;
        for(FragmentResultContainer itm:this.getResFragments())
        {
          fm=this.ctx.addContentFragment(itm.getFragment(), contextResourceId, itm.getMenuName(),showAnim,hideAnim);
            if(!(fm instanceof BaseResultFragment)){throw new RuntimeException("Целевой фрагмент не наследует BaseResultFragment("+fm.getClass().getCanonicalName()+") не уноследован от BaseActivity");}

            itm.setInstance(fm);
            ((BaseResultFragment)fm).setRawDataProcessor(this.rawDataProcessor);

        }
        return this.getResFragments();
    }
    /**
     * Добавить все фрагменты в разметку в контейнер, нужно использовать типа LinearLayout
     * Вернет List<FragmentResultContainer> в элементах которого будут наши ссылки на фрагменты
     * @param contextResourceId
     */

    public  List<FragmentResultContainer> addFragments(int contextResourceId)
    {

        Fragment fm;
        for(FragmentResultContainer itm:this.getResFragments())
        {
            fm=this.ctx.addContentFragment(itm.getFragment(), contextResourceId, itm.getMenuName());

            this.ctx.addContentFragment(DividerFragment.class, contextResourceId, itm.getMenuName()+"_div");

            if(!(fm instanceof BaseResultFragment)){throw new RuntimeException("Целевой фрагмент не наследует BaseResultFragment("+fm.getClass().getCanonicalName()+") не уноследован от BaseActivity");}

            itm.setInstance(fm);
            ((BaseResultFragment)fm).setRawDataProcessor(this.rawDataProcessor);

        }
        return this.getResFragments();
    }
    /**
     * Очистит контейнер от всех фрагментов
     * @param containerResourceID
     */
    public void clearContainer(int containerResourceID)
    {
        this.ctx.removeContentFragments(containerResourceID);
    }

    /**
     * Существуют ли в контейнере фрагменты
     * @param containerResourceID
     * @return
     */
    public boolean isContainedFragments(int containerResourceID)
    {
        return this.ctx.isConteinedFragments(containerResourceID);
    }
}
