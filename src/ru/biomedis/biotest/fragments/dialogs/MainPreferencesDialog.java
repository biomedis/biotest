package ru.biomedis.biotest.fragments.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.biomedis.biotest.AppOptions;
import ru.biomedis.biotest.BiotestApp;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.fragments.IndexesDinamic.IndexesDinamicFragment;
import ru.biomedis.biotest.util.Log;

import java.util.Map;


/**
 *Диалог основных опций. Можно сделать его универсальным в будущем, он принимать может любые настройки именять их(это для след программы)
 * Created by Anama on 16.12.2014.
 */
public class MainPreferencesDialog extends DialogStyling
{

    private IndexesDinamicFragment.FilteredIndexes indexesFilter;

    private LinearLayout containerItems;
    private ViewGroup containerDLG;
    private ScrollView scrollItems;
private AppOptions mainOpt;

    public static MainPreferencesDialog newInstance()
    {

        MainPreferencesDialog dlg=new MainPreferencesDialog();

        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);

        return dlg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mainOpt= ((BiotestApp)getActivity().getApplicationContext()).getMainOptions();
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.main_options_dialog,container,false);
styling();


        containerItems= (LinearLayout) view.findViewById(R.id.itemContainer);
        scrollItems=(ScrollView)view.findViewById(R.id.scrollItems);
       //заполним контейнер


       final  float deltaPorog = mainOpt.getFloat("lowPorogPulse").getMax()-mainOpt.getFloat("lowPorogPulse").getMin();

        float v = (mainOpt.getFloat("lowPorogPulse").getValue() - mainOpt.getFloat("lowPorogPulse").getMin()) / deltaPorog;

        SeekBar seekBarPorog = (SeekBar)view.findViewById(R.id.seekBar);
        seekBarPorog.setMax(100);
        seekBarPorog.setProgress((int)(v*100));

        seekBarPorog.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar)
            {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                mainOpt.getFloat("lowPorogPulse").setValue(deltaPorog*seekBar.getProgress()/100+ mainOpt.getFloat("lowPorogPulse").getMin());
                mainOpt.updateItemValue(mainOpt.getFloat("lowPorogPulse"));

                Log.v(mainOpt.getFloat("lowPorogPulse").getValue() + "");
            }
        });
        ((Button)view.findViewById(R.id.lowPorogDefault)).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mainOpt.getOption("lowPorogPulse").resetToDefault();
                mainOpt.updateItemValue( mainOpt.getOption("lowPorogPulse"));

                float vv=(mainOpt.getFloat("lowPorogPulse").getValue() - mainOpt.getFloat("lowPorogPulse").getMin()) / deltaPorog;
                seekBarPorog.setProgress((int)(vv*100));
            }
        });


        getDialog().setTitle(getString(R.string.options_title));

        return view;
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


    }



}
