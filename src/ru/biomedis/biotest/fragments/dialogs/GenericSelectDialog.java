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
import java.util.Collections;
import java.util.List;

/**
 * Created by Anama on 25.10.2014.
 */
public class GenericSelectDialog extends DialogStyling
{


    private TextView descr;
    private  ActionListener actionListener=null;
    private static String EXTRA_TITLE="ru.biomedis.selectProfileDlg.title";
    private static String EXTRA_DESCRIPTION="ru.biomedis.selectProfileDlg.desc";

    private String title;
    private String description;
    private List<String> listStrings =new ArrayList<>();
    private ListView vList;
    private ArrayAdapter<String> adapter;

    BiotestApp appContext=null;
    private ModelDataApp modelDataApp=null;






    public static GenericSelectDialog newInstance(String title,String description)
    {


        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TITLE, title);
        args.putSerializable(EXTRA_DESCRIPTION, description);





        GenericSelectDialog dlg=new GenericSelectDialog();
        dlg.setArguments(args);
        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);

        return dlg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        title= getArguments().getString(EXTRA_TITLE);
        description= getArguments().getString(EXTRA_DESCRIPTION);






    }


    public List<String> getListStrings()
    {
        return listStrings;
    }

    public void setListStrings(List<String> listStrings)
    {

        this.listStrings.clear();
        this.listStrings.addAll(listStrings);

        if(adapter!=null)adapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {



        styling();




        getDialog().setTitle(title);

        View view = inflater.inflate(R.layout.generic_select_dialog, container, false);


         descr=(TextView) view.findViewById(R.id.dlgDescription);
         vList =(ListView) view.findViewById(R.id.listItems);

        adapter=new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,listStrings);
        vList.setAdapter(adapter);


        vList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
               String p= listStrings.get(position);
                actionListener.selected(p);
               dismiss();

            }
        });

          descr.setText(description);


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
        public void selected(String item);
    }

}
