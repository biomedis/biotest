package ru.biomedis.biotest.fragments.dialogs;

import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
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
public class NewProfileDialog extends DialogStyling
{
    public static String TAG="NewProfileDialog";

    private Button buttonProfileSave;
    private EditText name;
    private EditText comment;
    private int bgNameColor;

    private  ActionListener actionListener=null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        styling();
      View  view=  inflater.inflate(R.layout.create_profile_dialog,container,false);
        getDialog().setTitle(getString(R.string.cp_title));


        buttonProfileSave= (Button)view.findViewById(R.id.buttonProfileSave);
        name= (EditText)view.findViewById(R.id.profileName);
        name.setFocusableInTouchMode(true);
        name.requestFocus();
        comment= (EditText)view.findViewById(R.id.profileComment);
        bgNameColor=name.getDrawingCacheBackgroundColor();

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) name.setBackgroundColor(bgNameColor);
            }
        });

        buttonProfileSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!name.getText().toString().isEmpty()) {
                    BiotestApp app = (BiotestApp) getActivity().getApplicationContext();

                   try
                   {
                       Profile profile = app.getModelDataApp().createProfile(name.getText().toString(), comment.getText().toString());
                       if(profile==null) { BiotestToast.makeMessageShow(v.getContext(), getString(R.string.error_create_profile), Toast.LENGTH_SHORT,BiotestToast.ERROR_MESSAGE); return;}

                      if(actionListener!=null) actionListener.onProfileCreated(profile);

                       BiotestToast.makeMessageShow(v.getContext(), getString(R.string.success_created_profile), Toast.LENGTH_SHORT,BiotestToast.NORMAL_MESSAGE);
                       dismiss();
                   }catch (Exception ex){
                       BiotestToast.makeMessageShow(v.getContext(), getString(R.string.error_create_profile), Toast.LENGTH_SHORT,BiotestToast.ERROR_MESSAGE);
                   }


                }else
                {
                    BiotestToast.makeMessageShow(v.getContext(), getString(R.string.need_fill_all_fields), Toast.LENGTH_SHORT,BiotestToast.WARNING_MESSAGE);
                    name.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

                }
            }
        });

        return view;

    }

    public void setActionListener(ActionListener actionListener){this.actionListener=actionListener;}
    public void removeActionListener(){this.actionListener=null;}
    public interface ActionListener{
        public void onProfileCreated(Profile profile);

    }


}
