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
import ru.biomedis.biotest.sql.entity.Profile;
import ru.biomedis.biotest.util.MidifyViews.BiotestToast;

/**
 * Created by Anama on 25.10.2014.
 */
public class EditProfileDialog extends DialogStyling
{
    public static String TAG="EditProfileDialog";

    private Button buttonProfileSave;
    private EditText name;
    private EditText comment;
    private int bgNameColor;
    private static String EXTRA_PROFILE_ENTITY="ru.biomedis.profile.entity";
    private Profile editedProfile;

    private  ActionListener actionListener=null;

    public static EditProfileDialog newInstance(Profile profile)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PROFILE_ENTITY, profile);

        EditProfileDialog dlg=new EditProfileDialog();
        dlg.setArguments(args);
        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);
        return dlg;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        editedProfile= (Profile)getArguments().getSerializable(EXTRA_PROFILE_ENTITY);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        styling();

      View  view=  inflater.inflate(R.layout.edit_profile_dialog,container,false);
        getDialog().setTitle(getString(R.string.ep_title));

        if(editedProfile==null) {  BiotestToast.makeMessageShow(getActivity(), getString(R.string.error_get_profile), Toast.LENGTH_SHORT, BiotestToast.ERROR_MESSAGE); return view;}

        buttonProfileSave= (Button)view.findViewById(R.id.buttonProfileSave);
        name= (EditText)view.findViewById(R.id.profileName);
        name.setText(editedProfile.getName());
        name.setFocusableInTouchMode(true);
        name.requestFocus();
        comment= (EditText)view.findViewById(R.id.profileComment);
        comment.setText(editedProfile.getComment());
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


                       editedProfile.setComment(comment.getText().toString());
                       editedProfile.setName(name.getText().toString());

                       app.getModelDataApp().updateProfile(editedProfile);



                       if(actionListener!=null)actionListener.onProfileSaved(editedProfile);

                       BiotestToast.makeMessageShow(v.getContext(), getString(R.string.succes_saved_profile), Toast.LENGTH_SHORT, BiotestToast.NORMAL_MESSAGE);
                       dismiss();
                   }catch (Exception ex){
                       BiotestToast.makeMessageShow(v.getContext(), getString(R.string.error_saved_profile), Toast.LENGTH_SHORT, BiotestToast.ERROR_MESSAGE);
                       if(actionListener!=null)actionListener.errorEdit();
                       return;
                   }


                }else
                {
                    BiotestToast.makeMessageShow(v.getContext(), getString(R.string.need_fill_name_profile), Toast.LENGTH_SHORT,BiotestToast.WARNING_MESSAGE);
                    name.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));

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
        public void onProfileSaved(Profile profile);
        public void errorEdit();

    }


}
