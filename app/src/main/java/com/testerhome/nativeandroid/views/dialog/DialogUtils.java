package com.testerhome.nativeandroid.views.dialog;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.fragments.SettingsFragment;
import com.testerhome.nativeandroid.views.widgets.ThemeUtils;


public final class DialogUtils {

    private DialogUtils() {}

    public static ProgressDialog createProgressDialog(Context context) {
        return new ProgressDialog(context,  PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(SettingsFragment.KEY_PREF_THEME, false)? R.style.AppDialogDark:R.style.AppDialogLight);
    }

    public static AlertDialog.Builder createAlertDialogBuilder(Context context) {

        return new AlertDialog.Builder(context, PreferenceManager.getDefaultSharedPreferences(context).
                getBoolean(SettingsFragment.KEY_PREF_THEME, false)? R.style.AppDialogDark:R.style.AppDialogLight);

    }

}