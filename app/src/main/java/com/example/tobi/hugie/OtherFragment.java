package com.example.tobi.hugie;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by tobi on 28.12.16.
 */

public class OtherFragment extends android.app.Fragment{

    private View settingsFragmentView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        settingsFragmentView = inflater.inflate(R.layout.fragment_other, container, false);

        return settingsFragmentView;
    }
}
