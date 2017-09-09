package ru.biomedis.biotest.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.biomedis.biotest.R;

/**
 * Created by Anama on 14.04.2015.
 */
public class MessageFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.message_fragment, container, false);
    }
}