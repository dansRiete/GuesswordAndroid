package com.kuzko.aleksey.guessword.utils;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kuzko.aleksey.guessword.R;
import com.kuzko.aleksey.guessword.datamodel.Phrase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aleks on 13.06.2017.
 */

public class PhrasesRecyclerAdapter extends RecyclerView.Adapter<PhrasesRecyclerAdapter.ViewHolder> {

    private List<Phrase> phrases = new ArrayList<>();

    public PhrasesRecyclerAdapter(List<Phrase> dataset) {
        phrases = dataset;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewRecyclerItem;
        ImageView imageView;
        ViewHolder(View view) {
            super(view);
            textViewRecyclerItem = (TextView) view.findViewById(R.id.textViewRecViewQuestion);
            imageView = (ImageView) view.findViewById(R.id.image);
        }
    }

    public Phrase retrieveArticle(int position){
        return phrases.get(position);
    }

    @Override
    public PhrasesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_question_item, parent, false);
        return new PhrasesRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhrasesRecyclerAdapter.ViewHolder holder, int position) {
        holder.textViewRecyclerItem.setText(phrases.get(position).toString());
    }

    @Override
    public int getItemCount() {
        return phrases.size();
    }

    public void add(Phrase addedPhrase){
        phrases.add(addedPhrase);
        notifyDataSetChanged();
    }
}
