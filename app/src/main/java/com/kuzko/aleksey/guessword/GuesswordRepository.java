package com.kuzko.aleksey.guessword;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Aleks on 04.05.2017.
 */

public class GuesswordRepository {
    private static GuesswordRepository instance;

    public GuesswordService getGuesswordService() {
        return guesswordService;
    }

    private GuesswordService guesswordService;
    private GuesswordRepository(){
        Retrofit guessword = new Retrofit.Builder().baseUrl(GuesswordService.BASE_GUESSWORD_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        guesswordService = guessword.create(GuesswordService.class);
    }
    public static GuesswordRepository getInstance(){
        if(instance == null){
            instance = new GuesswordRepository();
        }
        return instance;
    }
}
