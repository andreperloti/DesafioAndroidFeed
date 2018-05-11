package br.com.developers.perloti.desafioandroidtechfit.controller.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by perloti on 08/05/18.
 */

public interface ClienteAPI {

    @GET("feed")
    Call<LinkedTreeMap> getFeed();

    @GET("feed")
    Call<LinkedTreeMap> getFeed(@Query("p") String p, @Query("t") String t);

    @GET("profile/{id}")
    Call<LinkedTreeMap> getProfile(@Path("id") String id);

    @GET("profile/{id}")
    Call<LinkedTreeMap> getProfile(@Path("id") String id, @Query("p") String p, @Query("t") String t);

    @GET("feed/{feedHash}")
    Call<LinkedTreeMap> getDetailPost(@Path("feedHash") String id);

    class MyRetrofit {
        private static String BASE_URL = "http://api.tecnonutri.com.br/api/v4/";

        public static ClienteAPI getInstance() {
            Gson gson = new GsonBuilder()
                    .setLenient()
                    .create();

            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS);
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(httpClient.build())
                    .build();

            return retrofit.create(ClienteAPI.class);
        }
    }
}
