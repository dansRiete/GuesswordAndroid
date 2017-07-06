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
import static com.google.common.base.Preconditions.checkNotNull;

public class EditActivity extends DrawerActivity implements View.OnClickListener  {

    private PhrasesAdapter phrasesAdapter;
    private FloatingActionButton floatingActionButton;
    private MyApplication application;
    private AlertDialog alertDialog;

    private static class PhrasesAdapter extends RecyclerView.Adapter<PhrasesAdapter.ViewHolder> {

        private List<Phrase> phrases = new ArrayList<>();
        private Context context;

        private PhrasesAdapter(List<Phrase> dataset, Context context) {
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
                    new SimpleDateFormat(context.getString(R.string.creatianDate_template_format), Locale.ENGLISH).format(currentPhrase.getCollectionAddingDateTime()) :
                    context.getString(R.string.unknown_date);

            String lastAccessDate = currentPhrase.getLastAccessDateTime() != null ?
                    new SimpleDateFormat(context.getString(R.string.lastAccessDate_templateFormat), Locale.ENGLISH).format(currentPhrase.getLastAccessDateTime()) :
                    context.getString(R.string.never_accessed);

            holder.textViewPhraseAdapterLabel.setText(label);
            holder.textViewPhraseAdapterCreatedDate.setText(createdDate);
            holder.textViewPhraseAdapterAccessDate.setText(lastAccessDate);
            holder.textViewPhraseAdapterMultiplier.setText(String.format(Locale.getDefault(), context.getString(R.string.multiplier_and_probFactor_format), currentPhrase.getMultiplier()));
            holder.textViewRecViewQuestion.setText(currentPhrase.getForeignWord() + " - " + currentPhrase.getNativeWord() + transcription );
            holder.textViewPhraseAdapterProb.setText(String.format(Locale.getDefault(), context.getString(R.string.multiplier_and_probFactor_format), currentPhrase.getProbabilityFactor()));

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
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(phrasesAdapter);
    }

    private void initDialog() {

        alertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.dialog_addPhrase)
                .setTitle(R.string.dialogTitle_addPhrase)
                .setPositiveButton(R.string.save_dialog_button, null) //Set to null. We override the onclick
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                final EditText editTextDialogForeignWord = checkNotNull((EditText) alertDialog.findViewById(R.id.editTextDialogForeingWord));
                final EditText editTextDialogNativeWord = checkNotNull((EditText) alertDialog.findViewById(R.id.editTextDialogNativeWord));
                final EditText editTextDialogTranscription = checkNotNull((EditText) alertDialog.findViewById(R.id.editTextDialogTranscription));
                final EditText editTextDialogLabel = checkNotNull((EditText) alertDialog.findViewById(R.id.editTextDialogLabel));

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
