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

import java.sql.SQLException;

public class LearnActivity extends BaseActivity implements View.OnClickListener {

//    private ArrayList<Question> askedPhrasesLog;
    private Button answerButton, buttonPreviousWrong, buttonPreviousRight, buttonIDoNotKnow, buttonIKnow;
    private QuestionsRecyclerAdapter questionsRecyclerAdapter;
    private GuesswordRepository repository = GuesswordRepository.getInstance();
    private final static String ASKED_PHRASES_LOG = "ASKED_PHRASES_LOG";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putSerializable(ASKED_PHRASES_LOG, askedPhrasesLog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getLocalClassName(), "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        answerButton = (Button) findViewById(R.id.buttonAnswer);
        buttonPreviousWrong = (Button) findViewById(R.id.buttonPreviousWrong);
        buttonPreviousRight = (Button) findViewById(R.id.buttonPreviousRight);
        buttonIDoNotKnow = (Button) findViewById(R.id.buttonIDoNotKnow);
        buttonIKnow = (Button) findViewById(R.id.buttonIKnow);
        answerButton.setOnClickListener(this);
        buttonPreviousWrong.setOnClickListener(this);
        buttonIKnow.setOnClickListener(this);
        buttonPreviousRight.setOnClickListener(this);
        buttonIDoNotKnow.setOnClickListener(this);

        /*if(savedInstanceState != null){
            Object obj = savedInstanceState.getSerializable(ASKED_PHRASES_LOG);
            if(obj != null && obj instanceof ArrayList){
                //noinspection unchecked
                askedPhrasesLog = (ArrayList) obj;
            }else {
                askedPhrasesLog = new ArrayList<>();
            }
        }else {
            askedPhrasesLog = new ArrayList<>();
        }*/

        questionsRecyclerAdapter = new QuestionsRecyclerAdapter(repository.getTodaysQuestions(), this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(questionsRecyclerAdapter);
        if(repository.getTodaysQuestions().size() == 0){
            ask();
        }
    }

    private void ask(){
        try {
            Question askedQuestion = repository.askQuestion();
//            askedPhrasesLog.add(0, askedQuestion);
            questionsRecyclerAdapter.notifyDataSetChanged();
            Log.d("INFO", "Phrase asked: " + askedQuestion.toString());
        } catch (EmptyCollectionException e) {
            e.printStackTrace();
            Toast.makeText(this, "Phrases collection is empty", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(this, "Something went wrong during persisting question in DB", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onClick(View v) {
        Question question = repository.getCurrentQuestion();
        switch (v.getId()){
            case R.id.buttonAnswer:
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
