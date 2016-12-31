package com.testerhome.nativeandroid.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.views.ShowWebImageActivity;

import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import us.feras.mdv.MarkdownView;

/**
 * Created by vclub on 15/10/25.
 */

public class MarkdownFragment extends BaseFragment {

    @BindView(R.id.markdown_topic_body)
    MarkdownView mTopicBody;

    @SuppressLint({"JavascriptInterface", "AddJavascriptInterface"})
    @Override
    protected void setupView() {
        mTopicBody.addJavascriptInterface(new JavascriptInterface(getActivity()), "imagelistner");

        mTopicBody.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                builder.setToolbarColor(getResources().getColor(R.color.colorPrimary));

                CustomTabsIntent customTabsIntent = builder.build();

                customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
                return true;
            }

            @SuppressLint("SetJavaScriptEnabled")
            @Override
            public void onPageFinished(WebView view, String url) {
                if (mTopicBody != null) {
                    mTopicBody.getSettings().setJavaScriptEnabled(true);
                }
                super.onPageFinished(view, url);
                addImageClickListener();
            }
        });
    }

    // js通信接口
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img) {
            System.out.println(img);
            //
            Intent intent = new Intent();
            intent.putExtra("image", img);
            intent.setClass(context, ShowWebImageActivity.class);
            context.startActivity(intent);
            System.out.println(img);
        }
    }

    // 注入js函数监听
    private void addImageClickListener() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，函数的功能是在图片点击的时候调用本地java接口并传递url过去
        if (mTopicBody == null){
            return;
        }
        mTopicBody.loadUrl("javascript:(function(){" +
                "var objs = document.getElementsByTagName(\"img\"); " +
                "for(var i=0;i<objs.length;i++)  " +
                "{"
                + "    objs[i].onclick=function()  " +
                "    {  "
                + "        window.imagelistner.openImage(this.src);  " +
                "    }  " +
                "}" +
                "})()");
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.fragment_markdown;
    }

    public void showWebContent(String htmlBody) {
        String prompt = "";
        AssetManager assetManager = getActivity().getResources().getAssets();

        try {
            InputStream inputStream;

            if (PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(SettingsFragment.KEY_PREF_THEME, false)) {
                Log.e("theme", "is dark theme");
                inputStream = assetManager.open("dark_template.html");
            } else {
                Log.e("theme", "is light theme");
                inputStream = assetManager.open("h5_template.html");
            }

            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            prompt = new String(b);
            htmlBody = htmlBody.replaceAll("/2/svg/", "/2/72x72/").replace(".svg", ".png");
            prompt = prompt.concat(htmlBody.replace("<img src=\"/uploads/",
                    "<img src=\"https://testerhome.com/uploads/")).concat("</body></html>");
            inputStream.close();
        } catch (IOException e) {
            Log.e("", "Counldn't open updrage-alter.html", e);
        }

        mTopicBody.setBackgroundColor(0);
        mTopicBody.loadDataWithBaseURL(null, prompt, "text/html", "utf-8", null);

    }
}
