package ru.biomedis.biotest.fragments.dialogs;

import android.app.ActionBar;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;
import ru.biomedis.biotest.R;

/**
 * Created by Anama on 25.10.2014.
 */
public class YesNoDialog extends DialogStyling
{

    private Button okButton;
    private Button cancelButton;
    private TextView descr;
    private  ActionListener actionListener=null;
    private static String EXTRA_TITLE="ru.biomedis.yesno.title";
    private static String EXTRA_DESCRIPTION="ru.biomedis.yesno.desc";
    private static String EXTRA_YES_TEXT="ru.biomedis.yesno.yText";
    private static String EXTRA_NO_TEXT="ru.biomedis.yesno.noText";

    private String title;
    private String description;
    private String yes;
    private String no;









    public static YesNoDialog newInstance(String title,String description,String yesText,String noText)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TITLE, title);
        args.putSerializable(EXTRA_DESCRIPTION, description);
        args.putSerializable(EXTRA_YES_TEXT, yesText);
        args.putSerializable(EXTRA_NO_TEXT, noText);

        YesNoDialog dlg=new YesNoDialog();
        dlg.setArguments(args);
        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);
        return dlg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        title= getArguments().getString(EXTRA_TITLE);
        description= getArguments().getString(EXTRA_DESCRIPTION);
        yes= getArguments().getString(EXTRA_YES_TEXT);
        no= getArguments().getString(EXTRA_NO_TEXT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        styling();
        getDialog().setTitle(title);

        View view = inflater.inflate(R.layout.yes_no_dialog, container, true);


         okButton = (Button) view.findViewById(R.id.ok);
        okButton.setText(yes);
         cancelButton = (Button) view.findViewById(R.id.cancel);
        cancelButton.setText(no);
         descr=(TextView) view.findViewById(R.id.dlgDescription);

         descr.setText(description);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dismiss();
                if(actionListener!=null) actionListener.ok();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dismiss();
                if(actionListener!=null)actionListener.cancel();
            }
        });


        return view;

    }


    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        if(actionListener!=null)actionListener.cancel();
    }

    public void setActionListener(ActionListener actionListener)
    {
        this.actionListener=actionListener;
    }

    public void removeActionListener()
    {
        this.actionListener=null;
    }

    public interface ActionListener
    {
        public void cancel();
        public void ok();
    }

}
