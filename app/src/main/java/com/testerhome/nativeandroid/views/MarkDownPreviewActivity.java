package com.testerhome.nativeandroid.views;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.FrameLayout;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.fragments.SettingsFragment;
import com.testerhome.nativeandroid.utils.StringUtils;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;
import com.testerhome.nativeandroid.views.base.BaseActivity;

import java.io.IOException;
import java.io.InputStream;

import us.feras.mdv.MarkdownView;

/**
 * Created by cvter on 12/3/16.
 */
public class MarkDownPreviewActivity extends BackBaseActivity {

    private static final String EXTRA_MARKDOWN = "markdown";
    private MarkdownView markdownView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_base);
        setCustomTitle("MarkDown预览");
        FrameLayout layout = (FrameLayout) findViewById(R.id.container);

        markdownView = new MarkdownView(this);
        layout.addView(markdownView);
//        markdownView.loadMarkdown();
        showWebContent(StringUtils.renderMarkdown(getIntent().getStringExtra(EXTRA_MARKDOWN)));
    }





    public static void open(Context context, String markdown) {
        Intent intent = new Intent(context, MarkDownPreviewActivity.class);
        intent.putExtra(EXTRA_MARKDOWN, markdown);
        context.startActivity(intent);
    }


    public void showWebContent(String htmlBody) {
        String prompt = "";
        AssetManager assetManager = getResources().getAssets();

        try {
            InputStream inputStream;

            if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SettingsFragment.KEY_PREF_THEME, false)) {
                Log.e("theme", "is dark theme");
                inputStream = assetManager.open("dark_template.html");
            } else {
                Log.e("theme", "is light theme");
                inputStream = assetManager.open("h5_template.html");
            }

            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            prompt = new String(b);
            prompt = prompt.concat(htmlBody.replace("<img src=\"/photo/",
                    "<img src=\"https://testerhome.com/photo/")).concat("</body></html>");
            inputStream.close();
        } catch (IOException e) {
            Log.e("", "Counldn't open updrage-alter.html", e);
        }

        markdownView.setBackgroundColor(0);
        markdownView.loadDataWithBaseURL(null, prompt, "text/html", "utf-8", null);

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        markdownView.removeAllViews();
        markdownView.destroy();
    }
}
