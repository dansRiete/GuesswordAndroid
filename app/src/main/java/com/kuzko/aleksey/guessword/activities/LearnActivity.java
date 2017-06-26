package com.kuzko.aleksey.guessword.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LearnActivity extends DrawerActivity {

    private EditText editTextAnswer;
    private QuestionsAdapter questionsAdapter;

    private static class QuestionsAdapter extends RecyclerView.Adapter<QuestionsAdapter.ViewHolder> {

        private List<Question> questions = new ArrayList<>();
        private Context context;

        private QuestionsAdapter(List<Question> dataset, Context context) {
            questions = dataset;
            this.context = context;
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

            String transcription = askedPhrase.getTranscription() != null && !askedPhrase.getTranscription().equals("") ?
                                   ("[" + askedPhrase.getTranscription() + "]") : "";

            String stringRepresent = nativeWord + (currentQuestion.isAnswered() ?
                                                   (" - " + foreignWord + " " + transcription) : "");

            int colorGreen = ContextCompat.getColor(context, R.color.colorGreen);
            int colorRed = ContextCompat.getColor(context, R.color.colorRed);
            int colorGray = ContextCompat.getColor(context, R.color.colorGray);

            holder.textViewRecViewQuestion.setText(stringRepresent);
            holder.textViewRecViewTime.setText(
                    new SimpleDateFormat(context.getString(R.string.questionAdapter_timeFormat),
                            Locale.getDefault()).format(currentQuestion.getAskDate())
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
                holder.textViewRecViewQuestion.setTextSize(context.getResources().getInteger(R.integer.questionAdapter_currentQuestion_fontSize));
//                holder.textViewRecViewQuestion.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
//                holder.textViewRecViewTime.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
//                holder.textViewRecViewRight.setShadowLayer(5.0f, 1.0f, 1.0f, colorGray);
//                holder.textViewRecViewWrong.setShadowLayer(5.0f, 1.0f, 1.0f, colorGray);
//                holder.textViewRecViewSlash.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
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
        editTextAnswer = (EditText) findViewById(R.id.editTextAnswer);

        //TODO show progress bar
        new AsyncTask<Void, Void, List<Question>>(){
            @Override
            protected List<Question> doInBackground(Void... params) {
                GuesswordRepository.init((MyApplication) getApplication());
                return GuesswordRepository.getInstance().getTodaysQuestions();
            }

            @Override
            protected void onPostExecute(List<Question> todayAskedQuestions) {
                super.onPostExecute(todayAskedQuestions);
                initRecyclerAdapter(todayAskedQuestions);
                //TODO hide progress bar
            }
        }.execute();
    }

    private void initRecyclerAdapter(List<Question> todayAskedQuestions){
        questionsAdapter = new QuestionsAdapter(todayAskedQuestions, this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(questionsAdapter);
        if(questionsListIsEmpty() || lastQuestionIsAnswered()){
            newQuestion();
            questionsAdapter.notifyDataSetChanged();
        }
    }

    private void newQuestion(){
        try {
            GuesswordRepository.getInstance().askQuestion();
        } catch (EmptyCollectionException e) {
            Log.w(getClass().getSimpleName(), "Phrases collection is empty");
            Toast.makeText(this, "Phrases collection is empty", Toast.LENGTH_LONG).show();
        }
    }

    public void answerButtonClick(View view) {
        Question currentQuestion = GuesswordRepository.getInstance().getCurrentQuestion();
        if(currentQuestion != null) {
            currentQuestion.answer(editTextAnswer.getText().toString());
            editTextAnswer.setText("");
            newQuestion();
        }
        questionsAdapter.notifyDataSetChanged();
    }

    public void previousWrongButtonClick(View view) {
        Question previousQuestion = GuesswordRepository.getInstance().getPreviousQuestion();
        if(previousQuestion != null){
            previousQuestion.wrongAnswer();
        }
        questionsAdapter.notifyDataSetChanged();
    }

    public void previousRightButtonClick(View view) {
        Question previousQuestion = GuesswordRepository.getInstance().getPreviousQuestion();
        if(previousQuestion != null){
            previousQuestion.rightAnswer();
        }
        questionsAdapter.notifyDataSetChanged();
    }

    public void wrongButtonClick(View view) {
        Question currentQuestion = GuesswordRepository.getInstance().getCurrentQuestion();
        if(currentQuestion != null){
            currentQuestion.wrongAnswer();
            editTextAnswer.setText("");
            newQuestion();
        }
        questionsAdapter.notifyDataSetChanged();
    }

    public void rightButtonClick(View view) {
        Question currentQuestion = GuesswordRepository.getInstance().getCurrentQuestion();
        if(currentQuestion != null){
            currentQuestion.rightAnswer();
            editTextAnswer.setText("");
            newQuestion();
        }
        questionsAdapter.notifyDataSetChanged();
    }

    private boolean lastQuestionIsAnswered(){
        return GuesswordRepository.getInstance().getCurrentQuestion().isAnswered();
    }

    private boolean questionsListIsEmpty(){
        return GuesswordRepository.getInstance().getTodaysQuestions().size() == 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(questionsAdapter != null && (questionsListIsEmpty() || lastQuestionIsAnswered())){
            newQuestion();
            questionsAdapter.notifyDataSetChanged();
        }
    }
}
