package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.kuzko.aleksey.guessword.datamodel.Phrase;

import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends AppCompatActivity {
    private static final String BASE_GUESSWORD_URL = "http://192.168.1.115:8080/guessword-1/";
    private TextView textView;
    GuesswordService guesswordService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textView);

        Retrofit guessword = new Retrofit.Builder().baseUrl(BASE_GUESSWORD_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();

        guesswordService = guessword.create(GuesswordService.class);

        guesswordService.getPhrases(2)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<Phrase>>() {
                    @Override
                    public void call(List<Phrase> phrases) {
                        String result = "";
                        for(Phrase phrase : phrases){
                            result += phrase.represent() + "\n";
                        }
                        textView.setText(result);
                    }

                });
    }
}
