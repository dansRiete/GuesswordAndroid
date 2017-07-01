package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.activities.DrawerActivity;
import com.kuzko.aleksey.guessword.data.Phrase;
import com.kuzko.aleksey.guessword.utils.WebguesswordRepository;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SettingsActivity extends DrawerActivity {

    private final static String BASE_NEWS_URL = "http://guessword-aleks267.rhcloud.com/rest/";
    private Button buttonStartSync;
    private WebguesswordRepository webguesswordRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Retrofit webGuessWord = new Retrofit.Builder().baseUrl(BASE_NEWS_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
        webguesswordRepository = webGuessWord.create(WebguesswordRepository.class);
    }

    public void startSync(View view) {
        webguesswordRepository.retrievePhrases()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Phrase>>() {
                    @Override
                    public void call(List<Phrase> phrases) {
                        Toast.makeText(SettingsActivity.this, "Size = " + phrases.size(), Toast.LENGTH_SHORT).show();
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        throwable.printStackTrace();
                        Toast.makeText(SettingsActivity.this, "Error " + throwable.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
