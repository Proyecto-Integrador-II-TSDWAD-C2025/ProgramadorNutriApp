package com.example.nutriappmovil.network;

import android.content.Context;

import com.example.nutriappmovil.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static volatile Retrofit retrofit;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            synchronized (ApiClient.class) {
                if (retrofit == null) {
                    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                            .addInterceptor(
                                    new AuthInterceptor(
                                            context.getApplicationContext()
                                    )
                            );

                    if (BuildConfig.DEBUG) {
                        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                        logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
                        clientBuilder.addInterceptor(logging);
                    }

                    retrofit = new Retrofit.Builder()
                            .baseUrl(ApiConfig.BASE_URL)
                            .client(clientBuilder.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                }
            }
        }

        return retrofit;
    }
}
