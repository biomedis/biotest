package ru.biomedis.biotest.fragments.dialogs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import ru.biomedis.biotest.R;

import java.util.Calendar;
import java.util.Date;

/**
 * Диалог выбора даты с установкой начальной даты и возможностью задать actionListener
 * Created by Anama on 15.12.2014.
 */
public class DatePickerSimple extends DialogStyling
{

    private Date startDate;
    private Date minDate;
    private Date maxDate;
    private ActionListener actionListener;
    private Date settedDate;
    private DatePicker datePicker;
    private Button buttonOk;
    private Button buttonCancel;
    public static String EXTRA_DATEPICKER_START_DATE="EXTRA_DATEPICKER_START_DATE";
    public static String EXTRA_DATEPICKER_MIN_DATE="EXTRA_DATEPICKER_MIN_DATE";
    public static String EXTRA_DATEPICKER_MAX_DATE="EXTRA_DATEPICKER_MAX_DATE";

    /**
     * Создает экземпляр диалога, инициализирует его датой startDate. Можем задавать пределы выбора дат или null для бесконечного предела в нужную сторону
     * @param startDate дата которая булет отображена в диалоге
     *         @param minDate нижний предел выбора даты
     *         @param maxDate верхний предел выбора даты
     * @return
     */
    public static DatePickerSimple newInstance(Date startDate,Date minDate,Date maxDate)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATEPICKER_START_DATE, startDate);
       if(minDate!=null) args.putSerializable(EXTRA_DATEPICKER_MIN_DATE, minDate);
       if(maxDate!=null) args.putSerializable(EXTRA_DATEPICKER_MAX_DATE, maxDate);

        DatePickerSimple dlg=new DatePickerSimple();
        dlg.setArguments(args);
        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);
        return dlg;
    }

    /**
     * Создает экземпляр диалога, инициализирует его датой startDate не ограничивает макс и мин диапазон
     * @param startDate дата которая булет отображена в диалоге
     * @return
     */
    public static DatePickerSimple newInstance(Date startDate)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_DATEPICKER_START_DATE, startDate);


        DatePickerSimple dlg=new DatePickerSimple();
        dlg.setArguments(args);
        dlg.setStyle(DialogFragment.STYLE_NORMAL, R.style.BiotestDialog);
        return dlg;
    }
    /**
     * не нужно вызывать напрямую
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startDate= (Date)getArguments().getSerializable(EXTRA_DATEPICKER_START_DATE);
        if(getArguments().containsKey(EXTRA_DATEPICKER_MIN_DATE))  minDate= (Date)getArguments().getSerializable(EXTRA_DATEPICKER_MIN_DATE);
        if(getArguments().containsKey(EXTRA_DATEPICKER_MAX_DATE))maxDate= (Date)getArguments().getSerializable(EXTRA_DATEPICKER_MAX_DATE);




    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        styling();


        View view = inflater.inflate(R.layout.datepicker_dialog,container,false);
         datePicker = (DatePicker) view.findViewById(R.id.pickerdate);
        getDialog().setTitle(getString(R.string.data_picker_title));

         buttonOk = (Button) view.findViewById(R.id.ok);
        buttonOk.setOnClickListener(onclick);
         buttonCancel = (Button)view.findViewById(R.id.cancel);
        buttonCancel.setOnClickListener(onclick);


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        if(minDate!=null)  datePicker.setMinDate(minDate.getTime());
        if(maxDate!=null)  datePicker.setMaxDate(maxDate.getTime());

        datePicker.setCalendarViewShown(false);
        settedDate=startDate;//это если пользователя устроит эта дата

        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener()
        {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth)
            {
                Calendar calendar=  Calendar.getInstance();
                calendar.set(year,monthOfYear,dayOfMonth);
                settedDate =calendar.getTime();
            }
        });
        return view;
    }


Button.OnClickListener onclick =new View.OnClickListener()
{
    @Override
    public void onClick(View v)
    {
        switch(v.getId())
        {
            case R.id.ok:
                dismiss();
               break;
            case R.id.cancel:
                settedDate=null;
                dismiss();
                break;
        }



    }
};



   /*

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        DatePickerDialog dlg=new DatePickerDialog(getActivity(),myCallBack,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));


        if(minDate!=null)   dlg.getDatePicker().setMinDate(minDate.getTime());
        if(maxDate!=null)   dlg.getDatePicker().setMaxDate(maxDate.getTime());

        dlg.getDatePicker().setCalendarViewShown(false);
        return dlg;
    }


    DatePickerDialog.OnDateSetListener myCallBack = new  DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {


         Calendar calendar=  Calendar.getInstance();
            calendar.set(year,monthOfYear,dayOfMonth);
          settedDate =calendar.getTime();


        }
    };


*/




    @Override
    public void onDismiss(DialogInterface dialog)
    {
        super.onDismiss(dialog);
        if(actionListener!=null)
        {
            if(settedDate!=null)actionListener.onComplete(settedDate,getTag());
            else actionListener.onCancel(getTag());
        }


    }




    public void setActionListener(ActionListener action)
    {
        actionListener=action;
    }

    /**
     * нужно установить обработчик ччтобы получать результат
     */
    public interface ActionListener
    {
        /**
         * Укажет данные которые выбраны
         * @param val Данные
         * @param tag таг который мы указали про создании. Можно использовать для идентификации диалога
         */
        public void onComplete(Date val,String tag);
        public void onCancel(String tag);
    }
}
