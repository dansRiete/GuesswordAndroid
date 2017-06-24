package com.kuzko.aleksey.guessword.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kuzko.aleksey.guessword.MyApplication;
import com.kuzko.aleksey.guessword.R;
import com.kuzko.aleksey.guessword.data.GuesswordRepository;
import com.kuzko.aleksey.guessword.data.Phrase;
import com.kuzko.aleksey.guessword.data.Question;
import com.kuzko.aleksey.guessword.exceptions.EmptyCollectionException;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LearnActivity extends DrawerActivity implements View.OnClickListener {

    private Button answerButton, buttonPreviousWrong, buttonPreviousRight, buttonIDoNotKnow, buttonIKnow;
    private EditText editTextAnswer;
    private QuestionsAdapter questionsAdapter;
    public static class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

        private List<Question> questions = new ArrayList<>();
        private Context context;

        public QuestionsAdapter(List<Question> dataset, Context context) {
            questions = dataset;
            this.context = context;
        }

        public void add(Question question){
            questions.add(question);
            notifyDataSetChanged();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView textViewRecViewQuestion, textViewRecViewTime, textViewRecViewRight, textViewRecViewWrong, textViewRecViewSlash;
            ImageView imageView;
            ViewHolder(View view) {
                super(view);
                textViewRecViewQuestion = (TextView) view.findViewById(R.id.textViewRecViewQuestion);
                textViewRecViewTime = (TextView) view.findViewById(R.id.textViewRecViewTime);
                textViewRecViewRight = (TextView) view.findViewById(R.id.textViewRecViewRight);
                textViewRecViewWrong = (TextView) view.findViewById(R.id.textViewRecViewWrong);
                textViewRecViewSlash = (TextView) view.findViewById(R.id.textViewRecViewSlash);
                imageView = (ImageView) view.findViewById(R.id.image);
            }
        }

        @Override
        public QuestionsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_questions_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

            Question currentQuestion = questions.get(position);
            Phrase askedPhrase = currentQuestion.getAskedPhrase();

            String foreignWord = askedPhrase.getForeignWord();
            String nativeWord = askedPhrase.getNativeWord();
            String transcription = askedPhrase.getTranscription() != null && !askedPhrase.getTranscription().equals("") ? ("[" + askedPhrase.getTranscription() + "]") : "";
            String stringRepresent = nativeWord + (currentQuestion.isAnswered() ? (" - " + foreignWord + " " + transcription) : "");

            int colorGreen = ContextCompat.getColor(context, R.color.colorGreen);
            int colorRed = ContextCompat.getColor(context, R.color.colorRed);
            int colorGray = ContextCompat.getColor(context, R.color.colorGray);

            holder.textViewRecViewQuestion.setText(stringRepresent);
            holder.textViewRecViewTime.setText(new SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                    .format(currentQuestion.getAskDate())
            );

            if(currentQuestion.isAnswered()){
                if(currentQuestion.isAnswerCorrect()){
                    holder.textViewRecViewRight.setTextColor(colorGreen);
                    holder.textViewRecViewWrong.setTextColor(colorGray);
                }else {
                    holder.textViewRecViewWrong.setTextColor(colorRed);
                    holder.textViewRecViewRight.setTextColor(colorGray);
                }
            }else {
                holder.textViewRecViewRight.setTextColor(colorGray);
                holder.textViewRecViewWrong.setTextColor(colorGray);
            }

            if(position == 0){
                holder.textViewRecViewQuestion.setTextSize(20);
                holder.textViewRecViewQuestion.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
                holder.textViewRecViewTime.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
                holder.textViewRecViewRight.setShadowLayer(5.0f, 1.0f, 1.0f, colorGray);
                holder.textViewRecViewWrong.setShadowLayer(5.0f, 1.0f, 1.0f, colorGray);
                holder.textViewRecViewSlash.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
            }
        }

        @Override
        public int getItemCount() {
            return questions.size();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        GuesswordRepository.init((MyApplication) getApplication());
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
        questionsAdapter = new QuestionsAdapter(GuesswordRepository.getInstance().getTodaysQuestions(), this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(questionsAdapter);

    }

    private void newQuestion(){
        try {
            GuesswordRepository.getInstance().askQuestion();
        } catch (EmptyCollectionException e) {
            Log.w(getLocalClassName(), "Phrases collection is empty");
            Toast.makeText(this, "Phrases collection is empty", Toast.LENGTH_LONG).show();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Exception during persisting question in DB");
        }
    }

    @Override
    public void onClick(View v) {
        Question currentQuestion = GuesswordRepository.getInstance().getCurrentQuestion();
        Question previousQuestion = GuesswordRepository.getInstance().getPreviousQuestion();
        switch (v.getId()){
            case R.id.buttonAnswer:
                if(currentQuestion != null) {
                    currentQuestion.answer(editTextAnswer.getText().toString());
                    editTextAnswer.setText("");
                    newQuestion();
                    break;
                }
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
                if(previousQuestion != null){
                    previousQuestion.rightAnswer();
                }
                break;
            case R.id.buttonPreviousWrong:
                if(previousQuestion != null){
                    previousQuestion.wrongAnswer();
                }
                break;
            default:
                break;
        }
        questionsAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(GuesswordRepository.getInstance().getTodaysQuestions().size() == 0 ||
                GuesswordRepository.getInstance().getCurrentQuestion().isAnswered()){
            newQuestion();
            questionsAdapter.notifyDataSetChanged();
        }
    }
}
