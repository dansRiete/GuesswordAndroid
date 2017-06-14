package com.kuzko.aleksey.guessword;

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

import com.kuzko.aleksey.guessword.datamodel.Question;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.ViewHolder> {

    private List<Question> questions;
    private Context context;

    QuestionsRecyclerAdapter(List<Question> dataset, Context context) {
        questions = dataset;
        this.context = context;
    }

    public void add(Question question){
        questions.add(question);
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecViewQuestion, textViewRecViewTime, textViewRecViewRight, textViewRecViewWrong;
        ImageView imageView;
        ViewHolder(View view) {
            super(view);
            textViewRecViewQuestion = (TextView) view.findViewById(R.id.textViewRecViewQuestion);
            textViewRecViewTime = (TextView) view.findViewById(R.id.textViewRecViewTime);
            textViewRecViewRight = (TextView) view.findViewById(R.id.textViewRecViewRight);
            textViewRecViewWrong = (TextView) view.findViewById(R.id.textViewRecViewWrong);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_question_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Question currentQuestion = questions.get(position);
        int colorGreen = ContextCompat.getColor(context, R.color.colorGreen);
        int colorRed = ContextCompat.getColor(context, R.color.colorRed);
        int colorGray = ContextCompat.getColor(context, R.color.colorGray);
        holder.textViewRecViewQuestion.setText(currentQuestion.toString());
        holder.textViewRecViewTime.setText(
                new SimpleDateFormat("hh:mm:ss", Locale.ENGLISH)
                        .format(currentQuestion.getAskDate())
        );
        holder.textViewRecViewRight.setTextColor(colorRed);
        holder.textViewRecViewRight.setTextColor(currentQuestion.isAnswered() ?
                (currentQuestion.isAnswerCorrect() ? colorGreen : colorGray) : colorGray
        );
        holder.textViewRecViewWrong.setTextColor(currentQuestion.isAnswered() ?
                (currentQuestion.isAnswerCorrect() ? colorGray : colorRed) : colorGray
        );
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
