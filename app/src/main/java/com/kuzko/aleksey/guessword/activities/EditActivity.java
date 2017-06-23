package com.kuzko.aleksey.guessword.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.kuzko.aleksey.guessword.GuesswordRepository;
import com.kuzko.aleksey.guessword.MyApplication;
import com.kuzko.aleksey.guessword.R;
import com.kuzko.aleksey.guessword.datamodel.Phrase;
import com.kuzko.aleksey.guessword.utils.PhrasesRecyclerAdapter;

public class EditActivity extends DrawerActivity implements View.OnClickListener  {

    private View dialogView;
//    private AlertDialog.Builder alertDialog;
    private PhrasesRecyclerAdapter phrasesRecyclerAdapter;
    private FloatingActionButton floatingActionButton;
    private MyApplication application;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        application = (MyApplication) getApplication();
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButtonAddPhrase);
        floatingActionButton.setOnClickListener(v -> {
//            removeView();
            alertDialog.show();
        });
        initDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        phrasesRecyclerAdapter = new PhrasesRecyclerAdapter(GuesswordRepository.getInstance().getAllPhrases(), this);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.phrases_edit_recycler_view);
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(phrasesRecyclerAdapter);
    }

    private void initDialog() {

        alertDialog = new AlertDialog.Builder(this)
                .setView(R.layout.phrases_add_dialog_layout)
                .setTitle("Add phrase")
                .setPositiveButton(R.string.save_dialog_button, null) //Set to null. We override the onclick
                .create();

        alertDialog.setOnShowListener(dialog -> {

            EditText editTextDialogForeignWord = (EditText) alertDialog.findViewById(R.id.editTextDialogForeingWord);
            EditText editTextDialogNativeWord = (EditText) alertDialog.findViewById(R.id.editTextDialogNativeWord);
            EditText editTextDialogTranscription = (EditText) alertDialog.findViewById(R.id.editTextDialogTranscription);
            EditText editTextDialogLabel = (EditText) alertDialog.findViewById(R.id.editTextDialogLabel);

            editTextDialogForeignWord.setText("");
            editTextDialogForeignWord.requestFocus();
            editTextDialogNativeWord.setText("");
            editTextDialogTranscription.setText("");
            editTextDialogLabel.setText("");
            editTextDialogForeignWord.setError(null);
            editTextDialogNativeWord.setError(null);

            Button positiveButton = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);

            positiveButton.setOnClickListener(view -> {

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
//                    phrasesRecyclerAdapter.add(addedPhrase);
                    GuesswordRepository.getInstance().createPhrase(addedPhrase);
                    phrasesRecyclerAdapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
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
