package com.testerhome.nativeandroid.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.views.widgets.ThemeUtils;

/**
 * Created by vclub on 15/10/23.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {


    public static final String KEY_PREF_THEME = "pref_theme";
    public static final String KEY_PREF_COMMENT_WITH_SNACK = "pref_comment_with_snack_key";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(SettingsFragment.KEY_PREF_THEME, false)) {
            view.setBackgroundColor(getResources().getColor(R.color.selectable_item_background_general_dark_normal));
        } else {

            view.setBackgroundColor(getResources().getColor(R.color.white));
        }


        return view;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pref_general);

//        findPreference(KEY_PREF_THEME).setSummary(getKeyPrefThemeString(KEY_PREF_THEME));

    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(KEY_PREF_THEME)) {
            Preference themePref = findPreference(key);
            // Set summary to be the user-description for the selected value
            if (getKeyPrefTheme(key)){
                getActivity().setTheme(R.style.theme_dark);
            } else {
                getActivity().setTheme(R.style.theme_light);
            }
            ThemeUtils.recreateActivity(getActivity());
        }
    }

    private Boolean getKeyPrefTheme(String key) {
        try {
            return PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(key, false);
        } catch (Exception ex) {
            return false;
        }
    }

}
