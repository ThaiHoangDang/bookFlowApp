package com.example.cleanconnect.ui.fragment;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.cleanconnect.R;
import com.example.cleanconnect.ui.activity.MainActivity;
import com.example.cleanconnect.viewmodel.SettingsViewModel;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String TAG = "FilterFragment";
    private SettingsViewModel settingsViewModel;
    private MainActivity activity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();

        View view = super.onCreateView(inflater, container, savedInstanceState);
        settingsViewModel = new ViewModelProvider(requireActivity()).get(SettingsViewModel.class);

//        if (view != null) {
//            getListView().setPadding(0, 0, 0, (int) 164);
//        }

        return view;    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.filter_preferences, rootKey);
//        ListPreference themePreference = findPreference(Preferences.THEME);
//        if (themePreference != null) {
//            themePreference.setOnPreferenceChangeListener(
//                    (preference, newValue) -> {
//                        String themeOption = (String) newValue;
//                        ThemeHelper.applyTheme(themeOption);
//                        return true;
//                    });
//        }
    }

    //
//    @Override
//    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        super.onActivityCreated(savedInstanceState);
//        mViewModel = new ViewModelProvider(this).get(FilterViewModel.class);
//        // TODO: Use the ViewModel
//    }

}