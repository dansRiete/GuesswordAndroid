package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.datamodel.Question;
import com.kuzko.aleksey.guessword.exceptions.EmptyCollectionException;

import java.util.ArrayList;

public class LearnActivity extends BaseActivity implements View.OnClickListener {

    private ArrayList<Question> askedPhrasesLog;
    private Button answerButton, buttonPreviousWrong, buttonPreviousRight, buttonIDoNotKnow, buttonIKnow;
    private QuestionsRecyclerAdapter questionsRecyclerAdapter;
    private final static String ASKED_PHRASES_LOG = "ASKED_PHRASES_LOG";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(ASKED_PHRASES_LOG, askedPhrasesLog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getLocalClassName(), "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        answerButton = (Button) findViewById(R.id.answerButton);
        buttonPreviousWrong = (Button) findViewById(R.id.buttonPreviousWrong);
        buttonPreviousRight = (Button) findViewById(R.id.buttonPreviousRight);
        buttonIDoNotKnow = (Button) findViewById(R.id.buttonIDoNotKnow);
        buttonIKnow = (Button) findViewById(R.id.buttonIKnow);
        answerButton.setOnClickListener(this);
        buttonPreviousWrong.setOnClickListener(this);
        buttonIKnow.setOnClickListener(this);
        buttonPreviousRight.setOnClickListener(this);
        buttonIDoNotKnow.setOnClickListener(this);
        if(savedInstanceState != null){
            askedPhrasesLog = (ArrayList) savedInstanceState.getSerializable(ASKED_PHRASES_LOG);
        }else {
            askedPhrasesLog = new ArrayList<>();
        }
        questionsRecyclerAdapter = new QuestionsRecyclerAdapter(askedPhrasesLog, this);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(questionsRecyclerAdapter);
    }

    private void ask(){
        try {
            Question askedQuestion = GuesswordRepository.getInstance().askQuestion();
            askedPhrasesLog.add(0, askedQuestion);
            questionsRecyclerAdapter.notifyDataSetChanged();
            Log.d("INFO", "Phrase asked: " + askedQuestion.toString());
        } catch (EmptyCollectionException e) {
            Toast.makeText(this, "Phrases collection is empty", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onClick(View v) {
        Question question = questionsRecyclerAdapter.retrieveArticle(0);
        switch (v.getId()){
            case R.id.answerButton:
                ask();
                break;
            case R.id.buttonIKnow:
                if(question != null){
                    question.rightAnswer();
                    ask();
                }
                break;
            case R.id.buttonIDoNotKnow:
                if(question != null){
                    question.wrongAnswer();
                    ask();
                }
                break;
            default:
                break;
        }
    }
}
