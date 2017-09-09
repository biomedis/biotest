package ru.biomedis.biotest.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import ru.biomedis.biotest.R;

/**
 * Created by Anama on 30.12.2014.
 */
public class ManualContent extends Fragment
{
    private WebView webview;

public static String EXSTRA_START_URL="EXSTRA_START_URL";
    private String startUrl;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        startUrl= bundle.getString(EXSTRA_START_URL,"");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {


        webview =(WebView)inflater.inflate(R.layout.manual_content_fragment,container,false);

        loadPage(startUrl);

        WebSettings settings = webview.getSettings();
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptEnabled(true);
        settings.setDefaultTextEncodingName("utf-8");
        settings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);

        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);



        webview.setWebViewClient(new LocalWebViewClient());


        return webview;
    }


public void loadPage(String url)
{

    webview.loadUrl(url);
}

    /**
     * Рабоатает с событиями на странице
     */
    private class LocalWebViewClient extends WebViewClient
    {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            //позволит позвонить если ссылка типа tel:222-222-222
            if (url.indexOf("tel:") > -1) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(url)));
            } else {
                view.loadUrl(url);
            }
            return true;
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onStart()
    {
        super.onStart();
    }
}
