package com.testerhome.nativeandroid.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by vclub on 15/11/4.
 */
public class URLImageParser implements Html.ImageGetter {

    private Context ctx;
    private View container;

    public URLImageParser(View container, Context context) {
        this.ctx = context;
        this.container = container;
    }

    @Override
    public Drawable getDrawable(String source) {

        URLDrawable urlDrawable = new URLDrawable();

        ImageGetterAsyncTask asyncTask =
                new ImageGetterAsyncTask(urlDrawable);

        asyncTask.execute(source);

        return urlDrawable;
    }

    public class ImageGetterAsyncTask extends AsyncTask<String, Void, Drawable> {
        URLDrawable urlDrawable;

        public ImageGetterAsyncTask(URLDrawable urlDrawable) {
            this.urlDrawable = urlDrawable;
        }

        @Override
        protected Drawable doInBackground(String... params) {
            String source = params[0];
            return fetchDrawable(source);
        }

        @Override
        protected void onPostExecute(Drawable result) {

            if (result == null) return;
            // set the correct bound according to the result from HTTP call
            urlDrawable.setBounds(0, 0, 0 + result.getIntrinsicWidth(),
                    0 + result.getIntrinsicHeight());

            // change the reference of the current drawable to the result
            // from the HTTP call
            urlDrawable.drawable = result;

            // redraw the image by invalidating the container
            URLImageParser.this.container.invalidate();
        }

        public Drawable fetchDrawable(String urlString) {
            try {
                InputStream is = fetch(urlString);
                Drawable drawable = Drawable.createFromStream(is, "src");
                drawable.setBounds(0, 0, 0 + drawable.getIntrinsicWidth(),
                        0 + drawable.getIntrinsicHeight());
                return drawable;
            } catch (Exception e) {
                Log.e("error", "" + e.getMessage() );
                return null;
            }
        }

        private InputStream fetch(String urlString) throws IOException {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(urlString).build();

            Response response = client.newCall(request).execute();

            return response.body().byteStream();
        }
    }
}
