package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.datamodel.Question;
import com.kuzko.aleksey.guessword.exceptions.EmptyCollectionException;

import java.util.ArrayList;
import java.util.List;

public class LearnFragment extends Fragment implements View.OnClickListener{

    private List<Question> askedPhrasesLog = new ArrayList<>();
    private Button answerButton, buttonPreviousWrong, buttonPreviousRight, buttonIDoNotKnow, buttonIKnow;
    private QuestionsRecyclerAdapter questionsRecyclerAdapter;

    public LearnFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onStart(){
        super.onStart();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        answerButton = (Button) getActivity().findViewById(R.id.answerButton);
        buttonPreviousWrong = (Button) getActivity().findViewById(R.id.buttonPreviousWrong);
        buttonPreviousRight = (Button) getActivity().findViewById(R.id.buttonPreviousRight);
        buttonIDoNotKnow = (Button) getActivity().findViewById(R.id.buttonIDoNotKnow);
        buttonIKnow = (Button) getActivity().findViewById(R.id.buttonIKnow);
        answerButton.setOnClickListener(this);
        buttonPreviousWrong.setOnClickListener(this);
        buttonIKnow.setOnClickListener(this);
        buttonPreviousRight.setOnClickListener(this);
        buttonIDoNotKnow.setOnClickListener(this);
        questionsRecyclerAdapter = new QuestionsRecyclerAdapter(askedPhrasesLog, getContext());
        RecyclerView mRecyclerView = (RecyclerView) getActivity().findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(questionsRecyclerAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_learn, container, false);

        return v;
    }

    private void ask(){
        try {
            Question askedQuestion = GuesswordRepository.getInstance().askQuestion();
            askedPhrasesLog.add(askedQuestion);
            questionsRecyclerAdapter.notifyDataSetChanged();
            Log.d("INFO", "Phrase asked: " + askedQuestion.toString());
        } catch (EmptyCollectionException e) {
            Toast.makeText(getContext(), "Phrases collection is empty", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {
        Question question = questionsRecyclerAdapter
                .retrieveArticle(questionsRecyclerAdapter.lastPositinon());
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
