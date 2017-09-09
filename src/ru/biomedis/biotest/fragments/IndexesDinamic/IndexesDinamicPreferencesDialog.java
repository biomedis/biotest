package ru.biomedis.biotest.fragments.IndexesDinamic;

import android.app.DialogFragment;

import android.content.DialogInterface;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.fragments.dialogs.DialogStyling;

import java.util.Map;


/**
 * Диалог выбора видимости индексов в графиках динамики показателей.
 * Диалог только заполняет объект фильтра, а вызвавший этот диалог должен обновить параметры sharedPreferences
 * результат кликов сохранится в обхекте фильтра что мы передавали при инциализации в объект класса
 * При любом закрытии окна будет сохранено состаяние пунктов выбора для обработке из вне
 * Created by Anama on 16.12.2014.
 */
public class IndexesDinamicPreferencesDialog extends DialogStyling
{
    private static  String EXTRA_INDEXES="EXTRA_INDEXESIndexesDinamicPreferencesDialog";
    private IndexesDinamicFragment.FilteredIndexes indexesFilter;
    private  ActionListener actionListener=null;
    private LinearLayout containerItems;
    private ViewGroup containerDLG;
    private ScrollView scrollItems;


    public static IndexesDinamicPreferencesDialog newInstance(IndexesDinamicFragment.FilteredIndexes pFilter)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_INDEXES, pFilter);
        IndexesDinamicPreferencesDialog dlg=new IndexesDinamicPreferencesDialog();
        dlg.setArguments(args);
        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);

        return dlg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        indexesFilter= (IndexesDinamicFragment.FilteredIndexes)getArguments().getSerializable(EXTRA_INDEXES);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.indexes_dinamic_preferences_dialog,container,false);
styling();

        containerItems= (LinearLayout) view.findViewById(R.id.itemContainer);
        scrollItems=(ScrollView)view.findViewById(R.id.scrollItems);
       //заполним контейнер
        View viewItem;
        TextView titleItem;
        CheckBox checkBoxItem;

        for (Map.Entry<String, Boolean> entry : indexesFilter.getIndexesVisibilities().entrySet()) {
             viewItem = inflater.inflate(R.layout.indexes_view_preferences_list_item, containerItems, false);


             //инициализируем контент пунктов
             checkBoxItem = (CheckBox) viewItem.findViewById(R.id.checkIndexView);

             titleItem = (TextView) viewItem.findViewById(R.id.indexTitle);

            checkBoxItem.setChecked(entry.getValue());//видимость на графике
            checkBoxItem.setOnCheckedChangeListener(checkListener);
            checkBoxItem.setTag(entry.getKey());

            titleItem.setText(entry.getKey()+" - "+indexesFilter.getIndexDescription(entry.getKey()));//имя



                    containerItems.addView(viewItem);// добавим пункт



        }


        getDialog().setTitle(getString(R.string.id_list_params));

        return view;
    }

    /**
     * Слушатель для чекбоксов пунктов.
     */
  private  CompoundButton.OnCheckedChangeListener checkListener =  new CompoundButton.OnCheckedChangeListener()
  {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
      {
          indexesFilter.updateIndexVisibility((String)buttonView.getTag(),isChecked);
      }


  };


    @Override
    public void onResume()
    {
        super.onResume();


    }

    /**
     * завершить положительно диалог
     */
    private void complete()
    {
        if(this.actionListener!=null)this.actionListener.onComplete();

    }

    @Override
    public void onDismiss(DialogInterface dialog)
    {
        complete();
        super.onDismiss(dialog);
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
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
    public void onPause()
    {
        super.onPause();
        this.dismiss();
    }

    @Override
    public void onCancel(DialogInterface dialog)
    {
        super.onCancel(dialog);
        //if(this.actionListener!=null)this.actionListener.onCancel();

    }
    public void setActionListener(ActionListener actionListener)
    {
        this.actionListener=actionListener;
    }

    public void removeActionListener()
    {
        this.actionListener=null;
    }

    public interface ActionListener{

        public void onComplete();

    }


}
