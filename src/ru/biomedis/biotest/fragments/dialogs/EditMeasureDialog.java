package ru.biomedis.biotest.fragments.dialogs;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import ru.biomedis.biotest.BiotestApp;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.sql.entity.Measure;
import ru.biomedis.biotest.sql.entity.Profile;
import ru.biomedis.biotest.util.MidifyViews.BiotestToast;

/**
 * Created by Anama on 25.10.2014.
 */
public class EditMeasureDialog extends DialogStyling
{
    public static String TAG="EditMeasureDialog";

    private Button buttonMeasureSave;
    private EditText note;


    private static String EXTRA_MEASURE_ENTITY="ru.biomedis.measure.entity";
    private Measure editedMeasure;

    private  ActionListener actionListener=null;

    public static EditMeasureDialog newInstance(Measure measure)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_MEASURE_ENTITY, measure);

        EditMeasureDialog dlg=new EditMeasureDialog();
        dlg.setArguments(args);
        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);
        return dlg;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editedMeasure= (Measure)getArguments().getSerializable(EXTRA_MEASURE_ENTITY);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        styling();
      View  view=  inflater.inflate(R.layout.edit_measure_dialog,container,false);
        getDialog().setTitle(getString(R.string.em_title));

        if(editedMeasure==null) {   BiotestToast.makeMessageShow(getActivity(), getString(R.string.em_error_get_measure), Toast.LENGTH_SHORT, BiotestToast.ERROR_MESSAGE); return view;}

        buttonMeasureSave = (Button)view.findViewById(R.id.saveMeasure);
        note = (EditText)view.findViewById(R.id.note);
        note.setText(editedMeasure.getComment());
        note.setFocusableInTouchMode(true);
        note.requestFocus();





        buttonMeasureSave.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {


                    BiotestApp app = (BiotestApp) getActivity().getApplicationContext();

                    try {


                        editedMeasure.setComment(note.getText().toString());
                        editedMeasure.setComment(note.getText().toString());

                        app.getModelDataApp().updateMeasure(editedMeasure);


                        if (actionListener != null) actionListener.onMeasureSaved(editedMeasure);

                        BiotestToast.makeMessageShow(v.getContext(), getString(R.string.em_measure_success_updated), Toast.LENGTH_SHORT, BiotestToast.NORMAL_MESSAGE);
                        dismiss();
                    } catch (Exception ex) {
                        BiotestToast.makeMessageShow(v.getContext(), getString(R.string.em_error_updated_measure), Toast.LENGTH_SHORT,BiotestToast.ERROR_MESSAGE);
                        if (actionListener != null) actionListener.errorEdit();
                        return;
                    }



            }
        });

        return view;

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
        public void onMeasureSaved(Measure editedMeasure);
        public void errorEdit();

    }


}
