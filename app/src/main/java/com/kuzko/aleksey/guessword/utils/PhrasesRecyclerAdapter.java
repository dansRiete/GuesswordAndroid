package com.kuzko.aleksey.guessword.utils;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kuzko.aleksey.guessword.R;
import com.kuzko.aleksey.guessword.datamodel.Phrase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Aleks on 13.06.2017.
 */

public class PhrasesRecyclerAdapter extends RecyclerView.Adapter<PhrasesRecyclerAdapter.ViewHolder> {

    private List<Phrase> phrases = new ArrayList<>();
    private Context context;
    private final static String CREATION_PHRASE_DATE_TEMPLATE = "dd/MM/y";
    private final static String LAST_ACCESS_DATE_TEMPLATE = "dd/MM/y HH:mm";
    private final static String MULTIPLIER_AND_PROB_FORMAT = "%1$,.2f";

    public PhrasesRecyclerAdapter(List<Phrase> dataset, Context context) {
        this.phrases = dataset;
        this.context = context;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView    textViewPhraseAdapterLabel,
                    textViewPhraseAdapterCreatedDate,
                    textViewPhraseAdapterAccessDate,
                    textViewPhraseAdapterMultiplier,
                    textViewRecViewQuestion,
                    textViewPhraseAdapterProb;

        ViewHolder(View view) {
            super(view);
            textViewRecViewQuestion = (TextView) view.findViewById(R.id.textViewRecViewQuestion);
            textViewPhraseAdapterLabel = (TextView) view.findViewById(R.id.textViewPhraseAdapterLabel);
            textViewPhraseAdapterCreatedDate = (TextView) view.findViewById(R.id.textViewPhraseAdapterCreatedDate);
            textViewPhraseAdapterProb = (TextView) view.findViewById(R.id.textViewPhraseAdapterProb);
            textViewPhraseAdapterMultiplier = (TextView) view.findViewById(R.id.textViewPhraseAdapterMultiplier);
            textViewPhraseAdapterAccessDate = (TextView) view.findViewById(R.id.textViewPhraseAdapterAccessDate);
        }
    }

    public Phrase retrieveArticle(int position){
        return phrases.get(position);
    }

    @Override
    public PhrasesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_phrases_edit_item, parent, false);
        return new PhrasesRecyclerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PhrasesRecyclerAdapter.ViewHolder holder, int position) {
        Phrase currentPhrase = phrases.get(position);

        String label = currentPhrase.getLabel() == null || currentPhrase.getLabel().equals("") ?
                       context.getString(R.string.no_label) :
                       currentPhrase.getLabel().toUpperCase(Locale.getDefault());

        String transcription = currentPhrase.getTranscription() == null || currentPhrase.getTranscription().equals("") ? "" :
                               (" [" + currentPhrase.getTranscription() + "]");

        String createdDate = currentPhrase.getCollectionAddingDateTime() != null ?
                             new SimpleDateFormat(CREATION_PHRASE_DATE_TEMPLATE, Locale.ENGLISH).format(currentPhrase.getCollectionAddingDateTime()) :
                             context.getString(R.string.unknown_date);

        String lastAccessDate = currentPhrase.getLastAccessDateTime() != null ?
                                new SimpleDateFormat(LAST_ACCESS_DATE_TEMPLATE, Locale.ENGLISH).format(currentPhrase.getLastAccessDateTime()) :
                                context.getString(R.string.never_accessed);

        holder.textViewPhraseAdapterLabel.setText(label);
        holder.textViewPhraseAdapterCreatedDate.setText(createdDate);
        holder.textViewPhraseAdapterAccessDate.setText(lastAccessDate);
        holder.textViewPhraseAdapterMultiplier.setText(String.format(Locale.getDefault(), MULTIPLIER_AND_PROB_FORMAT, currentPhrase.getMultiplier()));
        holder.textViewRecViewQuestion.setText(currentPhrase.getForeignWord() + " - " + currentPhrase.getNativeWord() + transcription );
        holder.textViewPhraseAdapterProb.setText(String.format(Locale.getDefault(), MULTIPLIER_AND_PROB_FORMAT, currentPhrase.getProbabilityFactor()));

        if(!label.equals(context.getString(R.string.no_label))){
            holder.textViewPhraseAdapterLabel.setTextColor(ContextCompat.getColor(context, R.color.colorGreen));
        }
        if(lastAccessDate.equals(context.getString(R.string.never_accessed))){
            holder.textViewPhraseAdapterAccessDate.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
        }
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
