package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kuzko.aleksey.guessword.database.HelperFactory;
import com.kuzko.aleksey.guessword.datamodel.Phrase;
import com.kuzko.aleksey.guessword.utils.PhrasesRecyclerAdapter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EditActivity extends BaseActivity implements View.OnClickListener  {

    private View dialogView;
    private AlertDialog.Builder alertDialog;
    private PhrasesRecyclerAdapter phrasesRecyclerAdapter;
    private List<Phrase> phrases = new ArrayList<>();
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(getLocalClassName(), "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        try {
            phrasesRecyclerAdapter = new PhrasesRecyclerAdapter(HelperFactory.getHelper().getPhraseDao().retrieveAll());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fetching Phrases in onActivityCreated()");
        }
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButtonAddPhrase);
        floatingActionButton.setOnClickListener(v -> {
            removeView();
            alertDialog.show();
        });
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.phrases_edit_recycler_view);
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(phrasesRecyclerAdapter);
        initDialog();
    }

    private void initDialog() {
        alertDialog = new AlertDialog.Builder(this);
        dialogView = getLayoutInflater().inflate(R.layout.phrases_add_dialog_layout, null);
        alertDialog.setView(dialogView);
        alertDialog.setPositiveButton("Save", (dialog, which) -> {

            EditText editTextDialogForeingWord = (EditText) dialogView.findViewById(R.id.editTextDialogForeingWord);
            EditText editTextDialogNativeWord = (EditText) dialogView.findViewById(R.id.editTextDialogNativeWord);
            EditText editTextDialogTranscription = (EditText) dialogView.findViewById(R.id.editTextDialogTranscription);
            EditText editTextDialogLabel = (EditText) dialogView.findViewById(R.id.editTextDialogLabel);

            String givenNativeWord = editTextDialogNativeWord.getText().toString();
            String givenForeignWord = editTextDialogForeingWord.getText().toString();
            String givenTranscription = editTextDialogTranscription.getText().toString();
            String givenLabel = editTextDialogLabel.getText().toString();

            Phrase addedPhrase = new Phrase(/*GuesswordRepository.getInstance().giveNewPhraseId(), */givenForeignWord, givenNativeWord, givenTranscription, givenLabel);
            phrasesRecyclerAdapter.add(addedPhrase);
            GuesswordRepository.getInstance().addPhrase(addedPhrase);
            dialog.dismiss();

        });
    }

    private void removeView() {
        if(dialogView.getParent() != null) {
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }
    }

    @Override
    public void onClick(View v) {
        
    }
}
