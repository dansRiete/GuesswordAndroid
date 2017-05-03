package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.datamodel.GuesswordRepository;
import com.kuzko.aleksey.guessword.datamodel.Phrase;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LearnActivity extends AppCompatActivity {
    private long userLoginId;
    private TextView textViewTrainingLog;
    private List<Phrase> phrases;
    private GuesswordService guesswordService;
    private Button nextButton;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        userLoginId = retrieveUserLogin(savedInstanceState);
        textViewTrainingLog = (TextView) findViewById(R.id.textViewTrainingLog);
        guesswordService = GuesswordRepository.getInstance().getGuesswordService();
        nextButton = (Button) findViewById(R.id.nextButton);
        nextButton.setClickable(false);
        guesswordService.fetchAllPhrases(userLoginId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                response -> {
                                    Log.d("INFO", response.raw().request().url().toString());
                                    phrases = response.body();
                                    if(phrases != null){
                                        nextButton.setClickable(true);
                                    }
                                },
                                throwable ->{
                                    nextButton.setClickable(false);
                                    Toast.makeText(LearnActivity.this, "LearnActivity " + throwable.getLocalizedMessage(), Toast.LENGTH_LONG).show();
                                }
                        );
    }

    private long retrieveUserLogin(Bundle savedInstanceState){
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                return 0;
            } else {
                return extras.getLong(LoginActivity.USERNAME_KEY);
            }
        } else {
            return savedInstanceState.getLong(LoginActivity.USERNAME_KEY);
        }
    }

    public void nextButtonClicked(View view) {

        if(counter < phrases.size()){
            String setText = phrases.get(counter++).toString();
            Log.d("INFO", "Set text " + setText);
            textViewTrainingLog.setText(setText);
        }
    }
}
