package com.kuzko.aleksey.guessword;

/**
 * Created by Aleks on 20.03.2017.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuzko.aleksey.guessword.datamodel.Question;

import java.util.List;


class QuestionsRecyclerAdapter extends RecyclerView.Adapter<QuestionsRecyclerAdapter.ViewHolder> {

    private List<Question> questions;

    QuestionsRecyclerAdapter(List<Question> dataset) {
        questions = dataset;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecyclerItem;
        ImageView imageView;
        ViewHolder(View view) {
            super(view);
            textViewRecyclerItem = (TextView) view.findViewById(R.id.textViewRecyclerItem);
            imageView = (ImageView) view.findViewById(R.id.image);
        }
    }

    public Question retrieveArticle(int position){
        return questions.get(position);
    }

    @Override
    public QuestionsRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textViewRecyclerItem.setText(questions.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }
}
