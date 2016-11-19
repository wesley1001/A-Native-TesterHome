package com.testerhome.nativeandroid.views;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.google.gson.Gson;
import com.testerhome.nativeandroid.R;
import com.testerhome.nativeandroid.auth.TesterHomeAccountService;
import com.testerhome.nativeandroid.models.OAuth;
import com.testerhome.nativeandroid.networks.RestAdapterUtils;
import com.testerhome.nativeandroid.oauth2.AuthenticationService;
import com.testerhome.nativeandroid.views.base.BackBaseActivity;
import com.testerhome.nativeandroid.views.dialog.DialogUtils;
import com.testerhome.nativeandroid.views.dialog.ProgressDialog;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by vclub on 15/9/18.
 */
public class AuthActivity extends BackBaseActivity {

    private ProgressDialog pd;
    private String auth_code = "";
    private WebView mWebView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_base);
        setCustomTitle("登录");

        FrameLayout layout = (FrameLayout) findViewById(R.id.container);

        mWebView = new WebView(this.getApplicationContext());

        if (layout != null)
            layout.addView(mWebView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(AuthenticationService.AUTHORIZATION_URL)) {
                    try {
                        auth_code = url.substring(url.lastIndexOf("/") + 1);
                        //Generate URL for requesting Access Token
                        // String accessTokenUrl = AuthenticationService.getAccessTokenUrl(auth_code = code);
                        //We make the request in a AsyncTask
                        mWebView.setVisibility(View.INVISIBLE);
                        new PostRequestAsyncTask().execute(AuthenticationService.ACCESS_TOKEN_URL);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (url.equals(AuthenticationService.HTTPS_BASEURL)) {
                    url = AuthenticationService.getAuthorizationUrl();
                }
//                return super.shouldOverrideUrlLoading(view, url);
                view.loadUrl(url);
                return true;
            }

        });

        mWebView.loadUrl(AuthenticationService.getAuthorizationUrl());

    }

    private class PostRequestAsyncTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            pd = DialogUtils.createProgressDialog(AuthActivity.this);
            pd.setMessage("正在登录");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Boolean doInBackground(String... urls) {
            if (urls.length > 0) {

                String url = urls[0];
                OkHttpClient okHttpClient = new OkHttpClient();


                FormBody formBody =  new FormBody.Builder()
                        .add("client_id", AuthenticationService.getAuthorize_client_id())
                        .add("code", auth_code)
                        .add("grant_type", "authorization_code")
                        .add("redirect_uri", AuthenticationService.REDIRECT_URI)
                        .add("client_secret", "3a20127eb087257ad7196098bfd8240746a66b0550d039eb2c1901c025e7cbea")
                        .build();

                Request request = new Request.Builder()
                        .url(url)
                        .post(formBody)
                        .build();

                Response response = null;
                try {
                    response = okHttpClient.newCall(request).execute();
                    if (response.isSuccessful()) {
                        //Convert the string result to a JSON Object
                        String responseStr = response.body().string();
                        Log.e("Tokenm", "doInBackground: " + responseStr );
                        Gson gson = new Gson();
                        OAuth oAuth = gson.fromJson(responseStr,OAuth.class);
                        if (oAuth.getExpires_in() > 0 && oAuth.getAccess_token() != null) {
                            getUserInfo(oAuth);
                            return true;
                        }
                    } else {
                        Log.e("Tokenm", "error else:" + response.message());
                    }
                } catch (Exception e) {
                    Log.e("Tokenm", "error catch:" + request.url().toString(), e);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mWebView.setVisibility(View.VISIBLE);
                            mWebView.loadUrl(AuthenticationService.getAuthorizationUrl());
                        }
                    });
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status) {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        }

    }


    private static final String TAG = "AuthActivity";
    private void getUserInfo(final OAuth oAuth) {

        Log.d(TAG, "getUserInfo() called with: " + "oAuth = [" + oAuth.toString() + "]");

        RestAdapterUtils.getRestAPI(this)
                .getCurrentUserInfo(oAuth.getAccess_token())
                .subscribe(userDetailResponse -> {
                    if (userDetailResponse != null) {
                        TesterHomeAccountService.getInstance(AuthActivity.this)
                                .signIn(userDetailResponse.getUser().getLogin(),
                                        userDetailResponse.getUser(), oAuth);
                        AuthActivity.this.finish();
                    }
                });
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null){
            mWebView.stopLoading();
            mWebView.removeAllViews();
            mWebView.destroy();
        }
        super.onDestroy();
    }
}
