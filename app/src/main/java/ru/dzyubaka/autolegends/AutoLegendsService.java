package ru.dzyubaka.autolegends;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

interface AutoLegendsService {

    @POST("/login")
    @FormUrlEncoded
    Call<User> login(
            @Field("login") String login,
            @Field("password") String password
    );

    @POST("/register")
    @FormUrlEncoded
    Call<User> register(
            @Field("login") String login,
            @Field("name") String name,
            @Field("password") String password
    );

    @POST("/levelUp")
    @FormUrlEncoded
    Call<User> levelUp(@Field("login") String login);

    AutoLegendsService instance = new Retrofit.Builder()
            .baseUrl("http://dzyuba.duckdns.org")
            .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                    .registerTypeAdapter(new TypeToken<List<UnitType>>() {
                    }.getType(), new UnitTypeDeserializer())
                    .create()))
            .build()
            .create(AutoLegendsService.class);
}
