package com.testerhome.nativeandroid.networks;

import android.content.Context;
import android.util.Log;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.testerhome.nativeandroid.Config;
import com.testerhome.nativeandroid.utils.NetworkUtils;

import java.io.IOException;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by vclub on 15/10/24.
 */
public class RestAdapterUtils {

    /**
     * support offline cache
     *
     * @return
     */
    public static TopicsService getRestAPI(Context ctx) {

        final boolean isOnline = NetworkUtils.isNetworkAvailable(ctx);

        OkHttpClient client = null;

        try {
            int cacheSize = 10 * 1024 * 1024;
            Cache cache = new Cache(ctx.getCacheDir(), cacheSize);

            client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new Interceptor() {
                        @Override
                        public Response intercept(Chain chain) throws IOException {
                            String cacheStr;
                            if (isOnline) {
                                cacheStr = "public, max-age=60";
                            } else {
                                cacheStr = "public, only-if-cached, max-stale=" + (60 * 60 * 24 * 28);
                            }
                            Response response = chain.proceed(chain.request());
                            return response.newBuilder()
                                    .header("Cache-Control", cacheStr)
                                    .build();
                        }
                    })
                    .addNetworkInterceptor(new StethoInterceptor())
                    .cache(cache)
                    .build();
        } catch (Exception ex) {
            Log.e("cache error", "can not create cache!");
        }

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .client(client)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(TopicsService.class);
    }
}
