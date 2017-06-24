package com.kuzko.aleksey.guessword.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.kuzko.aleksey.guessword.data.GuesswordRepository;
import com.kuzko.aleksey.guessword.MyApplication;
import com.kuzko.aleksey.guessword.R;
import com.kuzko.aleksey.guessword.data.Phrase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EditActivity extends DrawerActivity implements View.OnClickListener  {

    private PhrasesAdapter phrasesAdapter;
    private FloatingActionButton floatingActionButton;
    private MyApplication application;
    private AlertDialog alertDialog;

    public static class PhrasesAdapter extends RecyclerView.Adapter<PhrasesAdapter.ViewHolder> {

        private List<Phrase> phrases = new ArrayList<>();
        private Context context;
        private final static String CREATION_PHRASE_DATE_TEMPLATE = "dd/MM/y";
        private final static String LAST_ACCESS_DATE_TEMPLATE = "dd/MM/y HH:mm";
        private final static String MULTIPLIER_AND_PROB_FORMAT = "%1$,.2f";

        public PhrasesAdapter(List<Phrase> dataset, Context context) {
            this.phrases = dataset;
            this.context = context;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            TextView textViewPhraseAdapterLabel,
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
        public PhrasesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_phrases_edit_item, parent, false);
            return new PhrasesAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(PhrasesAdapter.ViewHolder holder, int position) {
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

        //24/06/2017
    /*public void add(Phrase addedPhrase){
        phrases.add(addedPhrase);
        notifyDataSetChanged();
    }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        application = (MyApplication) getApplication();
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButtonAddPhrase);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
        initDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        phrasesAdapter = new PhrasesAdapter(GuesswordRepository.getInstance().getAllPhrases(), this);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.phrases_edit_recycler_view);
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(phrasesAdapter);
    }

    private void initDialog() {

        alertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.phrases_add_dialog_layout)
                .setTitle("Add phrase")
                .setPositiveButton(R.string.save_dialog_button, null) //Set to null. We override the onclick
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                final EditText editTextDialogForeignWord = (EditText) alertDialog.findViewById(R.id.editTextDialogForeingWord);
                final EditText editTextDialogNativeWord = (EditText) alertDialog.findViewById(R.id.editTextDialogNativeWord);
                final EditText editTextDialogTranscription = (EditText) alertDialog.findViewById(R.id.editTextDialogTranscription);
                final EditText editTextDialogLabel = (EditText) alertDialog.findViewById(R.id.editTextDialogLabel);

                editTextDialogForeignWord.setText("");
                editTextDialogForeignWord.requestFocus();
                editTextDialogNativeWord.setText("");
                editTextDialogTranscription.setText("");
                editTextDialogLabel.setText("");
                editTextDialogForeignWord.setError(null);
                editTextDialogNativeWord.setError(null);

                Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String givenNativeWord = editTextDialogNativeWord.getText().toString();
                        String givenForeignWord = editTextDialogForeignWord.getText().toString();
                        String givenTranscription = editTextDialogTranscription.getText().toString();
                        String givenLabel = editTextDialogLabel.getText().toString();

                        if(givenForeignWord.equals("")){
                            editTextDialogForeignWord.requestFocus();
                            editTextDialogForeignWord.setError("Enter foreign word");
                        }else if(givenNativeWord.equals("")){
                            editTextDialogNativeWord.requestFocus();
                            editTextDialogNativeWord.setError("Enter native word");
                        }else {
                            Phrase addedPhrase = new Phrase(givenForeignWord, givenNativeWord, givenTranscription,
                                    givenLabel, application.retrieveLoggedUser());
//                    phrasesAdapter.add(addedPhrase);
                            GuesswordRepository.getInstance().createPhrase(addedPhrase);
                            phrasesAdapter.notifyDataSetChanged();
                            dialog.dismiss();
                        }
                    }
                });
            }
        });
    }/*

    private void removeView() {
        if(dialogView.getParent() != null) {
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }
    }*/

    @Override
    public void onClick(View v) {
        
    }
}
