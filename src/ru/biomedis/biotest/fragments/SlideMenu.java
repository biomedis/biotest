package ru.biomedis.biotest.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import ru.biomedis.biotest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Фрагмент показывающийся первым после запуска главной активности
 * Created by Anama on 14.10.2014.
 */
public class SlideMenu extends Fragment
{

    /**
     * листнер событий меню. Его должна реализовывать активность которая управляет меню.
     */
    public interface OnSlideMenuListner
    {

        public void onSelectedItem(MenuItem item);

    }

    /**
     * Класс инкапсулирующий пункт меню
     */
   public class MenuItem
   {

       /**
        * Идентификатор действия, обрабатывается активностью при поучении события выбора пункта меню
         */
    private String actionId;

       /**
        * Заголовок меню
        */
    private String title;

       /**
        * View которое отображает пункт меню
        */
    private View view;

       /**
        * Устанавливает отображение пункта
        */
    private boolean isVisible=true;

       /**
        * Можно ли взаимодействовать спунктом
        */
   private boolean isEnabled=true;

       public boolean isEnabled() {
           return isEnabled;
       }

       public MenuItem setEnabled(boolean isEnabled) {
           this.isEnabled = isEnabled;
           if(this.getView()!=null)this.getView().setEnabled(isEnabled);
           return this;
       }

       public String getActionId() {
           return actionId;
       }

       public MenuItem setActionId(String actionId) {
           this.actionId = actionId;
           return this;
       }

       public String getTitle() {
           return title;
       }

       public MenuItem setTitle(String title) {
           this.title = title;
           return this;
       }

       public View getView() {
           return view;
       }

       private MenuItem setView(View view) {
           this.view = view;
           return this;
       }

       public boolean isVisible() {
           return isVisible;
       }

       public MenuItem setVisible(boolean isVisible) {
           this.isVisible = isVisible;
           return this;
       }

       /**
        * Осуществляет построение пункта меню. Здесь прописано внутренне устройство пункта
        * @return
        */
       public MenuItem build()
       {
           Button  btn =(Button)layoutInflater.inflate(R.layout.slide_menu_item,layout,false);//если поставить true то метод вернет не тот элемент что в разметке, а корневой те layout
          // Button btn=new Button(getActivity());
           btn.setEnabled(this.isEnabled);
           btn.setText(this.getTitle());
           btn.setOnClickListener(onClickListener);
           btn.setTag(this);
           layout.addView(btn);
            this.setView(btn);
           return this;
       }
   }




   ///////////////////  Сам класс //////////////


    private List<MenuItem> menuItemsList=new ArrayList<MenuItem>();
    private OnSlideMenuListner onSlideMenuListner;//листнер событий меню. Его должна реализовывать активность которая управляет меню.
    private int activeItemPos=-1;//позиция выбранного элемента, или -1 если не выбран ни какой
    private LinearLayout layout;//контейнер для пунктов меню
    //private Activity activity;
    private View.OnClickListener onClickListener;
    private LayoutInflater layoutInflater=null;

    public SlideMenu() {


        super();

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                 setActiveItem((MenuItem) v.getTag(), false);



                onSlideMenuListner.onSelectedItem((MenuItem)v.getTag());
            }
        };
    }




    public void clearMenuList(){this.menuItemsList.clear();}

    /**
     * Добавляет новый item. В конце цепочи применения параметров addMenuItem().setTitle("sdfsdf").build() необходимо вызвать build иначе кнопки не будет
     * @return MenuItem чтобы можно было в одну строку применить все параметры после создания элемента.
     */
    public MenuItem addMenuItem()
    {
        MenuItem item =new MenuItem();
        this.menuItemsList.add(item);


        return item;
    }
    public void removeMenuItem(MenuItem item){this.menuItemsList.remove(item);}
    public void removeMenuItem(int position){this.menuItemsList.remove(position);}

    /**
     * Возвратит активный(выбранный) пункт меню
     * @return
     */
    public MenuItem getActiveMenuItem()
    {
        if(activeItemPos!=-1)return this.menuItemsList.get(activeItemPos);
        else return null;
    }

    /**
     * Установит активный пункт. Тк напрямую мы не можем из активности создать объект пункта меню, то проблем с методом быть не должно.
     * @param activeItem Указываем пунтк
     * @param firedClick Возбудить и событие щелчка на кнопке, что вызовет срабатываение метода в активности true
     */
    public void setActiveItem(MenuItem activeItem,boolean firedClick)
    {
       if(this.menuItemsList.contains(activeItem))
        {

            if(activeItem!=getActiveMenuItem())
            {
                activeItem.getView().setBackgroundResource(R.drawable.button_slide_active_background);
                ((Button) activeItem.getView()).setTextColor(getResources().getColorStateList(R.color.button_slide_active_text_color));
                if(getActiveMenuItem()!=null)
                {
                    getActiveMenuItem().getView().setBackgroundResource(R.drawable.button_slide_background);
                    ((Button)getActiveMenuItem().getView()).setTextColor(getResources().getColorStateList(R.color.button_slide_text_color));
                }
            }





            activeItemPos=this.menuItemsList.indexOf(activeItem);
            if(firedClick==true)onSlideMenuListner.onSelectedItem(activeItem);




        }

    }

    /**
     * Получает следующий за активным пункт.
     * @return
     */
    public MenuItem getNextMenuItem()
    {
       int lastIndex = this.menuItemsList.size()-1;

        if(activeItemPos!=-1)
        {

            if(activeItemPos<lastIndex) return this.menuItemsList.get(activeItemPos+1);else return null;

        }else return null;



    }

    /**
     * Получает предыдущий за активным пункт.
     * @return
     */
    public MenuItem getPrevMenuItem()
    {
        int lastIndex = this.menuItemsList.size()-1;
        if(activeItemPos!=-1)
        {
            if(activeItemPos>0) return this.menuItemsList.get(activeItemPos-1);else return null;
        }else return null;
    }


public final  List<MenuItem> getItemsList(){return  this.menuItemsList;}

    public MenuItem getItemByAction(String action)
    {
        for (MenuItem menuItem : this.menuItemsList) {
            if(menuItem.getActionId().equals(action))return menuItem;
        }
        return null;
    }


    /////////  Обработчики событий жизненного цикла фрагмента //////////
    @Override
    public void onAttach(Activity activity) {

        super.onAttach(activity);

        try {
            onSlideMenuListner=(OnSlideMenuListner)activity;//получим ссылку на реализованный интерфейс
            layoutInflater=activity.getLayoutInflater();
            //если не реализовано, то выпадет исключение
        } catch (ClassCastException ex) {
            throw new ClassCastException(activity.toString()+" должен реализовывать интерфейс SlideMenu.OnSlideMenuListner");

        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.slide_menu_fragment, container, false);
        this.layout= (LinearLayout)view.findViewById(R.id.menuItemContainer);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }



    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }



    /////////  Кастомный код //////////
















}
