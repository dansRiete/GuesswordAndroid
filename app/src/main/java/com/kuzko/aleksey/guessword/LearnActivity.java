package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.datamodel.Question;
import com.kuzko.aleksey.guessword.exceptions.EmptyCollectionException;
import com.kuzko.aleksey.guessword.utils.QuestionsRecyclerAdapter;

import java.sql.SQLException;

public class LearnActivity extends BaseActivity implements View.OnClickListener {

    private Button answerButton, buttonPreviousWrong, buttonPreviousRight, buttonIDoNotKnow, buttonIKnow;
    private EditText editTextAnswer;
    private QuestionsRecyclerAdapter questionsRecyclerAdapter;
    private GuesswordRepository repository = GuesswordRepository.getInstance();

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
        editTextAnswer = (EditText) findViewById(R.id.editTextAnswer);
        answerButton.setOnClickListener(this);
        buttonPreviousWrong.setOnClickListener(this);
        buttonIKnow.setOnClickListener(this);
        buttonPreviousRight.setOnClickListener(this);
        buttonIDoNotKnow.setOnClickListener(this);
        questionsRecyclerAdapter = new QuestionsRecyclerAdapter(repository.getTodaysQuestions(), this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(questionsRecyclerAdapter);

    }

    private void newQuestion(){
        try {
            repository.askQuestion();
        } catch (EmptyCollectionException e) {
            e.printStackTrace();
            Toast.makeText(this, "Phrases collection is empty", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception during persisting question in DB");
        }
    }

    @Override
    public void onClick(View v) {
        Question currentQuestion = repository.getCurrentQuestion();
        Question previousQuestion = repository.getPreviousQuestion();
        switch (v.getId()){
            case R.id.buttonAnswer:
                currentQuestion.answer(editTextAnswer.getText().toString());
                editTextAnswer.setText("");
                newQuestion();
                break;
            case R.id.buttonIKnow:
                if(currentQuestion != null){
                    currentQuestion.rightAnswer();
                    editTextAnswer.setText("");
                    newQuestion();
                }
                break;
            case R.id.buttonIDoNotKnow:
                if(currentQuestion != null){
                    currentQuestion.wrongAnswer();
                    editTextAnswer.setText("");
                    newQuestion();
                }
                break;
            case R.id.buttonPreviousRight:
                if(currentQuestion != null){
                    previousQuestion.rightAnswer();
                }
                break;
            case R.id.buttonPreviousWrong:
                if(currentQuestion != null){
                    previousQuestion.wrongAnswer();
                }
                break;
            default:
                break;
        }
        questionsRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(repository.getTodaysQuestions().size() == 0 || repository.getCurrentQuestion().isAnswered()){
            newQuestion();
        }
    }
}
