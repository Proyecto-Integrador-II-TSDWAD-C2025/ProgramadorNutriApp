package com.example.nutriappmovil.network;

import android.content.Context;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofit;

    public static Retrofit getClient(Context context) {

        if (retrofit == null) {

            HttpLoggingInterceptor logging =
                    new HttpLoggingInterceptor();

            logging.setLevel(
                    HttpLoggingInterceptor.Level.BASIC
            );

            OkHttpClient client =
                    new OkHttpClient.Builder()
                            .addInterceptor(
                                    new AuthInterceptor(
                                            context.getApplicationContext()
                                    )
                            )
                            .addInterceptor(logging)
                            .build();

            retrofit =
                    new Retrofit.Builder()
                            .baseUrl(ApiConfig.BASE_URL)
                            .client(client)
                            .addConverterFactory(
                                    GsonConverterFactory.create()
                            )
                            .build();
        }

        return retrofit;
    }
}