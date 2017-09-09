package ru.biomedis.biotest.fragments.IndexesDinamic;

import android.app.Fragment;
import android.os.Bundle;
import android.view.*;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import ru.biomedis.biotest.BiotestApp;
import ru.biomedis.biotest.IndexesDinamicActivity;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.fragments.dialogs.DatePickerSimple;
import ru.biomedis.biotest.sql.ModelDataApp;
import ru.biomedis.biotest.util.Log;
import ru.biomedis.biotest.util.MidifyViews.BiotestToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Фрагмент отображает фильтр по датам для выборки измерений
 * Created by Anama on 13.12.2014.
 */
public class IndexesDinamicFilterFragment extends Fragment
{


    private Button filterButton;//кнопка запуска фильтра
    private Button minDateButton;//кнопка открытия диалога выбора мин даты
    private Button maxDateButton;//кнопка открытия диалога выбора макс даты

    private ActionListener actionListener;
    private Date minDateDB;//минимальная дата в профиле
    private Date maxDateDB;//макс дата в профиле
    private Date settedMinDate;//дата установленная
    private Date settedMaxDate;//дата установленная
    private  SimpleDateFormat sdf=new SimpleDateFormat("dd-MM-yyyy");
    private ModelDataApp modelDataApp;

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

        //получим данные из БД. Определим крайние даты
       // Calendar calendar=Calendar.getInstance();
        //minDateDB=calendar.getTime();
       // calendar.add(Calendar.DAY_OF_MONTH,5);


        modelDataApp=   ((BiotestApp)getActivity().getApplicationContext()).getModelDataApp();



    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
      View view =  inflater.inflate(R.layout.indexes_dinamic_filter_fragment,container,false);


          filterButton=(Button)view.findViewById(R.id.filterButton);
          minDateButton=(Button)view.findViewById(R.id.minDateFilterButton);
          maxDateButton=(Button)view.findViewById(R.id.maxDateFilterButton);


        filterButton.setOnClickListener(onClickButtonListener);
        minDateButton.setOnClickListener(onClickButtonListener);
        maxDateButton.setOnClickListener(onClickButtonListener);

        registerForContextMenu(filterButton);





        return view;
    }


/*
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();



        switch(v.getId())
        {
            case R.id.filterButton:

                inflater.inflate(R.menu.indexes_dinamic_filter_context, menu);

                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {

        switch(item.getItemId())
        {
            case R.id.custominterval:

                //нужно проверить какая дата больше, если пользователь перепутал. И подставить даты верно в поиск

                long settedMinDateTime = settedMinDate.getTime();
                long settedMaxDateTime = settedMaxDate.getTime();
                //выдаем подписчикам сообщение о нажати кнопки поиска(он у нас в  IndexesDinamicActivity в onCreate)
                if(actionListener!=null)
                {
                    if(settedMinDateTime<settedMaxDateTime)actionListener.onAction(settedMinDate,settedMaxDate);
                    else actionListener.onAction(settedMaxDate,settedMinDate);
                }
                return true;

                break;


            case R.id.today:
                return true;
                break;
            case R.id.threeday:
                return true;
                break;
            case R.id.week:
                return true;
                break;
            case R.id.twoweek:
                return true;
                break;
            case R.id.month:
                return true;
                break;
            case R.id.threemonth:
                return true;
                break;
            case R.id.sixmonth:
                return true;
                break;
            case R.id.year:
                return true;
                break;

            default:
                return super.onContextItemSelected(item);

        }

    }


*/

   PopupMenu.OnMenuItemClickListener popupOnClick =new PopupMenu.OnMenuItemClickListener()
   {
       @Override
       public boolean onMenuItemClick(MenuItem item)
       {
           boolean res=true;

           Calendar cal;

           switch(item.getItemId())
           {
               case R.id.custominterval:
                   //нужно проверить какая дата больше, если пользователь перепутал. И подставить даты верно в поиск

               break;


               case R.id.today:
                   cal=Calendar.getInstance();
                   cal.set(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH),0,1);
                   //cal.clear(Calendar.HOUR);
                   //cal.clear(Calendar.MINUTE);
                   settedMinDate=cal.getTime();
                   minDateButton.setText(sdf.format(settedMinDate));

                   cal.add(Calendar.DAY_OF_MONTH,1);
                   settedMaxDate=cal.getTime();
                   maxDateButton.setText(sdf.format(settedMaxDate));


               break;
               case R.id.threeday:

                   cal=Calendar.getInstance();

                   settedMaxDate=cal.getTime();
                   maxDateButton.setText(sdf.format(settedMaxDate));

                   cal.clear(Calendar.HOUR);
                   cal.clear(Calendar.MINUTE);
                   cal.add(Calendar.DAY_OF_MONTH,-3);
                   settedMinDate=cal.getTime();
                   minDateButton.setText(sdf.format(settedMinDate));






               break;
               case R.id.week:

                   cal=Calendar.getInstance();

                   settedMaxDate=cal.getTime();
                   maxDateButton.setText(sdf.format(settedMaxDate));

                   cal.clear(Calendar.HOUR);
                   cal.clear(Calendar.MINUTE);
                   cal.add(Calendar.DAY_OF_MONTH,-7);
                   settedMinDate=cal.getTime();
                   minDateButton.setText(sdf.format(settedMinDate));
               break;
               case R.id.twoweek:
                   cal=Calendar.getInstance();

                   settedMaxDate=cal.getTime();
                   maxDateButton.setText(sdf.format(settedMaxDate));

                   cal.clear(Calendar.HOUR);
                   cal.clear(Calendar.MINUTE);
                   cal.add(Calendar.DAY_OF_MONTH,-14);
                   settedMinDate=cal.getTime();
                   minDateButton.setText(sdf.format(settedMinDate));
               break;
               case R.id.month:
                   cal=Calendar.getInstance();

                   settedMaxDate=cal.getTime();
                   maxDateButton.setText(sdf.format(settedMaxDate));

                   cal.clear(Calendar.HOUR);
                   cal.clear(Calendar.MINUTE);
                   cal.add(Calendar.DAY_OF_MONTH,-30);
                   settedMinDate=cal.getTime();
                   minDateButton.setText(sdf.format(settedMinDate));
               break;
               case R.id.threemonth:
                   cal=Calendar.getInstance();

                   settedMaxDate=cal.getTime();
                   maxDateButton.setText(sdf.format(settedMaxDate));

                   cal.clear(Calendar.HOUR);
                   cal.clear(Calendar.MINUTE);
                   cal.add(Calendar.DAY_OF_MONTH,-90);
                   settedMinDate=cal.getTime();
                   minDateButton.setText(sdf.format(settedMinDate));
               break;
               case R.id.sixmonth:
                   cal=Calendar.getInstance();

                   settedMaxDate=cal.getTime();
                   maxDateButton.setText(sdf.format(settedMaxDate));

                   cal.clear(Calendar.HOUR);
                   cal.clear(Calendar.MINUTE);
                   cal.add(Calendar.MONTH,-6);
                   settedMinDate=cal.getTime();
                   minDateButton.setText(sdf.format(settedMinDate));
               break;
               case R.id.year:
                   cal=Calendar.getInstance();

                   settedMaxDate=cal.getTime();
                   maxDateButton.setText(sdf.format(settedMaxDate));

                   cal.clear(Calendar.HOUR);
                   cal.clear(Calendar.MINUTE);
                   cal.add(Calendar.YEAR,-1);
                   settedMinDate=cal.getTime();
                   minDateButton.setText(sdf.format(settedMinDate));
               break;

               default:
                  res= false;

           }

            if(res!=false)
            {
                //выдаем подписчикам сообщение о нажати кнопки поиска(он у нас в  IndexesDinamicActivity в onCreate)
                if(actionListener!=null)
                {
                    long settedMinDateTime = settedMinDate.getTime();
                    long settedMaxDateTime = settedMaxDate.getTime();
                    if(settedMinDateTime<settedMaxDateTime)actionListener.onAction(settedMinDate,settedMaxDate);
                    else actionListener.onAction(settedMaxDate,settedMinDate);
                }
            }
           return res;
       }
   };



    private void  showPopupFilterMenu(View view)
    {

        PopupMenu popup = new PopupMenu(this.getActivity(), view);
        popup.inflate(R.menu.indexes_dinamic_filter_context);
        popup.setOnMenuItemClickListener(popupOnClick);

        popup.show();
    }




    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);

        IndexesDinamicActivity activity = (IndexesDinamicActivity)getActivity();

        minDateDB=modelDataApp.findMinDate(activity.getProfileID());
        maxDateDB=modelDataApp.findMaxDate(activity.getProfileID());

        settedMaxDate=maxDateDB;
        settedMinDate=minDateDB;

       if(settedMinDate!=null) minDateButton.setText(sdf.format(minDateDB));//установим надписи на кнопках
        else minDateButton.setText("-");
        if(settedMaxDate!=null) maxDateButton.setText(sdf.format(maxDateDB));
        else maxDateButton.setText("-");

    }

    /**
     * Слушатель события выбора даты датапикерами
     */
  private  DatePickerSimple.ActionListener datapickerActionListener = new  DatePickerSimple.ActionListener()
    {

        @Override
        public void onComplete(Date val,String tag)
        {



            //таги мы проставили в View.OnClickListener onClickButtonListener ,ниже
            if(tag.equals("dt1"))
            {
                settedMinDate=val;
                minDateButton.setText(sdf.format(settedMinDate));
            }
            else  if(tag.equals("dt2"))
            {
                settedMaxDate=val;
                maxDateButton.setText(sdf.format(settedMaxDate));
            }
        }

        @Override
        public void onCancel(String tag)
        {
            //BiotestToast.makeMessageShow(getActivity(), getString(R.string.cancel), Toast.LENGTH_SHORT,BiotestToast.NORMAL_MESSAGE);

            if(tag.equals("dt1"))
            {
                settedMinDate=minDateDB;
                minDateButton.setText(sdf.format(settedMinDate));
            }
            else  if(tag.equals("dt2"))
            {
                settedMaxDate=maxDateDB;
                maxDateButton.setText(sdf.format(settedMaxDate));
            }

        }
    };

    /**
     * Обработчк нажатия кнопок
     */
   private View.OnClickListener onClickButtonListener=new View.OnClickListener()
    {
        DatePickerSimple dlg;
       @Override
       public void onClick(View v)
       {

           switch (v.getId())
           {
               case R.id.minDateFilterButton://кнопка установки мин даты

                   Calendar cal=Calendar.getInstance();
                   // это для того чтобы отображаемая дата в датапикере была в промежутке дат, иначе диалог падает с ошибкой
                 long tm= minDateDB.getTime()+(long)Math.floor(maxDateDB.getTime() - minDateDB.getTime())/2 ;
                   cal.setTimeInMillis(tm);

                    dlg=DatePickerSimple.newInstance(cal.getTime(),minDateDB,maxDateDB);
                   dlg.setActionListener(datapickerActionListener);
                   dlg.show(getFragmentManager(),"dt1");

                   break;
               case R.id.maxDateFilterButton://кнопка установки макс даты

                   Calendar cal1=Calendar.getInstance();
                   // это для того чтобы отображаемая дата в датапикере была в промежутке дат, иначе диалог падает с ошибкой
                   long tm1= minDateDB.getTime()+(long)Math.floor(maxDateDB.getTime() - minDateDB.getTime())/2 ;
                   cal1.setTimeInMillis(tm1);
                    dlg=DatePickerSimple.newInstance(cal1.getTime(),minDateDB,maxDateDB);
                    dlg.setActionListener(datapickerActionListener);
                   dlg.show(getFragmentManager(),"dt2");
                   break;
               case R.id.filterButton://кнопка запуска фильтра
                   showPopupFilterMenu(filterButton);
                   break;
           }




       }
   };




public void setActionListener(ActionListener action)
{
    actionListener=action;
}
    public interface ActionListener
    {
        /**
         * Срабатывает когда мы нажимаем на кнопку показать
         * @param min дата минимума
         * @param max дата максимума
         */
        public void onAction(Date min,Date max);
    }
}
