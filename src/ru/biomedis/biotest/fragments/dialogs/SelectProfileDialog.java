package ru.biomedis.biotest.fragments.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import ru.biomedis.biotest.BiotestApp;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.sql.ModelDataApp;
import ru.biomedis.biotest.sql.entity.Profile;
import ru.biomedis.biotest.util.Log;
import ru.biomedis.biotest.util.MidifyViews.BiotestToast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anama on 25.10.2014.
 */
public class SelectProfileDialog extends DialogStyling
{


    private TextView descr;
    private  ActionListener actionListener=null;
    private static String EXTRA_TITLE="ru.biomedis.selectProfileDlg.title";
    private static String EXTRA_DESCRIPTION="ru.biomedis.selectProfileDlg.desc";
    private String title;
    private String description;
    private List<Profile> listProfile=new ArrayList<Profile>();
    private ListView vList;
    private ArrayAdapter<String> adapter;

    BiotestApp appContext=null;
    private ModelDataApp modelDataApp=null;





private String [] getStringArrayProfilesName()
{

    String [] s=new String[listProfile.size()];
    int i=0;
    for (Profile p : listProfile) {
        s[i++]=p.getName();
    }
return s;

}


    public static SelectProfileDialog newInstance(String title,String description)
    {


        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TITLE, title);
        args.putSerializable(EXTRA_DESCRIPTION, description);



        SelectProfileDialog dlg=new SelectProfileDialog();
        dlg.setArguments(args);
        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);

        return dlg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appContext = (BiotestApp) getActivity().getApplicationContext();
        modelDataApp = appContext.getModelDataApp();

        listProfile=modelDataApp.getListProfiles();


        title= getArguments().getString(EXTRA_TITLE);
        description= getArguments().getString(EXTRA_DESCRIPTION);





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {



        styling();




        getDialog().setTitle(title);

        View view = inflater.inflate(R.layout.select_profile_dialog, container, false);


         descr=(TextView) view.findViewById(R.id.dlgDescription);
         vList =(ListView) view.findViewById(R.id.listProfiles);

        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,getStringArrayProfilesName());
        vList.setAdapter(adapter);


        vList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
               Profile p= listProfile.get(position);

                if(p!=null)
                {

                   if(!modelDataApp.setActiveProfile(p))
                   {
                       BiotestToast.makeMessageShow(appContext, getString(R.string.error_select_profile), Toast.LENGTH_SHORT, BiotestToast.ERROR_MESSAGE);
                       Log.v(getString(R.string.error_set_profile));
                       actionListener.selected(null);
                       dismiss();
                       return;
                   }
                    p.setActiveProfile(true);
                    actionListener.selected(p);
                }else BiotestToast.makeMessageShow(appContext, getString(R.string.error_select_profile), Toast.LENGTH_SHORT,BiotestToast.ERROR_MESSAGE);

               dismiss();

            }
        });
        Profile activeProfile = modelDataApp.getActiveProfile();
        if(activeProfile!=null)
        {
            descr.setText(description+" "+getString(R.string.dp_act)+activeProfile.getName());

        }
            else     descr.setText(description);


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
        public void selected(Profile profile);
    }

}
