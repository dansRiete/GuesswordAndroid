package com.kuzko.aleksey.guessword;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.kuzko.aleksey.guessword.database.HelperFactory;
import com.kuzko.aleksey.guessword.datamodel.Phrase;
import com.kuzko.aleksey.guessword.utils.PhrasesRecyclerAdapter;

import java.sql.SQLException;

public class EditActivity extends BaseActivity implements View.OnClickListener  {

    private View dialogView;
    private AlertDialog.Builder alertDialog;
    private PhrasesRecyclerAdapter phrasesRecyclerAdapter;
    private FloatingActionButton floatingActionButton;
    private MyApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        application = (MyApplication) getApplication();
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingButtonAddPhrase);
        floatingActionButton.setOnClickListener(v -> {
            removeView();
            alertDialog.show();
        });
        initDialog();
    }

    @Override
    protected void onStart() {
        super.onStart();
        try {
            phrasesRecyclerAdapter = new PhrasesRecyclerAdapter(HelperFactory.getHelper().getPhraseDao().retrieveAll(), this);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Fetching Phrases in onActivityCreated()");
        }

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.phrases_edit_recycler_view);
        mRecyclerView.setHasFixedSize(true);    // If confident of rec.view layout size isn't changed by content
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(phrasesRecyclerAdapter);
    }

    private void initDialog() {
        alertDialog = new AlertDialog.Builder(this);
        dialogView = getLayoutInflater().inflate(R.layout.phrases_add_dialog_layout, null);
        alertDialog.setView(dialogView);
        alertDialog.setPositiveButton(R.string.save_dialog_button, (dialog, which) -> {

            EditText editTextDialogForeingWord = (EditText) dialogView.findViewById(R.id.editTextDialogForeingWord);
            EditText editTextDialogNativeWord = (EditText) dialogView.findViewById(R.id.editTextDialogNativeWord);
            EditText editTextDialogTranscription = (EditText) dialogView.findViewById(R.id.editTextDialogTranscription);
            EditText editTextDialogLabel = (EditText) dialogView.findViewById(R.id.editTextDialogLabel);

            String givenNativeWord = editTextDialogNativeWord.getText().toString();
            String givenForeignWord = editTextDialogForeingWord.getText().toString();
            String givenTranscription = editTextDialogTranscription.getText().toString();
            String givenLabel = editTextDialogLabel.getText().toString();

            Phrase addedPhrase = new Phrase(givenForeignWord, givenNativeWord, givenTranscription, givenLabel, application.retrieveLoggedUser());
            phrasesRecyclerAdapter.add(addedPhrase);
            GuesswordRepository.getInstance().createPhrase(addedPhrase);
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
