package ru.biomedis.biotest.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.biomedis.biotest.R;
import ru.biomedis.biotest.fragments.measureResults.BaseResultFragment;

/**
 * Created by Anama on 11.02.2015.
 */
public class DividerFragment extends Fragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.divider_fragment, container, false);
        return view;
    }
}
