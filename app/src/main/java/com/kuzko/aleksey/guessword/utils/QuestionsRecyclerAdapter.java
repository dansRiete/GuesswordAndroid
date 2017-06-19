package com.kuzko.aleksey.guessword.utils;

/**
 * Created by Aleks on 20.03.2017.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuzko.aleksey.guessword.R;
import com.kuzko.aleksey.guessword.datamodel.Phrase;
import com.kuzko.aleksey.guessword.datamodel.Question;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.ViewHolder> {

    private List<Question> questions = new ArrayList<>();
    private Context context;

    public QuestionsRecyclerAdapter(List<Question> dataset, Context context) {
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

    public Question retrieveArticle(int position){
        Question question = null;
        if(questions.isEmpty()){
            return null;
        }
        try {
            question = questions.get(position);
        }catch (IndexOutOfBoundsException e){
            e.printStackTrace();
        }
        return question;
    }

    public int lastPositinon(){
        return questions.size() - 1;
    }

    @Override
    public QuestionsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
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
        String stringRepresent = foreignWord + (currentQuestion.isAnswered() ? (" - " + nativeWord + " " + transcription) : "");

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
            holder.textViewRecViewQuestion.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
            holder.textViewRecViewTime.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
            holder.textViewRecViewRight.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
            holder.textViewRecViewWrong.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
            holder.textViewRecViewSlash.setShadowLayer(5.0f, 2.0f, 2.0f, colorGray);
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
