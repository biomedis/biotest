package ru.biomedis.biotest.fragments.dialogs;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import ru.biomedis.biotest.R;

/**
 * Показывает html переданый в text EXTRA_HTML_TEXT
 * Заголовки вставляются автоматически
 * Created by Anama on 25.10.2014.
 */
public class TextViewDialog extends DialogStyling
{


    private WebView contentView;
    private static String EXTRA_TITLE="ru.biomedis.yesno.title";
    private static String EXTRA_HTML_TEXT="EXTRA_HTML_TEXT";


    private String title;
    private String text;










    public static TextViewDialog newInstance(String title,String txt)
    {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_TITLE, title);
        args.putSerializable(EXTRA_HTML_TEXT, txt);

        TextViewDialog dlg=new TextViewDialog();
        dlg.setArguments(args);
        dlg.setStyle(DialogFragment.STYLE_NORMAL,R.style.BiotestDialog);
        return dlg;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);


        title= getArguments().getString(EXTRA_TITLE);
        text= getArguments().getString(EXTRA_HTML_TEXT);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {

        styling();
        getDialog().setTitle(title);
        getDialog().setCanceledOnTouchOutside(true);

        View view = inflater.inflate(R.layout.text_view_dialog, container, true);

        contentView=(WebView)view.findViewById(R.id.webview);

        String htmltext="<!DOCTYPE html><html><head>\n" +
                "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\">\n" +
                "<meta charset=\"utf-8\">\n</head><body>";
        htmltext+=text;
        htmltext+="</body></html>";

        contentView.loadDataWithBaseURL(null, htmltext, "text/html", "en_US", null);
        WebSettings settings = contentView.getSettings();
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);




        return view;

    }

/*
    @Override
    public void onResume()
    {
        super.onResume();

        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.MATCH_PARENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
    */
@Override
public void onStart() {
    super.onStart();

    // safety check
    if (getDialog() == null) {
        return;
    }

    int dialogWidth = ViewGroup.LayoutParams.MATCH_PARENT; // specify a value here
    int dialogHeight = ViewGroup.LayoutParams.MATCH_PARENT;// specify a value here

    getDialog().getWindow().setLayout(dialogWidth, dialogHeight);


}
}
